package com.example.playlistmaker.media.ui.view_model

sealed class NavigationState {
    object NavigateBack : NavigationState()
    object ShowDialogBeforeNavigateBack : NavigationState()
    data class PlaylistCreated(
        val playlistName: String
    ) : NavigationState()

    object DefaultNothing : NavigationState()
}