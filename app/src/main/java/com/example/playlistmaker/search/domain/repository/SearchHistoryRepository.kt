package com.example.playlistmaker.search.domain.repository

import com.example.playlistmaker.search.domain.model.Track

interface SearchHistoryRepository {
    suspend fun saveSearchHistory(track: Track)
    suspend fun getSearchHistory(): List<Track>
    fun clearSearchHistory()
}