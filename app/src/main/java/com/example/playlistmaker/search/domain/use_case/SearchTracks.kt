package com.example.playlistmaker.search.domain.use_case

import com.example.playlistmaker.search.domain.consumer.Consumer

interface SearchTracks {
    fun execute(query: String, consumer: Consumer)
}