package com.example.playlistmaker.di

import com.example.playlistmaker.media.ui.view_model.CreatePlaylistViewModel
import com.example.playlistmaker.media.ui.view_model.FavoriteTracksViewModel
import com.example.playlistmaker.media.ui.view_model.PlaylistCollectionViewModel
import com.example.playlistmaker.player.ui.view_model.AudioPlayerViewModel
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.view_model.SearchViewModel
import com.example.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        SearchViewModel(androidApplication(), get(), get(), get(), get())
    }

    viewModel {
        SettingsViewModel(
            androidApplication(),
            get(),
            get(),
        )
    }

    viewModel { (track: Track) ->
        AudioPlayerViewModel(
            track,
            get(),
            get(),
            get(),
            get()
        )
    }

    viewModel {
        FavoriteTracksViewModel(get())
    }

    viewModel {
        CreatePlaylistViewModel(get())
    }

    viewModel {
        PlaylistCollectionViewModel(get())
    }
}