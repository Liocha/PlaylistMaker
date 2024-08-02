package com.example.playlistmaker.search.data.repository

import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.dto.TracksSearchRequest
import com.example.playlistmaker.search.data.dto.TracksSearchResponse
import com.example.playlistmaker.search.domain.model.Resource
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.domain.repository.TracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val appDatabase: AppDatabase
) : TracksRepository {
    override fun searchTracks(query: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TracksSearchRequest(query))

        when (response.resultCode) {
            200 -> {
                with(response as TracksSearchResponse) {
                    val tracks = results.map {
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
                    val favoriteTrackIds = appDatabase.trackDao().getTracksId()
                    tracks.forEach { track ->
                        if (favoriteTrackIds.contains(track.trackId)) {
                            track.isFavorite = true
                        }
                    }
                    emit(Resource.Success(tracks))
                }
            }

            -1 -> {
                emit(Resource.Error(response.message))
            }

            else -> {
                emit(Resource.Error(response.message))
            }

        }

    }
}