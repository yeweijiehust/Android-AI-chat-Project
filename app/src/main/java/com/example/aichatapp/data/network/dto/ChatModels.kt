package com.example.aichatapp.data.network.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Request Models
@Serializable
data class ChatCompletionRequest(
    val model: String,
    val messages: List<MessageDto>,
    val temperature: Double = 0.7,
    val stream: Boolean = false
)

@Serializable
data class MessageDto(
    val role: String, // "system", "user", or "assistant"
    val content: String
)

// Response Models
@Serializable
data class ChatCompletionResponse(
    val id: String = "",
    val choices: List<ChoiceDto>
)

@Serializable
data class ChoiceDto(
    val index: Int,
    val message: MessageDto,
    @SerialName("finish_reason") val finishReason: String? = null
)

@Serializable
data class ChatStreamChunk(
    val id: String = "",
    val choices: List<StreamChoice>
)

@Serializable
data class StreamChoice(
    val index: Int,
    val delta: DeltaDto,
    @SerialName("finish_reason") val finishReason: String? = null
)

@Serializable
data class DeltaDto(
    val content: String? = null // It might be null in the first or last chunk
)