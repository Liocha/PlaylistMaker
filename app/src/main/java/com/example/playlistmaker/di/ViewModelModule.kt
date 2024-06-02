package com.example.playlistmaker.di

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
        )
    }
}