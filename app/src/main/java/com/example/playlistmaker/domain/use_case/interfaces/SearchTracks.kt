package com.example.playlistmaker.domain.use_case.interfaces

import com.example.playlistmaker.domain.consumer.Consumer

interface SearchTracks {
    fun execute(query: String, consumer: Consumer)
}