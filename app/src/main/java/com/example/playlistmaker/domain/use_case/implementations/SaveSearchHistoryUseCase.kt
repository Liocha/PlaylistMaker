package com.example.playlistmaker.domain.use_case.implementations

import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.SearchHistoryRepository
import com.example.playlistmaker.domain.use_case.interfaces.SaveSearchHistory

class SaveSearchHistoryUseCase(private val repository: SearchHistoryRepository) : SaveSearchHistory{
    override fun execute(track: Track) {
        repository.saveSearchHistory(track)
    }
}