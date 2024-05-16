package com.example.playlistmaker.data.repository

import android.media.MediaPlayer
import com.example.playlistmaker.domain.api.PlayerListener
import com.example.playlistmaker.domain.model.PlayerState
import com.example.playlistmaker.domain.repository.MediaPlayerRepository

class MediaPlayerRepositoryImpl : MediaPlayerRepository {


    private lateinit var listener: PlayerListener

    private var mediaPlayer = MediaPlayer()

    override fun setListener(listener: PlayerListener) {
        this.listener = listener
    }

    override fun preparePlayer(url: String) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            listener.onStateChange(state = PlayerState.PREPARED)
        }
        mediaPlayer.setOnCompletionListener {
            listener.onStateChange(state = PlayerState.PLAYBACK_COMPLETED)
        }
    }


    override fun start() {
        mediaPlayer.start()
        listener.onStateChange(state = PlayerState.PLAYING)
    }

    override fun pause() {
        mediaPlayer.pause()
        listener.onStateChange(state = PlayerState.PAUSED)
    }

    override fun release() {
        mediaPlayer.release()
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }
}