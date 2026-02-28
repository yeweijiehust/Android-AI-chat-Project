package com.example.aichatapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    // Session Queries
    @Query("SELECT * FROM chat_sessions ORDER BY updatedAt DESC")
    fun getAllSessions(): Flow<List<ChatSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: ChatSession)

    @Query("UPDATE chat_sessions SET systemPrompt = :prompt, updatedAt = :time WHERE sessionId = :sessionId")
    suspend fun updateSystemPrompt(sessionId: String, prompt: String, time: Long = System.currentTimeMillis())

    @Query("DELETE FROM chat_sessions WHERE sessionId = :sessionId")
    suspend fun deleteSession(sessionId: String)

    // Message Queries
    @Query("SELECT * FROM chat_messages WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    fun getMessagesForSession(sessionId: String): Flow<List<ChatMessage>>

    @Insert
    suspend fun insertMessage(message: ChatMessage)

    // Helper to update the session's timestamp when a new message is sent
    @Query("UPDATE chat_sessions SET updatedAt = :time WHERE sessionId = :sessionId")
    suspend fun updateSessionTimestamp(sessionId: String, time: Long = System.currentTimeMillis())

    @Query("SELECT * FROM chat_sessions WHERE sessionId = :sessionId LIMIT 1")
    suspend fun getSessionById(sessionId: String): ChatSession?
}