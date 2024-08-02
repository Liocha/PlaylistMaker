package com.example.playlistmaker.search.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Track(
    val artistName: String,
    val artworkUrl100: String,
    val trackName: String,
    val trackTimeMillis: Int,
    val trackId: String,
    val collectionName: String,
    val releaseDate: String?,
    val primaryGenreName: String,
    val country: String,
    var previewUrl: String?,
    var isFavorite: Boolean = false
) : Parcelable
