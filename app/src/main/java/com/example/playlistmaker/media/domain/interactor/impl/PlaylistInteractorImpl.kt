package com.example.playlistmaker.media.domain.interactor.impl

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

    override suspend fun updateTrackIdList(playlistId: Int, trackIdList: String, tracksCount: Int) {
        repository.updateTrackIdList(playlistId, trackIdList, tracksCount)
    }

    override suspend fun addTrack(track: Track) {
        repository.addTrack(track)
    }
}
