package com.example.playlistmaker.creator

import android.content.Context
import com.example.playlistmaker.search.data.repository.impl.TracksRepositoryImpl
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.player.data.repository.impl.MediaPlayerRepositoryImpl
import com.example.playlistmaker.search.data.repository.impl.SearchHistoryRepositoryImpl
import com.example.playlistmaker.player.data.repository.MediaPlayerRepository
import com.example.playlistmaker.search.data.repository.SearchHistoryRepository
import com.example.playlistmaker.search.data.repository.TracksRepository
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
import com.example.playlistmaker.settings.data.SettingsRepository
import com.example.playlistmaker.settings.data.impl.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.sharing.data.ExternalNavigator
import com.example.playlistmaker.sharing.data.ResourceShareProvider
import com.example.playlistmaker.sharing.data.impl.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.data.impl.ResourceShareProviderImpl
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