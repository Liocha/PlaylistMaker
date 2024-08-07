package com.example.playlistmaker.search.domain.use_case

import com.example.playlistmaker.search.domain.model.Track

interface GetSearchHistory {
    suspend fun execute(): List<Track>
}