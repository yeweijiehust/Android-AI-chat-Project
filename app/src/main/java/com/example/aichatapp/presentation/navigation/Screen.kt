package com.example.aichatapp.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    object Home : Screen(route = "home", title = "Chats", icon = Icons.AutoMirrored.Filled.Message)
    object Settings : Screen(route = "settings", title = "Settings", icon = Icons.Default.Settings)

    // The chat route requires a sessionId parameter
    object Chat : Screen(route = "chat/{sessionId}", title = "Chat") {
        fun createRoute(sessionId: String) = "chat/$sessionId"
    }
}