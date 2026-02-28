package com.example.aichatapp.data.network

import com.example.aichatapp.data.network.dto.ChatCompletionRequest
import com.example.aichatapp.data.network.dto.ChatCompletionResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

interface AiApi {
    @POST
    suspend fun getChatCompletion(
        @Url url: String,
        @Body request: ChatCompletionRequest
    ): ChatCompletionResponse
}