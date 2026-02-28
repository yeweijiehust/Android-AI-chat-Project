package com.example.aichatapp.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.intPreferencesKey
import com.example.aichatapp.data.security.CryptoManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")

class SettingsRepository(
    private val dataStore: DataStore<Preferences>,
    private val cryptoManager: CryptoManager // Inject the CryptoManager
) {

    private val API_BASE_URL = stringPreferencesKey("api_base_url")
    private val SECURE_API_KEY = stringPreferencesKey("secure_api_key")
    private val MODEL = stringPreferencesKey("model")
    private val IS_REVERSE_PROXY = booleanPreferencesKey("is_reverse_proxy")

    val apiBaseUrl: Flow<String> = dataStore.data.map { it[API_BASE_URL] ?: "https://api.openai.com/v1/" }
    val model: Flow<String> = dataStore.data.map { it[MODEL] ?: "gpt-3.5-turbo" }
    val isReverseProxy: Flow<Boolean> = dataStore.data.map { it[IS_REVERSE_PROXY] ?: false }
    private val MAX_HISTORY = intPreferencesKey("max_history")
    val maxHistory: Flow<Int> = dataStore.data.map { it[MAX_HISTORY] ?: 10 }
    private val APP_THEME = stringPreferencesKey("app_theme")

    val appTheme: Flow<String> = dataStore.data.map { it[APP_THEME] ?: "SYSTEM" }



    val apiKey: Flow<String> = dataStore.data.map { preferences ->
        val encryptedKey = preferences[SECURE_API_KEY] ?: ""
        cryptoManager.decrypt(encryptedKey)
    }

    // Encrypt the key before saving it to DataStore
    suspend fun saveApiKey(key: String) {
        val encryptedKey = cryptoManager.encrypt(key)
        dataStore.edit { it[SECURE_API_KEY] = encryptedKey }
    }

    suspend fun saveApiBaseUrl(url: String) {
        dataStore.edit { it[API_BASE_URL] = url }
    }

    suspend fun saveModel(model: String) {
        dataStore.edit { it[MODEL] = model }
    }

    suspend fun setReverseProxy(enabled: Boolean) {
        dataStore.edit { it[IS_REVERSE_PROXY] = enabled }
    }

    suspend fun saveMaxHistory(count: Int) {
        dataStore.edit { it[MAX_HISTORY] = count }
    }

    suspend fun saveAppTheme(theme: String) {
        dataStore.edit { it[APP_THEME] = theme }
    }
}