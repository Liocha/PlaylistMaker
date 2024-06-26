package com.example.playlistmaker.player.data.repository

import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.api.PlayerListener
import com.example.playlistmaker.player.domain.model.PlayerState
import com.example.playlistmaker.player.domain.repository.MediaPlayerRepository

class MediaPlayerRepositoryImpl(private var mediaPlayer: MediaPlayer) : MediaPlayerRepository {

    private lateinit var listener: PlayerListener

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