package com.example.playlistmaker.domain.use_case.implementations

import com.example.playlistmaker.domain.repository.SearchHistoryRepository
import com.example.playlistmaker.domain.use_case.interfaces.ClearSearchHistory

class ClearSearchHistoryUseCase(private val repository: SearchHistoryRepository):
    ClearSearchHistory {
    override fun execute() {
        repository.clearSearchHistory()
    }
}