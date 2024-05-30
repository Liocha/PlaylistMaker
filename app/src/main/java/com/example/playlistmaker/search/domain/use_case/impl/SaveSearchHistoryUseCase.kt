package com.example.playlistmaker.search.domain.use_case.impl

import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.domain.repository.SearchHistoryRepository
import com.example.playlistmaker.search.domain.use_case.SaveSearchHistory

class SaveSearchHistoryUseCase(private val repository: SearchHistoryRepository) :
    SaveSearchHistory {
    override fun execute(track: Track) {
        repository.saveSearchHistory(track)
    }
}