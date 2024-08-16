package com.example.playlistmaker.media.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import com.example.playlistmaker.media.data.converters.PlaylistDbConverter
import com.example.playlistmaker.media.data.converters.TrackDbConverter
import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.media.data.db.entity.PlaylistEntity
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.media.domain.repository.PlaylistRepository
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PlaylistRepositoryImpl(
    private val context: Context,
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

    override suspend fun saveImageToPrivateStorage(uri: Uri): Uri? {
        val filePath = File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "playlist_covers"
        )
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "IMG_$timeStamp.jpg"
        val file = File(filePath, fileName)
        val inputStream = context.contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, JPEG_COMPRESSION_QUALITY, outputStream)

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    private fun convertFromPlaylistEntity(playlistsEntity: List<PlaylistEntity>): List<Playlist> {
        return playlistsEntity.map { playlistEntity -> playlistDbConverter.map(playlistEntity) }
    }

    companion object {
        private const val JPEG_COMPRESSION_QUALITY = 30
    }
}
