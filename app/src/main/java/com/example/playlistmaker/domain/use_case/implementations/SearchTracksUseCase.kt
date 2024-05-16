package com.example.playlistmaker.domain.use_case.implementations

import com.example.playlistmaker.domain.consumer.Consumer
import com.example.playlistmaker.domain.consumer.ConsumerData
import com.example.playlistmaker.domain.model.Resource
import com.example.playlistmaker.domain.repository.TracksRepository
import com.example.playlistmaker.domain.use_case.interfaces.SearchTracks
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