package com.example.playlistmaker.player.domain.api

import com.example.playlistmaker.player.domain.model.PlayerState

interface PlayerListener {
    fun onStateChange(state: PlayerState)
}