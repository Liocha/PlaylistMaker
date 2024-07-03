package com.example.playlistmaker.player.domain.repository

import com.example.playlistmaker.player.domain.model.PlayerState
import kotlinx.coroutines.flow.StateFlow

interface MediaPlayerRepository {
    val stateFlow: StateFlow<PlayerState>

    fun preparePlayer(url: String)
    fun start()
    fun pause()

    fun release()

    fun getCurrentPosition(): Int

}