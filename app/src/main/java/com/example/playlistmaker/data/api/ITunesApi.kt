package com.example.playlistmaker.data.api

import com.example.playlistmaker.data.api.model.Response
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesService {
    @GET("/search?entity=song")
    fun search(
        @Query("term") term: String?
    ): Call<Response>
}

