package com.example.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.player.domain.model.PlayerState
import com.example.playlistmaker.player.domain.use_case.MediaPlayerInteractor
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AudioPlayerViewModel(
    private val track: Track,
    private val playerInteractor: MediaPlayerInteractor
) : ViewModel() {

    private val _screenState =
        MutableLiveData<AudioPlayerScreenState>(AudioPlayerScreenState.Loading)
    val screenState: LiveData<AudioPlayerScreenState> = _screenState

    private lateinit var playerState: PlayerState

    private val _currentTrackTime = MutableLiveData<String>()
    val currentTrackTime: LiveData<String> = _currentTrackTime

    private val _playButtonState = MutableLiveData<Int>()
    val playButtonState: LiveData<Int> = _playButtonState

    private var timerJob: Job? = null

    private val dataFormat by lazy {
        SimpleDateFormat(
            "mm:ss",
            Locale.getDefault()
        )
    }

    init {
        setupMediaPlayback()
        loadingTrack()
        viewModelScope.launch {
            playerInteractor.playerStateFlow().collectLatest { latestPlayerState ->
                playerState = latestPlayerState
                updatePlaybackUi()
                if (playerState == PlayerState.PLAYBACK_COMPLETED) {
                    timerJob?.cancel()
                    _currentTrackTime.postValue(dataFormat.format(Date(0)))
                }
            }
        }
    }

    companion object {
        const val TRACK_POSITION_UPDATE_INTERVAL_MS = 300L
    }

    private fun loadingTrack() {
        _screenState.value = AudioPlayerScreenState.Success(track)
    }


    private fun currentPositionSetter() {
        timerJob = viewModelScope.launch {
            while (playerState == PlayerState.PLAYING) {
                delay(TRACK_POSITION_UPDATE_INTERVAL_MS)
                _currentTrackTime.postValue(
                    dataFormat.format(playerInteractor.getCurrentPosition())
                )
            }
        }
    }

    private fun setupMediaPlayback() {
        track.previewUrl?.let { playerInteractor.preparePlayer(it) }
    }


    fun playbackControl() {
        handlePlaybackState()
    }

    private fun handlePlaybackState() {
        when (playerState) {
            PlayerState.PLAYING -> {
                playerInteractor.pausePlayer()
                timerJob?.cancel()
            }

            PlayerState.PAUSED, PlayerState.PREPARED, PlayerState.PLAYBACK_COMPLETED -> {
                playerInteractor.startPlayer()
                currentPositionSetter()
            }

            PlayerState.DEFAULT -> {}

        }
    }

    private fun updatePlaybackUi() {
        val iconResource = when (playerState) {
            PlayerState.PLAYING -> R.drawable.pause_button
            PlayerState.DEFAULT -> return
            PlayerState.PREPARED, PlayerState.PAUSED, PlayerState.PLAYBACK_COMPLETED -> R.drawable.play_button
        }
        _playButtonState.postValue(iconResource)
    }

    public override fun onCleared() {
        super.onCleared()
        playerInteractor.release()
    }

    fun handleActivityPause() {
        playerInteractor.pausePlayer()
        updatePlaybackUi()
    }

}