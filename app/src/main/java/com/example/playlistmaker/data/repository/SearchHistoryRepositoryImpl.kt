package com.example.playlistmaker.data.repository

import android.content.Context
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.SearchHistoryRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

const val SEARCH_HISTORY_KEY = "search_history_key"

class SearchHistoryRepositoryImpl(context: Context) : SearchHistoryRepository {

    private val maxTrackHistoryCount = 10
    private val sharedPref = context.getSharedPreferences(SEARCH_HISTORY_KEY, Context.MODE_PRIVATE)

    override fun saveSearchHistory(track: Track) {
        val history = getSearchHistory().apply {
            remove(track)
            add(0, track)
        }
        val jsonString = Gson().toJson(history.take(maxTrackHistoryCount))
        sharedPref.edit().putString(SEARCH_HISTORY_KEY, jsonString).apply()
    }

    override fun getSearchHistory(): MutableList<Track> {
        val jsonString = sharedPref.getString(SEARCH_HISTORY_KEY, null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<Track>>() {}.type
        return Gson().fromJson(jsonString, type)
    }


    override fun clearSearchHistory() {
        sharedPref.edit().remove(SEARCH_HISTORY_KEY).apply()
    }
}