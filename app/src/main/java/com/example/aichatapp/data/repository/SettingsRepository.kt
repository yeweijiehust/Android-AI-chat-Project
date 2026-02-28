package com.example.aichatapp.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension property to create DataStore
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")

class SettingsRepository(private val dataStore: DataStore<Preferences>) {

    // Define the Keys
    private val API_BASE_URL = stringPreferencesKey("api_base_url")
    private val API_KEY = stringPreferencesKey("api_key")
    private val MODEL = stringPreferencesKey("model")
    private val IS_REVERSE_PROXY = booleanPreferencesKey("is_reverse_proxy")

    // Provide default values if nothing is saved yet
    val apiBaseUrl: Flow<String> = dataStore.data.map { it[API_BASE_URL] ?: "https://api.openai.com/v1/" }
    val apiKey: Flow<String> = dataStore.data.map { it[API_KEY] ?: "" }
    val model: Flow<String> = dataStore.data.map { it[MODEL] ?: "gpt-3.5-turbo" }
    val isReverseProxy: Flow<Boolean> = dataStore.data.map { it[IS_REVERSE_PROXY] ?: false }

    // Suspend functions to save data
    suspend fun saveApiBaseUrl(url: String) {
        dataStore.edit { it[API_BASE_URL] = url }
    }

    suspend fun saveApiKey(key: String) {
        dataStore.edit { it[API_KEY] = key }
    }

    suspend fun saveModel(model: String) {
        dataStore.edit { it[MODEL] = model }
    }

    suspend fun setReverseProxy(enabled: Boolean) {
        dataStore.edit { it[IS_REVERSE_PROXY] = enabled }
    }
}