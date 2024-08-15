package com.example.playlistmaker.media.domain.model

data class Playlist(
    val id: Int = 0,
    val name: String,
    val description: String = "",
    val pathCover: String = "",
    val trackIdList: String = "",
    val tracksCount: Int = 0
)
