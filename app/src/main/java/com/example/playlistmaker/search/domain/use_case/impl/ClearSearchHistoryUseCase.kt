package com.example.playlistmaker.search.domain.use_case.impl

import com.example.playlistmaker.search.domain.repository.SearchHistoryRepository
import com.example.playlistmaker.search.domain.use_case.ClearSearchHistory

class ClearSearchHistoryUseCase(private val repository: SearchHistoryRepository):
    ClearSearchHistory {
    override fun execute() {
        repository.clearSearchHistory()
    }
}