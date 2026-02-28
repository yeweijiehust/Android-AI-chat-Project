package com.example.aichatapp.domain.util

sealed class NetworkResult<T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error<T>(val message: String, val exception: Exception? = null) : NetworkResult<T>()
}