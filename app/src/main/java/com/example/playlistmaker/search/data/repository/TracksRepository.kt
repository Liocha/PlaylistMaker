package com.example.playlistmaker.search.data.repository

import com.example.playlistmaker.search.domain.model.Resource
import com.example.playlistmaker.search.domain.model.Track

interface TracksRepository {
    fun searchTracks(query: String): Resource<List<Track>>
}