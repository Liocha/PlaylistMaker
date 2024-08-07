package com.example.playlistmaker.media.domain.db

import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesInteractor {
    fun favoritesTracks(): Flow<List<Track>>

    suspend fun addFavoriteTrack(track: Track)

    suspend fun removeFavoriteTrack(trackId: String)
}