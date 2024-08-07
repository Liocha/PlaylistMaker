package com.example.playlistmaker.search.data.repository

import android.content.SharedPreferences
import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.domain.repository.SearchHistoryRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

const val SEARCH_HISTORY_KEY = "search_history_key"

class SearchHistoryRepositoryImpl(
    private val prefs: SharedPreferences,
    private val gson: Gson,
    private val appDatabase: AppDatabase
) : SearchHistoryRepository {

    private val maxTrackHistoryCount = 10


    override suspend fun saveSearchHistory(track: Track) {
        val trackCopy = track.copy(isFavorite = false)
        val jsonString = prefs.getString(SEARCH_HISTORY_KEY, null) ?: ""

        val type = object : TypeToken<MutableList<Track>>() {}.type
        val tracks: MutableList<Track> = if (jsonString.isNotEmpty()) {
            gson.fromJson(jsonString, type)
        } else {
            mutableListOf()
        }

        val history = tracks.apply {
            remove(trackCopy)
            add(0, trackCopy)
        }
        val newJsonString = gson.toJson(history.take(maxTrackHistoryCount))
        prefs.edit().putString(SEARCH_HISTORY_KEY, newJsonString).apply()
    }

    override suspend fun getSearchHistory(): MutableList<Track> {
        val jsonString = prefs.getString(SEARCH_HISTORY_KEY, null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<Track>>() {}.type
        val favoriteTrackIds = appDatabase.trackDao().getTracksId()
        val tracks: MutableList<Track> = gson.fromJson(jsonString, type)
        tracks.forEach { track ->
            track.isFavorite = favoriteTrackIds.contains(track.trackId)
        }
        return tracks
    }

    override fun clearSearchHistory() {
        prefs.edit().remove(SEARCH_HISTORY_KEY).apply()
    }
}