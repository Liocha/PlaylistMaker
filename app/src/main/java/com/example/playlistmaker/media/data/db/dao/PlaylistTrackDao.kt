package com.example.playlistmaker.media.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.media.data.db.entity.PlaylistTrackEntity

@Dao
interface PlaylistTrackDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTrack(track: PlaylistTrackEntity)

    @Query("SELECT * FROM playlist_tracks WHERE trackId IN (:trackIds)")
    suspend fun getTracksByIds(trackIds: List<String>): List<PlaylistTrackEntity>

    @Query("DELETE FROM playlist_tracks WHERE trackId = :id")
    suspend fun deleteTrack(id: String)

}
