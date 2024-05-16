package com.example.playlistmaker.domain.use_case.implementations

import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.SearchHistoryRepository
import com.example.playlistmaker.domain.use_case.interfaces.GetSearchHistory

class GetSearchHistoryUseCase (private val repository: SearchHistoryRepository) : GetSearchHistory {
    override fun execute(): List<Track> {
       return  repository.getSearchHistory()
    }
}