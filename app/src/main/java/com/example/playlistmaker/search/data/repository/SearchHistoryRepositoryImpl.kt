package com.example.playlistmaker.search.data.repository

import android.content.SharedPreferences
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.domain.repository.SearchHistoryRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

const val SEARCH_HISTORY_KEY = "search_history_key"

class SearchHistoryRepositoryImpl(private val prefs: SharedPreferences, private val gson: Gson) : SearchHistoryRepository {

    private val maxTrackHistoryCount = 10


    override fun saveSearchHistory(track: Track) {
        val history = getSearchHistory().apply {
            remove(track)
            add(0, track)
        }
        val jsonString = gson.toJson(history.take(maxTrackHistoryCount))
        prefs.edit().putString(SEARCH_HISTORY_KEY, jsonString).apply()
    }

    override fun getSearchHistory(): MutableList<Track> {
        val jsonString = prefs.getString(SEARCH_HISTORY_KEY, null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<Track>>() {}.type
        return gson.fromJson(jsonString, type)
    }


    override fun clearSearchHistory() {
        prefs.edit().remove(SEARCH_HISTORY_KEY).apply()
    }
}