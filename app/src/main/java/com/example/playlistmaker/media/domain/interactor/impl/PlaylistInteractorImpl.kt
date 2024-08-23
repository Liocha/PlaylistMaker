package com.example.playlistmaker.media.domain.interactor.impl

import android.net.Uri
import com.example.playlistmaker.media.domain.interactor.PlaylistInteractor
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.media.domain.repository.PlaylistRepository
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(private val repository: PlaylistRepository) : PlaylistInteractor {
    override suspend fun cretePlaylist(playlist: Playlist) {
        repository.createPlaylist(playlist)
    }

    override fun getAll(): Flow<List<Playlist>> {
        return repository.getAll()
    }

    override suspend fun updateTrackIdList(playlistId: Int, trackIdList: List<String>) {
        repository.updateTrackIdList(playlistId, trackIdList)
    }

    override suspend fun addTrack(track: Track) {
        repository.addTrack(track)
    }

    override suspend fun saveImageToPrivateStorage(uri: Uri): Uri {
        return repository.saveImageToPrivateStorage(uri)
    }

    override suspend fun getPlaylistByid(id: Int): Playlist {
        return repository.getPlaylistByid(id)
    }

    override suspend fun getAllTracksByIds(trackIdList: String): List<Track>? {
        return repository.getAllTracksByIds(trackIdList)
    }

    override suspend fun removeTrackFromPlaylist(playlistId: Int, trackId: String) {
        repository.removeTrackFromPlaylist(playlistId, trackId)
    }

    override suspend fun sharePlaylist(playlistId: Int) {
        repository.sharePlaylist(playlistId)
    }

    override suspend fun deletePlaylist(playlistId: Int) {
        repository.deletePlaylist(playlistId)
    }

    override suspend fun updatePlaylistData(
        id: Int,
        name: String?,
        description: String?,
        imagePath: Uri?
    ) {
        repository.updatePlaylistData(
            id = id,
            name = name,
            description = description,
            imagePath = imagePath
        )
    }
}
