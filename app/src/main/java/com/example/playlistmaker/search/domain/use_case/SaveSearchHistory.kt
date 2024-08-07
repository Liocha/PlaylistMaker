package com.example.playlistmaker.search.domain.use_case

import com.example.playlistmaker.search.domain.model.Track

interface SaveSearchHistory {
    suspend fun execute(track: Track)
}