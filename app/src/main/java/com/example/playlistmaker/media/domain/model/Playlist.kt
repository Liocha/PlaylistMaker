package com.example.playlistmaker.media.domain.model

import android.net.Uri

data class Playlist(
    val id: Int = 0,
    val name: String,
    val description: String = "",
    val pathCover: Uri? = null,
    val trackIdList: String = "",
    val tracksCount: Int = 0
)
