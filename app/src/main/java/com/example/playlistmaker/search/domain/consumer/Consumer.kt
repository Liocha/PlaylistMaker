package com.example.playlistmaker.search.domain.consumer

interface Consumer {
    fun consume(data: ConsumerData)
}