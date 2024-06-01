package com.example.playlistmaker.di

import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.network.ITunesApiService
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single<ITunesApiService> {
        Retrofit.Builder().baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesApiService::class.java)
    }

    single<NetworkClient> {
        RetrofitNetworkClient(get())
    }

}
