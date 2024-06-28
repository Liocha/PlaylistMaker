package com.example.playlistmaker.search.domain.use_case

import androidx.core.util.Pair
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface SearchTracks {
    fun execute(query: String): Flow<Pair<List<Track>?, String?>>
}