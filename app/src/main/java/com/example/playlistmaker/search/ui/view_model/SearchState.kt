package com.example.playlistmaker.search.ui.view_model

import com.example.playlistmaker.search.domain.model.Track

sealed class SearchState {
    object Loadind : SearchState()
    data class Content(val tracks: List<Track>) : SearchState()
    object Empty : SearchState()
    data class Error(val message: String) : SearchState()
}