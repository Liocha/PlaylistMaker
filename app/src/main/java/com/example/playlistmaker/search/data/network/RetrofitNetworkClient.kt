package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.dto.Response
import com.example.playlistmaker.search.data.dto.TracksSearchRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException


class RetrofitNetworkClient(
    private val iTunesService: ITunesApiService
) : NetworkClient {

    override suspend fun doRequest(dto: Any): Response {
        if (dto !is TracksSearchRequest) {
            return Response().apply {
                resultCode = 400
                message = "Ошибка, dto не TracksSearchRequest"
            }
        }

        return withContext(Dispatchers.IO) {
            try {
                val response = iTunesService.findTracks(dto.query)
                response.apply { resultCode = 200 }
            } catch (e: IOException) {
                val networkErrorMessage = e.message ?: "Network error occurred"
                Response().apply {
                    resultCode = -1
                    message = networkErrorMessage
                }
            } catch (e: Throwable) {
                val generalErrorMessage = e.message ?: "An error occurred"
                Response().apply {
                    resultCode = 500
                    message = generalErrorMessage
                }
            }
        }

    }

}