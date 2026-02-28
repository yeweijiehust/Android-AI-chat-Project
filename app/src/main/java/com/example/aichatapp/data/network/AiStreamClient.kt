package com.example.aichatapp.data.network

import com.example.aichatapp.data.network.dto.ChatCompletionRequest
import com.example.aichatapp.data.network.dto.ChatStreamChunk
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiStreamClient @Inject constructor(
    private val okHttpClient: OkHttpClient
) {
    private val json = Json { ignoreUnknownKeys = true }

    fun getChatStream(url: String, request: ChatCompletionRequest): Flow<String> = callbackFlow {

        val requestBody = json.encodeToString(request)
            .toRequestBody("application/json".toMediaType())

        val httpRequest = Request.Builder()
            .url(url)
            .post(requestBody)
            .header("Accept", "text/event-stream") // Standard SSE header
            .header("Cache-Control", "no-cache")   // Prevent caching of the stream
            .build()

        val factory = EventSources.createFactory(okHttpClient)
        val eventSource = factory.newEventSource(httpRequest, object : EventSourceListener() {

            override fun onEvent(eventSource: EventSource, id: String?, type: String?, data: String) {
                if (data == "[DONE]") {
                    close()
                    return
                }

                try {
                    val chunk = json.decodeFromString<ChatStreamChunk>(data)
                    val content = chunk.choices.firstOrNull()?.delta?.content
                    if (content != null) {
                        trySend(content)
                    }
                } catch (e: Exception) {
                    // Ignore parsing errors for empty chunks or non-JSON lines
                }
            }

            override fun onFailure(eventSource: EventSource, t: Throwable?, response: okhttp3.Response?) {
                close(t ?: Exception("SSE Failure: ${response?.code}"))
            }

            override fun onClosed(eventSource: EventSource) {
                close()
            }
        })

        awaitClose {
            eventSource.cancel()
        }
    }
}
