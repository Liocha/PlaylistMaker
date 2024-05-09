package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.Response
import com.example.playlistmaker.data.dto.TracksSearchRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException


class RetrofitNetworkClient : NetworkClient {

    private val iTunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(ITunesApiService::class.java)
    override fun doRequest(dto: Any): Response {
        if (dto is TracksSearchRequest) {
            try {
                val response = iTunesService.findTracks(dto.query).execute()
                if (response.isSuccessful) {
                    val body = response.body() ?: Response()
                    return body.apply { resultCode = response.code() }
                } else {
                    return Response().apply { resultCode = response.code() }
                }
            } catch (e: IOException) {
                val networkErrorMessage = e.message ?: "Network error occurred"
                return Response().apply {
                    resultCode = -1
                    message = networkErrorMessage
                }
            } catch (e: Exception) {
                val generalErrorMessage = e.message ?: "An error occurred"
                return Response().apply {
                    resultCode = -1
                    message = generalErrorMessage
                }
            }
        } else {
            return Response().apply {
                resultCode = 400
                message = "Ошибка, dto не TracksSearchRequest"
            }
        }
    }
}