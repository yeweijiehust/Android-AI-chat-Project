package com.example.aichatapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dagger.hilt.android.AndroidEntryPoint
import com.example.aichatapp.data.repository.SettingsRepository
import com.example.aichatapp.presentation.navigation.ChatAppShell
import com.example.aichatapp.ui.theme.AIChatAppTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeString by settingsRepository.appTheme.collectAsState(initial = "SYSTEM")

            val useDarkTheme = when (themeString) {
                "LIGHT" -> false
                "DARK" -> true
                else -> isSystemInDarkTheme() // Fallback to Android OS setting
            }

            AIChatAppTheme(darkTheme = useDarkTheme) {
                ChatAppShell()
            }
        }
    }
}