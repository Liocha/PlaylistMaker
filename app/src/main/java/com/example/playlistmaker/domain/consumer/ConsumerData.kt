package com.example.playlistmaker.domain.consumer

import com.example.playlistmaker.domain.model.Track

sealed interface ConsumerData {
    data class Data(val data: List<Track>) : ConsumerData
    data class Error(val message: String) : ConsumerData
    data class NetworkError(val message: String) : ConsumerData
}