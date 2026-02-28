package com.example.aichatapp.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onShowSnackbar: suspend (String) -> Unit
) {
    val baseUrl by viewModel.apiBaseUrl.collectAsStateWithLifecycle()
    val apiKey by viewModel.apiKey.collectAsStateWithLifecycle()
    val model by viewModel.model.collectAsStateWithLifecycle()
    val isReverseProxy by viewModel.isReverseProxy.collectAsStateWithLifecycle()
    val maxHistory by viewModel.maxHistory.collectAsStateWithLifecycle()
    val appTheme by viewModel.appTheme.collectAsStateWithLifecycle()
    var themeDropdownExpanded by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = { TopAppBar(title = { Text("Settings") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = baseUrl,
                onValueChange = viewModel::updateApiBaseUrl,
                label = { Text("API Base URL") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = apiKey,
                onValueChange = viewModel::updateApiKey,
                label = { Text("API Key") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = model,
                onValueChange = viewModel::updateModel,
                label = { Text("Model") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = maxHistory.toString(),
                onValueChange = viewModel::updateMaxHistory,
                label = { Text("Memory Context (1-100)") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            ExposedDropdownMenuBox(
                expanded = themeDropdownExpanded,
                onExpandedChange = { themeDropdownExpanded = !themeDropdownExpanded }
            ) {
                OutlinedTextField(
                    value = when (appTheme) {
                        "DARK" -> "Dark Mode"
                        "LIGHT" -> "Light Mode"
                        else -> "System Default"
                    },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("App Theme") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = themeDropdownExpanded) },
                    modifier = Modifier
                        .menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth(),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = themeDropdownExpanded,
                    onDismissRequest = { themeDropdownExpanded = false }
                ) {
                    val themeOptions = listOf("SYSTEM" to "System Default", "LIGHT" to "Light Mode", "DARK" to "Dark Mode")
                    themeOptions.forEach { (key, displayValue) ->
                        DropdownMenuItem(
                            text = { Text(displayValue) },
                            onClick = {
                                viewModel.updateAppTheme(key)
                                themeDropdownExpanded = false
                            }
                        )
                    }
                }
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Enable Reverse Proxy Mode", style = MaterialTheme.typography.bodyLarge)
                Switch(
                    checked = isReverseProxy,
                    onCheckedChange = viewModel::updateReverseProxy
                )
            }

            Text(
                text = "Changes are saved automatically.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
