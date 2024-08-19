package com.example.playlistmaker.di

import com.example.playlistmaker.media.domain.interactor.FavoritesInteractor
import com.example.playlistmaker.media.domain.interactor.PlaylistInteractor
import com.example.playlistmaker.media.domain.interactor.impl.FavoritesInteractorImpl
import com.example.playlistmaker.media.domain.interactor.impl.PlaylistInteractorImpl
import com.example.playlistmaker.player.domain.use_case.MediaPlayerInteractor
import com.example.playlistmaker.player.domain.use_case.impl.MediaPlayerInteractorImpi
import com.example.playlistmaker.search.domain.use_case.ClearSearchHistory
import com.example.playlistmaker.search.domain.use_case.GetSearchHistory
import com.example.playlistmaker.search.domain.use_case.SaveSearchHistory
import com.example.playlistmaker.search.domain.use_case.SearchTracks
import com.example.playlistmaker.search.domain.use_case.impl.ClearSearchHistoryUseCase
import com.example.playlistmaker.search.domain.use_case.impl.GetSearchHistoryUseCase
import com.example.playlistmaker.search.domain.use_case.impl.SaveSearchHistoryUseCase
import com.example.playlistmaker.search.domain.use_case.impl.SearchTracksUseCase
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.sharing.domain.SharingInteractor
import com.example.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import org.koin.dsl.module

val interactorModule = module {
    factory<SearchTracks> {
        SearchTracksUseCase(get())
    }

    factory<SettingsInteractor> {
        SettingsInteractorImpl(get())
    }

    factory<SharingInteractor> {
        SharingInteractorImpl(get(), get())
    }

    factory<MediaPlayerInteractor> {
        MediaPlayerInteractorImpi(get())
    }

    factory<ClearSearchHistory> {
        ClearSearchHistoryUseCase(get())
    }

    factory<SaveSearchHistory> {
        SaveSearchHistoryUseCase(get())
    }

    factory<GetSearchHistory> {
        GetSearchHistoryUseCase(get())
    }

    factory<FavoritesInteractor> {
        FavoritesInteractorImpl(get())
    }
    factory<PlaylistInteractor> {
        PlaylistInteractorImpl(get())
    }

}