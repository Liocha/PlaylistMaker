package com.example.playlistmaker.domain.consumer

interface Consumer {
    fun consume(data: ConsumerData)
}