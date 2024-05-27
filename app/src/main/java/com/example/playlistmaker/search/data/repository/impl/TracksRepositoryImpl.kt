package com.example.playlistmaker.search.data.repository.impl

import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.dto.TracksSearchRequest
import com.example.playlistmaker.search.data.dto.TracksSearchResponse
import com.example.playlistmaker.search.domain.model.Resource
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.data.repository.TracksRepository

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {
    override fun searchTracks(query: String): Resource<List<Track>> {
        val response = networkClient.doRequest(TracksSearchRequest(query))
        if (response.resultCode == 200) {
            val tracks = (response as TracksSearchResponse).results.map {
                Track(
                    it.artistName,
                    it.artworkUrl100,
                    it.trackName,
                    it.trackTimeMillis,
                    it.trackId,
                    it.collectionName,
                    it.releaseDate,
                    it.primaryGenreName,
                    it.country,
                    it.previewUrl
                )
            }
            return Resource.Success(tracks)
        } else if (response.resultCode == -1) {
            return Resource.NetworkError(response.message)
        } else {
            return Resource.Error(response.message)
        }
    }

}