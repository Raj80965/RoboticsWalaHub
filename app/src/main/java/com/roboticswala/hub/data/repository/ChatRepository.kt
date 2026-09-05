package com.roboticswala.hub.data.repository

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.roboticswala.hub.data.models.ChatMessage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ChatRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val chatCollection = firestore.collection("community_chats")
    private val typingCollection = firestore.collection("community_chat_typing")

    companion object {
        private const val PREFS_NAME = "rwh_chat_prefs"
        private const val KEY_LAST_READ_PREFIX = "last_read_ts_"

        fun getLastReadTimestamp(context: Context, userId: String): Long {
            if (userId.isBlank()) return 0L
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getLong(KEY_LAST_READ_PREFIX + userId, 0L)
        }

        fun markChatAsRead(context: Context, userId: String) {
            if (userId.isBlank()) return
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putLong(KEY_LAST_READ_PREFIX + userId, System.currentTimeMillis()).apply()
        }
    }

    fun getMessages(): Flow<List<ChatMessage>> = callbackFlow {
        val listener = chatCollection
            .orderBy("clientTimeMillis", Query.Direction.ASCENDING)
            .limitToLast(150)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val messages = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(ChatMessage::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(messages)
            }

        awaitClose { listener.remove() }
    }

    fun getUnreadCountFlow(context: Context, currentUserId: String): Flow<Int> = callbackFlow {
        if (currentUserId.isBlank()) {
            trySend(0)
            close()
            return@callbackFlow
        }

        val listener = chatCollection
            .orderBy("clientTimeMillis", Query.Direction.DESCENDING)
            .limit(50)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(0)
                    return@addSnapshotListener
                }
                val lastRead = getLastReadTimestamp(context, currentUserId)
                val unreadCount = snapshot?.documents.orEmpty().count { doc ->
                    val senderId = doc.getString("senderId").orEmpty()
                    val timeMillis = doc.getLong("clientTimeMillis") ?: 0L
                    senderId != currentUserId && timeMillis > lastRead
                }
                trySend(unreadCount)
            }

        awaitClose { listener.remove() }
    }

    suspend fun sendMessage(message: ChatMessage): Boolean {
        return try {
            chatCollection.add(message).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteMessage(messageId: String): Boolean {
        return try {
            chatCollection.document(messageId).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun setTypingStatus(userId: String, userName: String, isTyping: Boolean) {
        if (userId.isBlank()) return
        val data = mapOf(
            "userId" to userId,
            "userName" to userName,
            "isTyping" to isTyping,
            "timestamp" to System.currentTimeMillis()
        )
        typingCollection.document(userId).set(data, SetOptions.merge())
    }

    fun getTypingUsersFlow(currentUserId: String): Flow<List<String>> = callbackFlow {
        val listener = typingCollection
            .whereEqualTo("isTyping", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val now = System.currentTimeMillis()
                val typingNames = snapshot?.documents.orEmpty()
                    .filter { doc ->
                        val uid = doc.getString("userId").orEmpty()
                        val isTyping = doc.getBoolean("isTyping") ?: false
                        val ts = doc.getLong("timestamp") ?: 0L
                        isTyping && uid != currentUserId && (Math.abs(now - ts) < 30000)
                    }
                    .mapNotNull { it.getString("userName") }
                    .distinct()
                trySend(typingNames)
            }

        awaitClose { listener.remove() }
    }

    fun getLatestIncomingMessageFlow(currentUserId: String): Flow<ChatMessage?> = callbackFlow {
        if (currentUserId.isBlank()) {
            trySend(null)
            close()
            return@callbackFlow
        }
        val startTime = System.currentTimeMillis()
        val listener = chatCollection
            .orderBy("clientTimeMillis", Query.Direction.DESCENDING)
            .limit(1)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(null)
                    return@addSnapshotListener
                }
                val doc = snapshot?.documents?.firstOrNull()
                if (doc != null) {
                    val msg = doc.toObject(ChatMessage::class.java)?.copy(id = doc.id)
                    if (msg != null && msg.senderId != currentUserId && msg.clientTimeMillis > (startTime - 2000)) {
                        trySend(msg)
                    }
                }
            }
        awaitClose { listener.remove() }
    }
}
