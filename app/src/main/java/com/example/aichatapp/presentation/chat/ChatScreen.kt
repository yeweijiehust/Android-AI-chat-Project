package com.example.aichatapp.presentation.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.DisplaySettings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.aichatapp.presentation.util.UiEvent
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel = hiltViewModel(),
    onNavigateUp: () -> Unit,
    onShowSnackbar: suspend (String) -> Unit
) {
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGenerating.collectAsStateWithLifecycle()

    var inputText by remember { mutableStateOf("") }
    var showPromptDialog by remember { mutableStateOf(false) }
    var systemPromptInput by remember { mutableStateOf("") }

    val listState = rememberLazyListState()

    // Handle UI Events (like API error snackbars)
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            if (event is UiEvent.ShowSnackbar) {
                onShowSnackbar(event.message)
            }
        }
    }

    // Auto-scroll to the bottom when a new message arrives
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chat") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showPromptDialog = true }) {
                        Icon(Icons.Default.DisplaySettings, contentDescription = "System Prompt")
                    }
                }
            )
        },
        // imePadding ensures the UI moves up when the keyboard is opened
        modifier = Modifier.imePadding()
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Chat Message List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages) { message ->
                    val isUser = message.role == "user"
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
                    ) {
                        Text(
                            text = message.content,
                            color = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                .padding(12.dp)
                        )
                    }
                }
                if (isGenerating) {
                    item {
                        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    }
                }
            }

            // Bottom Input Area
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Type a message...") },
                    enabled = !isGenerating
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        viewModel.sendMessage(inputText)
                        inputText = ""
                    },
                    enabled = inputText.isNotBlank() && !isGenerating,
                    colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                }
            }
        }

        // System Prompt Configuration Dialog
        if (showPromptDialog) {
            AlertDialog(
                onDismissRequest = { showPromptDialog = false },
                title = { Text("Set System Prompt") },
                text = {
                    OutlinedTextField(
                        value = systemPromptInput,
                        onValueChange = { systemPromptInput = it },
                        label = { Text("Persona / Instructions") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.updateSystemPrompt(systemPromptInput)
                        showPromptDialog = false
                    }) { Text("Save") }
                },
                dismissButton = {
                    TextButton(onClick = { showPromptDialog = false }) { Text("Cancel") }
                }
            )
        }
    }
}