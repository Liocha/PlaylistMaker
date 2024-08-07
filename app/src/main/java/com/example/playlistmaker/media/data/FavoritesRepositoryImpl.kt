package com.example.playlistmaker.media.data

import com.example.playlistmaker.media.data.converters.TrackDbConverter
import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.media.data.db.entity.TrackEntity
import com.example.playlistmaker.media.domain.db.FavoritesRepository
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoritesRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConverter: TrackDbConverter
) : FavoritesRepository {

    override fun favoritesTracks(): Flow<List<Track>> {
        return appDatabase.trackDao().getTracks().map { trackEntities ->
            convertFromTRackEntity(trackEntities).reversed()
        }
    }

    override suspend fun addFavoriteTrack(track: Track) {
        val trackEntity = trackDbConverter.map(track)
        appDatabase.trackDao().insertTrack(trackEntity)
    }

    override suspend fun removeFavoriteTrack(trackId: String) {
        appDatabase.trackDao().deleteTrackById(trackId)
    }

    fun convertFromTRackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConverter.map(track) }
    }
}