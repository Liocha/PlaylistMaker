package com.example.playlistmaker.search.domain.use_case.impl

import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.data.repository.SearchHistoryRepository
import com.example.playlistmaker.search.domain.use_case.GetSearchHistory

class GetSearchHistoryUseCase (private val repository: SearchHistoryRepository) : GetSearchHistory {
    override fun execute(): List<Track> {
       return  repository.getSearchHistory()
    }
}