package com.example.playlistmaker.player.domain.use_case

import com.example.playlistmaker.player.domain.model.PlayerState
import kotlinx.coroutines.flow.StateFlow

interface MediaPlayerInteractor {
    fun preparePlayer(url: String)
    fun startPlayer()
    fun pausePlayer()

    fun release()

    fun getCurrentPosition(): Int

    fun playerStateFlow(): StateFlow<PlayerState>
}