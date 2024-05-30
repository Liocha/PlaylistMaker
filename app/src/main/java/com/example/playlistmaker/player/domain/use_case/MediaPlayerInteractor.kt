package com.example.playlistmaker.player.domain.use_case

import com.example.playlistmaker.player.domain.api.PlayerListener

interface MediaPlayerInteractor {
    fun setListener(listener: PlayerListener)
    fun preparePlayer(url: String)
    fun startPlayer()
    fun pausePlayer()

    fun release()

    fun getCurrentPosition() : Int
}