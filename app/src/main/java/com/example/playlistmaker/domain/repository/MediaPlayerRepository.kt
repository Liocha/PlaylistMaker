package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.api.PlayerListener

interface MediaPlayerRepository {
    fun setListener(listener: PlayerListener)
    fun preparePlayer(url: String)
    fun start()
    fun pause()

    fun release()

    fun getCurrentPosition() : Int
}