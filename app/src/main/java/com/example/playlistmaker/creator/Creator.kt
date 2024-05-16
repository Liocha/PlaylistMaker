package com.example.playlistmaker.creator

import android.content.Context
import com.example.playlistmaker.data.repository.TracksRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.repository.MediaPlayerRepositoryImpl
import com.example.playlistmaker.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.domain.repository.MediaPlayerRepository
import com.example.playlistmaker.domain.repository.SearchHistoryRepository
import com.example.playlistmaker.domain.repository.TracksRepository
import com.example.playlistmaker.domain.use_case.implementations.ClearSearchHistoryUseCase
import com.example.playlistmaker.domain.use_case.implementations.GetSearchHistoryUseCase
import com.example.playlistmaker.domain.use_case.interfaces.MediaPlayerInteractor
import com.example.playlistmaker.domain.use_case.implementations.MediaPlayerInteractorImpi
import com.example.playlistmaker.domain.use_case.implementations.SaveSearchHistoryUseCase
import com.example.playlistmaker.domain.use_case.implementations.SearchTracksUseCase
import com.example.playlistmaker.domain.use_case.interfaces.ClearSearchHistory
import com.example.playlistmaker.domain.use_case.interfaces.GetSearchHistory
import com.example.playlistmaker.domain.use_case.interfaces.SaveSearchHistory
import com.example.playlistmaker.domain.use_case.interfaces.SearchTracks

object Creator {
    private fun provideTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }

    private fun provideSearchHistoryRepository(context: Context): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(context)
    }

    private fun provideMediaPlayerRepository(): MediaPlayerRepository {
        return MediaPlayerRepositoryImpl()
    }

    fun provideSearchTracksUseCase(): SearchTracks {
        return SearchTracksUseCase(provideTracksRepository())
    }

    fun provideGetSearchHistoryUseCase(context: Context): GetSearchHistory {
        return GetSearchHistoryUseCase(provideSearchHistoryRepository(context))
    }

    fun provideSaveSearchHistoryUseCase(context: Context): SaveSearchHistory {
        return SaveSearchHistoryUseCase(provideSearchHistoryRepository(context))
    }

    fun provideClearSearchHistoryUseCase(context: Context): ClearSearchHistory {
        return ClearSearchHistoryUseCase(provideSearchHistoryRepository(context))
    }

    fun provideMediaPLayerInteractor(): MediaPlayerInteractor {
        return MediaPlayerInteractorImpi(provideMediaPlayerRepository())
    }
}