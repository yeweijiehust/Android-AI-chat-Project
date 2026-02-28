package com.example.aichatapp.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aichatapp.data.repository.ChatRepository
import com.example.aichatapp.presentation.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    // Automatically observes database changes!
    val chatSessions = chatRepository.getAllSessions().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun createNewChat(title: String) {
        viewModelScope.launch {
            val newSessionId = chatRepository.createNewSession(title)
            // Tell the UI to navigate to the new chat screen
            _uiEvent.emit(UiEvent.Navigate("chat/$newSessionId"))
        }
    }

    fun deleteChat(sessionId: String) {
        viewModelScope.launch {
            chatRepository.deleteSession(sessionId)
        }
    }
}