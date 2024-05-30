package com.example.playlistmaker.player.ui.view_model

import com.example.playlistmaker.search.domain.model.Track

sealed class AudioPlayerScreenState {
    object Loading: AudioPlayerScreenState()
    data class Success(val track: Track): AudioPlayerScreenState()
    data class Error(val message: String): AudioPlayerScreenState()
}