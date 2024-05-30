package com.example.playlistmaker.player.ui.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.R
import com.example.playlistmaker.player.domain.api.PlayerListener
import com.example.playlistmaker.player.domain.model.PlayerState
import com.example.playlistmaker.player.domain.use_case.MediaPlayerInteractor
import com.example.playlistmaker.search.domain.model.Track
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

    private var playerState: PlayerState = PlayerState.DEFAULT
    private val handler = Handler(Looper.getMainLooper())

    private val _currentTrackTime = MutableLiveData<String>()
    val currentTrackTime: LiveData<String> = _currentTrackTime

    private val _playButtonState = MutableLiveData<Int>()
    val playButtonState: LiveData<Int> = _playButtonState

    private val dataFormat by lazy {
        SimpleDateFormat(
            "mm:ss",
            Locale.getDefault()
        )
    }

    init {
        setupMediaPlayback()
        loadingTrack()
    }

    companion object {
        const val TRACK_POSITION_UPDATE_INTERVAL_MS = 200L
        fun getViewModelFactory(
            track: Track,
            playerInteractor: MediaPlayerInteractor
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                AudioPlayerViewModel(
                    track,
                    playerInteractor,
                )
            }
        }
    }

    private fun loadingTrack() {
        _screenState.value = AudioPlayerScreenState.Success(track)
    }

    private val currentPositionSetter = object : Runnable {
        override fun run() {
            handler.postDelayed(this, TRACK_POSITION_UPDATE_INTERVAL_MS)
            if (playerState == PlayerState.PLAYING) {
                _currentTrackTime.postValue(
                    dataFormat.format(playerInteractor.getCurrentPosition())
                )
            }
        }
    }

    private fun setupMediaPlayback() {
        playerInteractor.preparePlayer(track.previewUrl)
        playerInteractor.setListener(object : PlayerListener {
            override fun onStateChange(state: PlayerState) {
                playerState = state
                if (state == PlayerState.PLAYBACK_COMPLETED) {
                    handler.removeCallbacks(currentPositionSetter)
                    _currentTrackTime.postValue(dataFormat.format(Date(0)))
                }
                updatePlaybackUi()
            }
        })

    }


    fun playbackControl() {
        handlePlaybackState()
    }

    private fun handlePlaybackState() {
        when (playerState) {
            PlayerState.PLAYING -> {
                playerInteractor.pausePlayer()
                handler.removeCallbacks(currentPositionSetter)
            }

            PlayerState.PAUSED, PlayerState.PREPARED, PlayerState.PLAYBACK_COMPLETED -> {
                playerInteractor.startPlayer()
                handler.postDelayed(currentPositionSetter, TRACK_POSITION_UPDATE_INTERVAL_MS)
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
        handler.removeCallbacks(currentPositionSetter)
    }

}