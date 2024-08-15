package com.example.playlistmaker.media.data

import com.example.playlistmaker.media.data.converters.PlaylistDbConverter
import com.example.playlistmaker.media.data.converters.TrackDbConverter
import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.media.data.db.entity.PlaylistEntity
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.media.domain.repository.PlaylistRepository
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConverter: PlaylistDbConverter,
    private val trackDbConverter: TrackDbConverter

) : PlaylistRepository {
    override suspend fun createPlaylist(playlist: Playlist) {
        val playlistEntity = playlistDbConverter.map(playlist)
        appDatabase.playlistDao().insertPlaylist(playlistEntity)
    }

    override fun getAll(): Flow<List<Playlist>> {
        return appDatabase.playlistDao().getAll()
            .map { playlistsEntity -> convertFromPlaylistEntity(playlistsEntity).reversed() }
    }

    override suspend fun updateTrackIdList(playlistId: Int, trackIdList: String, tracksCount: Int) {
        appDatabase.playlistDao().updateTrackIdList(playlistId, trackIdList, tracksCount)
    }

    override suspend fun addTrack(track: Track) {
        appDatabase.playlistTrackDao().addTrack(trackDbConverter.map(track))
    }

    private fun convertFromPlaylistEntity(playlistsEntity: List<PlaylistEntity>): List<Playlist> {
        return playlistsEntity.map { playlistEntity -> playlistDbConverter.map(playlistEntity) }
    }
}
