package com.example.aichatapp.di

import android.content.Context
import androidx.room.Room
import com.example.aichatapp.data.local.AppDatabase
import com.example.aichatapp.data.local.ChatDao
import com.example.aichatapp.data.repository.ChatRepository
import com.example.aichatapp.data.repository.SettingsRepository
import com.example.aichatapp.data.repository.dataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.example.aichatapp.data.network.AiApi
import com.example.aichatapp.data.network.AuthInterceptor
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import com.example.aichatapp.data.security.CryptoManager

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideCryptoManager(): CryptoManager {
        return CryptoManager()
    }

    // 1. Provide DataStore Settings Repository
    @Provides
    @Singleton
    fun provideSettingsRepository(
        @ApplicationContext context: Context,
        cryptoManager: CryptoManager
    ): SettingsRepository {
        return SettingsRepository(context.dataStore, cryptoManager)
    }

    // 2. Provide Room Database
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "ai_chat_database"
        ).build()
    }

    // 3. Provide DAO
    @Provides
    fun provideChatDao(database: AppDatabase): ChatDao {
        return database.chatDao()
    }

    // 4. Provide Chat Repository
    @Provides
    @Singleton
    fun provideChatRepository(chatDao: ChatDao): ChatRepository {
        return ChatRepository(chatDao)
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(settingsRepository: SettingsRepository): AuthInterceptor {
        return AuthInterceptor(settingsRepository)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // Useful for debugging in Logcat
        }

        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS) // AI responses can be slow
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideAiApi(okHttpClient: OkHttpClient): AiApi {
        val json = Json { ignoreUnknownKeys = true } // Ignores extra fields from API

        return Retrofit.Builder()
            // Retrofit requires a base URL to compile, but we override it in the @Url parameter
            .baseUrl("https://api.openai.com/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(AiApi::class.java)
    }
}
