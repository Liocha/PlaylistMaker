package com.example.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.media.domain.interactor.FavoritesInteractor
import com.example.playlistmaker.media.domain.interactor.PlaylistInteractor
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.player.domain.model.PlayerState
import com.example.playlistmaker.player.domain.use_case.MediaPlayerInteractor
import com.example.playlistmaker.search.domain.model.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AudioPlayerViewModel(
    private val track: Track,
    private val playerInteractor: MediaPlayerInteractor,
    private val favoritesInteractor: FavoritesInteractor,
    private val playlistInteractor: PlaylistInteractor,
    private val gson: Gson
) : ViewModel() {

    private val _screenState =
        MutableLiveData<AudioPlayerScreenState>(AudioPlayerScreenState.Loading)
    val screenState: LiveData<AudioPlayerScreenState> = _screenState

    private lateinit var playerState: PlayerState

    private val _currentTrackTime = MutableLiveData<String>()
    val currentTrackTime: LiveData<String> = _currentTrackTime

    private val _playButtonState = MutableLiveData<Int>()
    val playButtonState: LiveData<Int> = _playButtonState

    private val _isFavorite = MutableLiveData(track.isFavorite)
    val isFavorite: LiveData<Boolean> = _isFavorite

    private val _toastMessage = MutableLiveData<Pair<Int, String>>()
    val toastMessage: LiveData<Pair<Int, String>> get() = _toastMessage

    private var timerJob: Job? = null

    private val dataFormat by lazy {
        SimpleDateFormat(
            "mm:ss",
            Locale.getDefault()
        )
    }

    private val _playlists = MutableLiveData<List<Playlist>>(emptyList())
    val playlists: LiveData<List<Playlist>> get() = _playlists

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

        viewModelScope.launch {
            playlistInteractor.getAll().collect { playlists -> _playlists.postValue(playlists) }
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

    fun onFavoriteClicked() {
        val isCurrentlyFavorite = isFavorite.value == true
        _isFavorite.postValue(!isCurrentlyFavorite)

        viewModelScope.launch {
            if (isCurrentlyFavorite) {
                favoritesInteractor.removeFavoriteTrack(track.trackId)
            } else {
                favoritesInteractor.addFavoriteTrack(track)
            }
        }
    }

    fun addTrackToPlaylist(playlist: Playlist) {
        val type = object : TypeToken<MutableList<String>>() {}.type
        val trackIds: MutableList<String> =
            gson.fromJson(playlist.trackIdList, type) ?: mutableListOf()
        if (!trackIds.contains(track.trackId)) {
            trackIds.add(track.trackId)
            val tracksCount = trackIds.count()
            val trackIdList = gson.toJson(trackIds)
            viewModelScope.launch {
                playlistInteractor.updateTrackIdList(playlist.id, trackIdList, tracksCount)
                playlistInteractor.addTrack(track)
                showToast(R.string.track_added_to_playlist, playlist.name)

            }
        } else {
            showToast(R.string.track_already_in_playlist, playlist.name)
        }
    }

    private fun showToast(resourceId: Int, arg: String) {
        _toastMessage.postValue(Pair(resourceId, arg))
    }


}