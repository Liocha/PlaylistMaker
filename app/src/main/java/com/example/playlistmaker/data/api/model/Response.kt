package com.example.playlistmaker.data.api.model

data class Response(
    val resultCount: Int,
    val results: List<Track>
)