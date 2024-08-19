package com.example.playlistmaker.media.data.converters

import android.net.Uri
import com.example.playlistmaker.media.data.db.entity.PlaylistEntity
import com.example.playlistmaker.media.domain.model.Playlist

class PlaylistDbConverter {
    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            name = playlist.name,
            description = playlist.description,
            pathCover = playlist.pathCover?.toString(),
            tracksCount = playlist.tracksCount,
            trackIdList = playlist.trackIdList
        )
    }

    fun map(playlistEntity: PlaylistEntity): Playlist {
        return Playlist(
            id = playlistEntity.id,
            name = playlistEntity.name,
            description = playlistEntity.description,
            pathCover = playlistEntity.pathCover?.let { Uri.parse(it) },
            tracksCount = playlistEntity.tracksCount,
            trackIdList = playlistEntity.trackIdList
        )
    }
}
