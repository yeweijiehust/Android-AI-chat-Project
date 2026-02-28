package com.example.aichatapp.data.repository

import com.example.aichatapp.data.local.ChatDao
import com.example.aichatapp.data.local.ChatMessage
import com.example.aichatapp.data.local.ChatSession
import kotlinx.coroutines.flow.Flow

class ChatRepository(private val chatDao: ChatDao) {

    fun getAllSessions(): Flow<List<ChatSession>> = chatDao.getAllSessions()

    fun getMessagesForSession(sessionId: String): Flow<List<ChatMessage>> =
        chatDao.getMessagesForSession(sessionId)

    suspend fun createNewSession(title: String, systemPrompt: String = ""): String {
        val session = ChatSession(title = title, systemPrompt = systemPrompt)
        chatDao.insertSession(session)
        return session.sessionId
    }

    suspend fun insertMessage(sessionId: String, role: String, content: String) {
        val message = ChatMessage(sessionId = sessionId, role = role, content = content)
        chatDao.insertMessage(message)
        // Update the session's timestamp so it jumps to the top of the Home list
        chatDao.updateSessionTimestamp(sessionId)
    }

    suspend fun updateSystemPrompt(sessionId: String, prompt: String) {
        chatDao.updateSystemPrompt(sessionId, prompt)
    }

    suspend fun deleteSession(sessionId: String) {
        chatDao.deleteSession(sessionId)
    }

    suspend fun getSessionById(sessionId: String): ChatSession? {
        return chatDao.getSessionById(sessionId)
    }
}