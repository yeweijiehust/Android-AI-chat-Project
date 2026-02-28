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
    val maxHistory = settingsRepository.maxHistory.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 10
    )

    val appTheme = settingsRepository.appTheme.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), "SYSTEM"
    )
    fun updateMaxHistory(countStr: String) = viewModelScope.launch {
        val count = countStr.toIntOrNull() ?: 10
        // Coerce the value so the user can't put negative numbers or crazy high numbers
        settingsRepository.saveMaxHistory(count.coerceIn(0, 100))
    }

    fun updateAppTheme(theme: String) = viewModelScope.launch {
        settingsRepository.saveAppTheme(theme)
    }
    fun updateApiBaseUrl(url: String) = viewModelScope.launch { settingsRepository.saveApiBaseUrl(url) }
    fun updateApiKey(key: String) = viewModelScope.launch { settingsRepository.saveApiKey(key) }
    fun updateModel(model: String) = viewModelScope.launch { settingsRepository.saveModel(model) }
    fun updateReverseProxy(enabled: Boolean) = viewModelScope.launch { settingsRepository.setReverseProxy(enabled) }
}