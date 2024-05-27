package com.example.playlistmaker.search.domain.use_case

import com.example.playlistmaker.search.domain.model.Track

interface GetSearchHistory {
    fun execute(): List<Track>
}