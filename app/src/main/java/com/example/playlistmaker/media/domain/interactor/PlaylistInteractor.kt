package com.example.playlistmaker.media.domain.interactor

import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun cretePlaylist(playlist: Playlist)
    fun getAll(): Flow<List<Playlist>>
    suspend fun updateTrackIdList(playlistId: Int, trackIdList: String, tracksCount: Int)
    suspend fun addTrack(track: Track)
}