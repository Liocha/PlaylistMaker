package com.example.playlistmaker.creator

import android.content.Context
import com.example.playlistmaker.search.data.repository.TracksRepositoryImpl
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.player.data.repository.MediaPlayerRepositoryImpl
import com.example.playlistmaker.search.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.player.domain.repository.MediaPlayerRepository
import com.example.playlistmaker.search.domain.repository.SearchHistoryRepository
import com.example.playlistmaker.search.domain.repository.TracksRepository
import com.example.playlistmaker.search.domain.use_case.impl.ClearSearchHistoryUseCase
import com.example.playlistmaker.search.domain.use_case.impl.GetSearchHistoryUseCase
import com.example.playlistmaker.player.domain.use_case.MediaPlayerInteractor
import com.example.playlistmaker.player.domain.use_case.impl.MediaPlayerInteractorImpi
import com.example.playlistmaker.search.domain.use_case.impl.SaveSearchHistoryUseCase
import com.example.playlistmaker.search.domain.use_case.impl.SearchTracksUseCase
import com.example.playlistmaker.search.domain.use_case.ClearSearchHistory
import com.example.playlistmaker.search.domain.use_case.GetSearchHistory
import com.example.playlistmaker.search.domain.use_case.SaveSearchHistory
import com.example.playlistmaker.search.domain.use_case.SearchTracks
import com.example.playlistmaker.settings.domain.repository.SettingsRepository
import com.example.playlistmaker.settings.data.repository.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.sharing.domain.repository.ExternalNavigator
import com.example.playlistmaker.sharing.domain.repository.ResourceShareProvider
import com.example.playlistmaker.sharing.data.repository.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.data.repository.ResourceShareProviderImpl
import com.example.playlistmaker.sharing.domain.SharingInteractor
import com.example.playlistmaker.sharing.domain.impl.SharingInteractorImpl

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

    private fun provideResourceShareProvider(context: Context) : ResourceShareProvider {
        return ResourceShareProviderImpl(context)
    }

    private fun provideExternalNavigator(context: Context) : ExternalNavigator {
        return ExternalNavigatorImpl(context)
    }

    private fun provideSettingsRepository(context: Context): SettingsRepository {
        return SettingsRepositoryImpl(context)
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

    fun provideSharingInteractor(context: Context): SharingInteractor {
        return SharingInteractorImpl(provideExternalNavigator(context), provideResourceShareProvider(context))
    }

    fun provideSettingsInteractor(context: Context): SettingsInteractor {
        return SettingsInteractorImpl(provideSettingsRepository(context))
    }

}