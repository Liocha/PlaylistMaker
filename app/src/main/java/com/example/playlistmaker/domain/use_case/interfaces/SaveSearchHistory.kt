package com.example.playlistmaker.domain.use_case.interfaces

import com.example.playlistmaker.domain.model.Track

interface SaveSearchHistory {
    fun execute(track: Track)
}