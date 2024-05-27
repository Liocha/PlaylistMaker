package com.example.playlistmaker.player.domain.use_case.impl

import com.example.playlistmaker.player.domain.use_case.MediaPlayerInteractor
import com.example.playlistmaker.player.domain.api.PlayerListener
import com.example.playlistmaker.player.data.repository.MediaPlayerRepository

class MediaPlayerInteractorImpi(private val repository: MediaPlayerRepository) :
    MediaPlayerInteractor
{
    override fun setListener(listener: PlayerListener) {
        repository.setListener(listener)
    }
    override fun preparePlayer(url: String) {
        repository.preparePlayer(url)
    }

    override fun startPlayer() {
        repository.start()
    }

    override fun pausePlayer() {
        repository.pause()
    }

    override fun release() {
        repository.release()
    }

    override fun getCurrentPosition() : Int {
        return repository.getCurrentPosition()
    }
}