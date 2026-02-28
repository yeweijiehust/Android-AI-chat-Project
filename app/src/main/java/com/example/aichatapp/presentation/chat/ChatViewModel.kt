package com.example.aichatapp.presentation.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aichatapp.data.network.AiApi
import com.example.aichatapp.data.network.dto.ChatCompletionRequest
import com.example.aichatapp.data.network.dto.MessageDto
import com.example.aichatapp.data.repository.ChatRepository
import com.example.aichatapp.data.repository.SettingsRepository
import com.example.aichatapp.presentation.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.UnknownHostException
import javax.inject.Inject
import com.example.aichatapp.data.network.AiStreamClient

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val settingsRepository: SettingsRepository,
    private val aiStreamClient: AiStreamClient,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val sessionId: String = checkNotNull(savedStateHandle["sessionId"])

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating = _isGenerating.asStateFlow()

    private val _streamingMessage = MutableStateFlow<String?>(null)
    val streamingMessage = _streamingMessage.asStateFlow()

    // Fetches the messages for this specific chat
    val messages = chatRepository.getMessagesForSession(sessionId).stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    fun updateSystemPrompt(prompt: String) {
        viewModelScope.launch {
            chatRepository.updateSystemPrompt(sessionId, prompt)
            _uiEvent.emit(UiEvent.ShowSnackbar("System Prompt Updated!"))
        }
    }

    fun sendMessage(content: String) {
        viewModelScope.launch {
            if (content.isBlank()) return@launch

            val baseUrl = settingsRepository.apiBaseUrl.first()
            val apiKey = settingsRepository.apiKey.first() // Or getApiKeySync() if using Step 7
            val model = settingsRepository.model.first()
            val maxHistory = settingsRepository.maxHistory.first()
            if (baseUrl.isBlank() || apiKey.isBlank()) {
                _uiEvent.emit(UiEvent.ShowSnackbar("Error: API Key or Base URL missing."))
                return@launch
            }

            // 1. Save User Message to DB immediately
            chatRepository.insertMessage(sessionId, "user", content)

            _isGenerating.value = true
            _streamingMessage.value = "" // Initialize empty stream

            try {
                // 2. Build Request (Set stream = true)
                val session = chatRepository.getSessionById(sessionId)
                val systemPrompt = session?.systemPrompt ?: ""

                val requestMessages = mutableListOf<MessageDto>()
                if (systemPrompt.isNotBlank()) requestMessages.add(MessageDto("system", systemPrompt))
                val recentHistory = messages.value.takeLast(maxHistory)

                requestMessages.addAll(recentHistory.map { MessageDto(it.role, it.content) })
                requestMessages.add(MessageDto("user", content))

                val request = ChatCompletionRequest(model = model, messages = requestMessages, stream = true)
                val endpoint = if (baseUrl.endsWith("/")) "${baseUrl}chat/completions" else "$baseUrl/chat/completions"

                // 3. Collect the Stream!
                aiStreamClient.getChatStream(endpoint, request).collect { textDelta ->
                    // Append the incoming chunk to the current streaming message
                    _streamingMessage.value = (_streamingMessage.value ?: "") + textDelta
                }

                // 4. Stream finished. Save the final completed message to the Database!
                val finalMessage = _streamingMessage.value
                if (!finalMessage.isNullOrBlank()) {
                    chatRepository.insertMessage(sessionId, "assistant", finalMessage)
                }

            } catch (e: Exception) {
                _uiEvent.emit(UiEvent.ShowSnackbar("Streaming Error: ${e.localizedMessage}"))
            } finally {
                // 5. Clean up state
                _streamingMessage.value = null
                _isGenerating.value = false
            }
        }
    }
}