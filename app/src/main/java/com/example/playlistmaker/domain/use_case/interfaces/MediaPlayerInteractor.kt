package com.example.playlistmaker.domain.use_case.interfaces

import com.example.playlistmaker.domain.api.PlayerListener

interface MediaPlayerInteractor {
    fun setListener(listener: PlayerListener)
    fun preparePlayer(url: String)
    fun startPlayer()
    fun pausePlayer()

    fun release()

    fun getCurrentPosition() : Int
}