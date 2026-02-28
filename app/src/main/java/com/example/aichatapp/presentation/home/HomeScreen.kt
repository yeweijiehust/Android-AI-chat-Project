package com.example.aichatapp.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.aichatapp.presentation.util.UiEvent
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigate: (String) -> Unit,
    onShowSnackbar: suspend (String) -> Unit
) {
    val sessions by viewModel.chatSessions.collectAsStateWithLifecycle()
    var showNewChatDialog by remember { mutableStateOf(false) }
    var newChatTitle by remember { mutableStateOf("") }

    // Listen for Navigation and Snackbar events from the ViewModel
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is UiEvent.Navigate -> onNavigate(event.route)
                is UiEvent.ShowSnackbar -> onShowSnackbar(event.message)
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("My Chats") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showNewChatDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "New Chat")
            }
        }
    ) { padding ->
        if (sessions.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No chats yet. Click + to start!", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
                items(sessions) { session ->
                    ListItem(
                        headlineContent = { Text(session.title) },
                        modifier = Modifier.clickable { onNavigate("chat/${session.sessionId}") },
                        trailingContent = {
                            IconButton(onClick = { viewModel.deleteChat(session.sessionId) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete Chat", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    )
                    HorizontalDivider()
                }
            }
        }

        // New Chat Dialog
        if (showNewChatDialog) {
            AlertDialog(
                onDismissRequest = { showNewChatDialog = false },
                title = { Text("New Chat") },
                text = {
                    OutlinedTextField(
                        value = newChatTitle,
                        onValueChange = { newChatTitle = it },
                        label = { Text("Chat Title") },
                        singleLine = true
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (newChatTitle.isNotBlank()) {
                            viewModel.createNewChat(newChatTitle)
                            showNewChatDialog = false
                            newChatTitle = ""
                        }
                    }) { Text("Create") }
                },
                dismissButton = {
                    TextButton(onClick = { showNewChatDialog = false }) { Text("Cancel") }
                }
            )
        }
    }
}