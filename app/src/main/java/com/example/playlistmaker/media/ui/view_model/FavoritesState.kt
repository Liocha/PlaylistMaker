package com.example.playlistmaker.media.ui.view_model

import com.example.playlistmaker.search.domain.model.Track

sealed class FavoritesState {
    data class Content(
        val tracks: List<Track>
    ) : FavoritesState()

    object Empty : FavoritesState()
}
