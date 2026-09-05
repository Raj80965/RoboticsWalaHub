package com.roboticswala.hub.data.models

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class ChatMessage(
    val id: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val senderRole: String = "STUDENT", // "ADMIN" or "STUDENT"
    val senderPhotoUrl: String = "",
    val message: String = "",
    @ServerTimestamp
    val timestamp: Date? = null,
    val clientTimeMillis: Long = System.currentTimeMillis()
)
