package com.example.aichatapp.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "chat_sessions")
data class ChatSession(
    @PrimaryKey val sessionId: String = UUID.randomUUID().toString(),
    val title: String,
    val systemPrompt: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "chat_messages",
    foreignKeys =[
        ForeignKey(
            entity = ChatSession::class,
            parentColumns = ["sessionId"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE // If a session is deleted, delete its messages
        )
    ],
    indices = [Index("sessionId")] // Speeds up querying messages for a specific chat
)
data class ChatMessage(
    @PrimaryKey val messageId: String = UUID.randomUUID().toString(),
    val sessionId: String,
    val role: String, // "user", "assistant", or "system"
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)