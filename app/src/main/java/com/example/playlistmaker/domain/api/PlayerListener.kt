package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.model.PlayerState

interface PlayerListener {
    fun onStateChange(state: PlayerState)
}