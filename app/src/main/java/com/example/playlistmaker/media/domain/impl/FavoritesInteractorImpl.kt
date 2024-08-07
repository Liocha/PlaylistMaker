package com.example.playlistmaker.media.domain.impl

import com.example.playlistmaker.media.domain.db.FavoritesInteractor
import com.example.playlistmaker.media.domain.db.FavoritesRepository
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

class FavoritesInteractorImpl(private val favoritesRepository: FavoritesRepository) :
    FavoritesInteractor {
    override fun favoritesTracks(): Flow<List<Track>> {
        return favoritesRepository.favoritesTracks()
    }

    override suspend fun addFavoriteTrack(track: Track) {
        favoritesRepository.addFavoriteTrack(track)
    }

    override suspend fun removeFavoriteTrack(trackId: String) {
        favoritesRepository.removeFavoriteTrack(trackId)
    }
}