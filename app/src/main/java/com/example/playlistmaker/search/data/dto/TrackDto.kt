package com.example.playlistmaker.search.data.dto

data class TrackDto(
    val artistName: String,
    val artworkUrl100: String,
    val trackName: String,
    val trackTimeMillis: Int,
    val trackId: String,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String
)
