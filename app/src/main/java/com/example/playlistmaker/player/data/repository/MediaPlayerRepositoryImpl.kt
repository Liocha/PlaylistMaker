package com.example.playlistmaker.player.data.repository

import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.model.PlayerState
import com.example.playlistmaker.player.domain.repository.MediaPlayerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MediaPlayerRepositoryImpl(private var mediaPlayer: MediaPlayer) : MediaPlayerRepository {

    private val _stateFlow = MutableStateFlow(PlayerState.DEFAULT)
    override val stateFlow = _stateFlow.asStateFlow()

    override fun preparePlayer(url: String) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            _stateFlow.value = PlayerState.PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            _stateFlow.value = PlayerState.PLAYBACK_COMPLETED
        }
    }


    override fun start() {
        mediaPlayer.start()
        _stateFlow.value = PlayerState.PLAYING
    }

    override fun pause() {
        mediaPlayer.pause()
        _stateFlow.value = PlayerState.PAUSED
    }

    override fun release() {
        mediaPlayer.release()
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

}