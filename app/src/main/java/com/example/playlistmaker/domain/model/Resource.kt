package com.example.playlistmaker.domain.model

sealed interface Resource<T> {
    data class Success<T>(val data: T) : Resource<T>
    data class Error<T>(val message: String, val code: Int? = null) : Resource<T>
    data class NetworkError<T>(val message: String) : Resource<T>
}