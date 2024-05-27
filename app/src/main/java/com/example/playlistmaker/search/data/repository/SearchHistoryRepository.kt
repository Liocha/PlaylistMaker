package com.example.playlistmaker.search.data.repository

import com.example.playlistmaker.search.domain.model.Track

interface SearchHistoryRepository {
    fun saveSearchHistory(track: Track)
    fun getSearchHistory(): List<Track>
    fun clearSearchHistory()
}