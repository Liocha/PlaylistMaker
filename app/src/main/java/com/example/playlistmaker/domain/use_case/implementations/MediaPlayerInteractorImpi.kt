package com.example.playlistmaker.domain.use_case.implementations

import com.example.playlistmaker.domain.use_case.interfaces.MediaPlayerInteractor
import com.example.playlistmaker.domain.api.PlayerListener
import com.example.playlistmaker.domain.repository.MediaPlayerRepository

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