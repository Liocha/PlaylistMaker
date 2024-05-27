package com.example.playlistmaker.search.domain.use_case.impl

import com.example.playlistmaker.search.domain.consumer.Consumer
import com.example.playlistmaker.search.domain.consumer.ConsumerData
import com.example.playlistmaker.search.domain.model.Resource
import com.example.playlistmaker.search.data.repository.TracksRepository
import com.example.playlistmaker.search.domain.use_case.SearchTracks
import java.util.concurrent.Executors

class SearchTracksUseCase(private val tracksRepository: TracksRepository) : SearchTracks {
    private val executor = Executors.newCachedThreadPool()
    override fun execute(query: String, consumer: Consumer) {
        executor.execute {
            when (val currencyResource = tracksRepository.searchTracks(query)) {
                is Resource.Success -> {
                    val tracks = currencyResource.data
                    consumer.consume(ConsumerData.Data(tracks))
                }

                is Resource.Error -> {
                    val msg = currencyResource.message
                    consumer.consume(ConsumerData.Error(msg))
                }

                is Resource.NetworkError -> {
                    val msg = currencyResource.message
                    consumer.consume(ConsumerData.NetworkError(msg))
                }
            }
        }
    }
}