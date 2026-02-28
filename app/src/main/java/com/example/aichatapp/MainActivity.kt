package com.example.aichatapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import com.example.aichatapp.presentation.navigation.ChatAppShell
import com.example.aichatapp.ui.theme.AIChatAppTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AIChatAppTheme {
                ChatAppShell()
            }
        }
    }
}