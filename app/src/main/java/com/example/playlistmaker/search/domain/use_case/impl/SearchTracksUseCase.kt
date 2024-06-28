package com.example.playlistmaker.search.domain.use_case.impl

import androidx.core.util.Pair
import com.example.playlistmaker.search.domain.model.Resource
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.domain.repository.TracksRepository
import com.example.playlistmaker.search.domain.use_case.SearchTracks
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SearchTracksUseCase(private val tracksRepository: TracksRepository) : SearchTracks {

    override fun execute(query: String): Flow<Pair<List<Track>?, String?>> {

        return tracksRepository.searchTracks(query).map { result ->
            when (result) {
                is Resource.Success -> {
                    Pair(result.data, null)
                }

                is Resource.Error -> {
                    Pair(null, result.message)
                }

            }
        }
    }

}