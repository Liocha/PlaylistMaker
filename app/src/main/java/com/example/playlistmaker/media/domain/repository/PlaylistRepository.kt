package com.example.playlistmaker.media.domain.repository

import android.net.Uri
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun createPlaylist(playlist: Playlist)
    fun getAll(): Flow<List<Playlist>>
    suspend fun updateTrackIdList(playlistId: Int, trackIdList: String, tracksCount: Int)
    suspend fun addTrack(track: Track)
    suspend fun saveImageToPrivateStorage(uri: Uri): Uri?
}