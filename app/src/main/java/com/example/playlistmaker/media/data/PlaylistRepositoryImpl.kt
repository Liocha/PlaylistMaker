package com.example.playlistmaker.media.data

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import com.example.playlistmaker.media.data.converters.PlaylistDbConverter
import com.example.playlistmaker.media.data.converters.PlaylistTrackDbConverter
import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.media.data.db.entity.PlaylistEntity
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.media.domain.repository.PlaylistRepository
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.utils.TextHelper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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
    private val playlistTrackDbConverter: PlaylistTrackDbConverter,
    private val gson: Gson
) : PlaylistRepository {
    override suspend fun createPlaylist(playlist: Playlist) {
        val playlistEntity = playlistDbConverter.map(playlist)
        appDatabase.playlistDao().insertPlaylist(playlistEntity)
    }

    override fun getAll(): Flow<List<Playlist>> {
        return appDatabase.playlistDao().getAll()
            .map { playlistsEntity -> convertFromPlaylistEntity(playlistsEntity).reversed() }
    }

    override suspend fun updateTrackIdList(playlistId: Int, trackIdList: List<String>) {
        appDatabase.playlistDao()
            .updateTrackIdList(playlistId, gson.toJson(trackIdList), trackIdList.count())
    }

    override suspend fun addTrack(track: Track) {
        appDatabase.playlistTrackDao().addTrack(playlistTrackDbConverter.map(track))
    }

    override suspend fun saveImageToPrivateStorage(uri: Uri): Uri {
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

        return Uri.fromFile(file)
    }

    override suspend fun getPlaylistByid(id: Int): Playlist {
        val playlistEntity = appDatabase.playlistDao().getPlaylistById(id)
        return playlistDbConverter.map(playlistEntity)
    }

    override suspend fun getAllTracksByIds(trackIdList: String): List<Track> {
        val trackIds = getTrackIdsFromJson(trackIdList)
        return appDatabase.playlistTrackDao().getTracksByIds(trackIds)
            .map { trackEntity -> playlistTrackDbConverter.map(trackEntity) }
    }

    override suspend fun removeTrackFromPlaylist(playlistId: Int, trackId: String) {
        val playlist = getPlaylistByid(playlistId)
        val trackIds = getTrackIdsFromJson(playlist.trackIdList)
        trackIds.removeIf { it == trackId }
        updateTrackIdList(playlist.id, trackIds)

        val allPlaylists = getAll().first()
        if (allPlaylists.none { playlist ->
                getTrackIdsFromJson(playlist.trackIdList).contains(
                    trackId
                )
            }) {
            appDatabase.playlistTrackDao().deleteTrack(trackId)
        }
    }

    override suspend fun sharePlaylist(playlistId: Int) {
        val playlist = getPlaylistByid(playlistId)
        val playlistData = mutableListOf<String>()
        playlistData.add(playlist.name)
        playlistData.add(playlist.description)
        playlistData.add(
            "${playlist.tracksCount} ${
                TextHelper.getCountEnding(
                    context,
                    playlist.tracksCount
                )
            }"
        )
        getAllTracksByIds(playlist.trackIdList)?.forEachIndexed { idx, track ->
            val time = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
            playlistData.add("${idx.plus(1)}. ${track.artistName} - ${track.trackName} ($time)")
        }

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, playlistData.joinToString(separator = "\n"))
            type = "text/plain"
        }
        val shareAppIntent = Intent.createChooser(sendIntent, null)
        shareAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareAppIntent)
    }

    override suspend fun deletePlaylist(playlistId: Int) {
        val playlistToDelete = getPlaylistByid(playlistId)
        val trackIdsToDelete = getTrackIdsFromJson(playlistToDelete.trackIdList)
        val allPlaylists = getAll().first()
        val tracksUsedInPlaylists = allPlaylists.filter { it.id != playlistToDelete.id }
            .fold(mutableSetOf<String>()) { acc, playlist ->
                acc.addAll(getTrackIdsFromJson(playlist.trackIdList))
                acc
            }
        trackIdsToDelete.forEach { trackId ->
            if (!tracksUsedInPlaylists.contains(trackId)) {
                appDatabase.playlistTrackDao().deleteTrack(trackId)
            }
        }
        appDatabase.playlistDao().deletePlaylistById(playlistId)
    }

    override suspend fun updatePlaylistData(
        id: Int,
        name: String?,
        description: String?,
        imagePath: Uri?
    ) {
        val playlist = getPlaylistByid(id)
        val updatedPlaylist = playlist.copy(
            name = name ?: playlist.name,
            description = description ?: playlist.description,
            pathCover = imagePath ?: playlist.pathCover
        )
        if (updatedPlaylist != playlist) {
            appDatabase.playlistDao().updatePlaylist(playlistDbConverter.map(updatedPlaylist))
        }
    }

    private fun convertFromPlaylistEntity(playlistsEntity: List<PlaylistEntity>): List<Playlist> {
        return playlistsEntity.map { playlistEntity -> playlistDbConverter.map(playlistEntity) }
    }

    private fun getTrackIdsFromJson(trackIdList: String): MutableList<String> {
        val type = object : TypeToken<MutableList<String>>() {}.type
        return gson.fromJson(trackIdList, type) ?: mutableListOf()
    }

    companion object {
        private const val JPEG_COMPRESSION_QUALITY = 30
    }
}
