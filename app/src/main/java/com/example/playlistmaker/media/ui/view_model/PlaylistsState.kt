package com.example.playlistmaker.media.ui.view_model

import com.example.playlistmaker.media.domain.model.Playlist

sealed class PlaylistsState {

    data class Content(val playlists: List<Playlist>) : PlaylistsState()
    object Empty : PlaylistsState()
}