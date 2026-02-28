package com.example.aichatapp.data.network

import com.example.aichatapp.data.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val settingsRepository: SettingsRepository
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Read the API key dynamically from DataStore
        val apiKey = runBlocking { settingsRepository.apiKey.first() }

        val requestBuilder = originalRequest.newBuilder()

        // Only add the header if the key exists
        if (apiKey.isNotBlank()) {
            requestBuilder.header("Authorization", "Bearer $apiKey")
        }

        // Add standard content type headers
        requestBuilder.header("Content-Type", "application/json")

        return chain.proceed(requestBuilder.build())
    }
}