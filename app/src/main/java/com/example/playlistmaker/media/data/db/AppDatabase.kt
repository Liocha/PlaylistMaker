package com.example.playlistmaker.media.data.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.media.data.db.dao.PlaylistDao
import com.example.playlistmaker.media.data.db.dao.PlaylistTrackDao
import com.example.playlistmaker.media.data.db.dao.TrackDao
import com.example.playlistmaker.media.data.db.entity.PlaylistEntity
import com.example.playlistmaker.media.data.db.entity.PlaylistTrackEntity
import com.example.playlistmaker.media.data.db.entity.TrackEntity


@Database(
    version = 2,
    entities = [TrackEntity::class, PlaylistEntity::class, PlaylistTrackEntity::class],
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
    ],
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun playlistTrackDao(): PlaylistTrackDao
}



