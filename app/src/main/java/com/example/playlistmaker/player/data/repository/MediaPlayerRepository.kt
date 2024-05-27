package com.example.playlistmaker.player.data.repository

import com.example.playlistmaker.player.domain.api.PlayerListener

interface MediaPlayerRepository {
    fun setListener(listener: PlayerListener)
    fun preparePlayer(url: String)
    fun start()
    fun pause()

    fun release()

    fun getCurrentPosition() : Int
}