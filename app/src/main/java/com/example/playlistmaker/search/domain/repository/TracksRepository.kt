package com.example.playlistmaker.search.domain.repository

import com.example.playlistmaker.search.domain.model.Resource
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow


interface TracksRepository {
    fun searchTracks(query: String): Flow<Resource<List<Track>>>
}