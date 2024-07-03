package com.example.playlistmaker.player.domain.use_case.impl

import com.example.playlistmaker.player.domain.model.PlayerState
import com.example.playlistmaker.player.domain.repository.MediaPlayerRepository
import com.example.playlistmaker.player.domain.use_case.MediaPlayerInteractor
import kotlinx.coroutines.flow.StateFlow

class MediaPlayerInteractorImpi(private val repository: MediaPlayerRepository) :
    MediaPlayerInteractor {


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

    override fun getCurrentPosition(): Int {
        return repository.getCurrentPosition()
    }

    override fun playerStateFlow(): StateFlow<PlayerState> {
        return repository.stateFlow
    }

}