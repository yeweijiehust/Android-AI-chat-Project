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

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val settingsRepository: SettingsRepository,
    private val aiApi: AiApi,
    savedStateHandle: SavedStateHandle // Used to grab the sessionId from the Navigation Route
) : ViewModel() {

    val sessionId: String = checkNotNull(savedStateHandle["sessionId"])

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    // State to show a loading spinner while waiting for the AI
    private val _isGenerating = MutableStateFlow(false)
    val isGenerating = _isGenerating.asStateFlow()

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

            // 1. Fetch dynamic settings
            val baseUrl = settingsRepository.apiBaseUrl.first()
            val apiKey = settingsRepository.apiKey.first()
            val model = settingsRepository.model.first()

            // 2. Validate Settings (Exception Handling Requirement)
            if (baseUrl.isBlank() || apiKey.isBlank()) {
                _uiEvent.emit(UiEvent.ShowSnackbar("Error: API Key or Base URL is missing. Please check Settings."))
                return@launch
            }

            _isGenerating.value = true

            try {
                // 3. Save User Message to DB
                chatRepository.insertMessage(sessionId, "user", content)

                // 4. Build the payload
                val session = chatRepository.getSessionById(sessionId)
                val systemPrompt = session?.systemPrompt ?: ""

                val requestMessages = mutableListOf<MessageDto>()

                // Add System Prompt if it exists
                if (systemPrompt.isNotBlank()) {
                    requestMessages.add(MessageDto("system", systemPrompt))
                }

                // Add previous chat history
                requestMessages.addAll(
                    messages.value.map { MessageDto(it.role, it.content) }
                )

                // Add the new user message
                requestMessages.add(MessageDto("user", content))

                val request = ChatCompletionRequest(model = model, messages = requestMessages)

                // 5. Ensure the Base URL is formatted correctly for the endpoint
                val endpoint = if (baseUrl.endsWith("/")) "${baseUrl}chat/completions" else "$baseUrl/chat/completions"

                // 6. Make Network Call
                val response = aiApi.getChatCompletion(url = endpoint, request = request)

                // 7. Extract AI response and save to DB
                val aiMessage = response.choices.firstOrNull()?.message?.content ?: "No response from AI."
                chatRepository.insertMessage(sessionId, "assistant", aiMessage)

            } catch (e: HttpException) {
                // Handle HTTP errors (e.g., 401 Unauthorized, 404 Not Found)
                val errorMsg = "API Error ${e.code()}: Please check your API Key and URL."
                _uiEvent.emit(UiEvent.ShowSnackbar(errorMsg))
            } catch (e: UnknownHostException) {
                // Handle Offline / Bad URL errors
                _uiEvent.emit(UiEvent.ShowSnackbar("Network Error: Unable to reach the server. Check your connection or Base URL."))
            } catch (e: Exception) {
                // Handle everything else
                _uiEvent.emit(UiEvent.ShowSnackbar("An error occurred: ${e.localizedMessage}"))
            } finally {
                _isGenerating.value = false
            }
        }
    }
}