package com.example.playlistmaker.data

import android.content.SharedPreferences
import com.example.playlistmaker.data.api.model.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


const val SEARCH_HISTORY_KEY = "search_history_key"

class SearchHistory(private val sharedPref: SharedPreferences) {
    private val maxTrackHistoryCount = 10
    fun addTrack(track: Track) {
        val history = tracks().apply {
            remove(track)
            add(0, track)
        }
        val jsonString = Gson().toJson(history.take(maxTrackHistoryCount))
        sharedPref.edit().putString(SEARCH_HISTORY_KEY, jsonString).apply()
    }

    fun tracks(): MutableList<Track> {
        val jsonString = sharedPref.getString(SEARCH_HISTORY_KEY, null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<Track>>() {}.type
        return Gson().fromJson(jsonString, type)
    }

    fun clear() {
        sharedPref.edit().remove(SEARCH_HISTORY_KEY).apply()
    }

    fun isNotEmpty() = tracks().isNotEmpty()

}