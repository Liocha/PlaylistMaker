package com.example.playlistmaker.media.domain.repository

import android.net.Uri
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun createPlaylist(playlist: Playlist)
    fun getAll(): Flow<List<Playlist>>
    suspend fun updateTrackIdList(playlistId: Int, trackIdList: List<String>)
    suspend fun addTrack(track: Track)
    suspend fun saveImageToPrivateStorage(uri: Uri): Uri
    suspend fun getPlaylistById(id: Int): Playlist
    suspend fun getAllTracksByIds(trackIdList: String): List<Track>?
    suspend fun removeTrackFromPlaylist(playlistId: Int, trackId: String)
    suspend fun sharePlaylist(playlistId: Int)
    suspend fun deletePlaylist(playlistId: Int)
    suspend fun updatePlaylistData(id: Int, name: String?, description: String?, imagePath: Uri?)
}
