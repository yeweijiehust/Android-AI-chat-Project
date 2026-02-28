package com.example.aichatapp.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aichatapp.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val apiBaseUrl = settingsRepository.apiBaseUrl.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ""
    )

    val apiKey = settingsRepository.apiKey.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), ""
    )

    val model = settingsRepository.model.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), "gpt-3.5-turbo"
    )

    val isReverseProxy = settingsRepository.isReverseProxy.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), false
    )

    fun updateApiBaseUrl(url: String) = viewModelScope.launch { settingsRepository.saveApiBaseUrl(url) }
    fun updateApiKey(key: String) = viewModelScope.launch { settingsRepository.saveApiKey(key) }
    fun updateModel(model: String) = viewModelScope.launch { settingsRepository.saveModel(model) }
    fun updateReverseProxy(enabled: Boolean) = viewModelScope.launch { settingsRepository.setReverseProxy(enabled) }
}