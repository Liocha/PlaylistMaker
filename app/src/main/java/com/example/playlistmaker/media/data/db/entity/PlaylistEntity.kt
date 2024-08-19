package com.example.playlistmaker.media.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String,
    @ColumnInfo(name = "path_cover") val pathCover: String?,
    @ColumnInfo(name = "track_id_list") val trackIdList: String,
    @ColumnInfo(name = "tracks_count") val tracksCount: Int
)
