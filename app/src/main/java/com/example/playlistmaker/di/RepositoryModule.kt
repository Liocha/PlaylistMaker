package com.example.playlistmaker.di

import com.example.playlistmaker.player.data.repository.MediaPlayerRepositoryImpl
import com.example.playlistmaker.player.domain.repository.MediaPlayerRepository
import com.example.playlistmaker.search.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.search.data.repository.TracksRepositoryImpl
import com.example.playlistmaker.search.domain.repository.SearchHistoryRepository
import com.example.playlistmaker.search.domain.repository.TracksRepository
import com.example.playlistmaker.settings.data.repository.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.repository.SettingsRepository
import com.example.playlistmaker.sharing.data.repository.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.data.repository.ResourceShareProviderImpl
import com.example.playlistmaker.sharing.domain.repository.ExternalNavigator
import com.example.playlistmaker.sharing.domain.repository.ResourceShareProvider
import org.koin.dsl.module


val repositoryModule = module {
    single<TracksRepository> {
        TracksRepositoryImpl(get())
    }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get(), get())
    }

    factory<MediaPlayerRepository> {
        MediaPlayerRepositoryImpl(get())
    }

    single<ResourceShareProvider> {
        ResourceShareProviderImpl(get())
    }

    single<ExternalNavigator> {
        ExternalNavigatorImpl(get())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }


}