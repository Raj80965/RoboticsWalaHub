package com.roboticswala.hub.data.models

/**
 * User profile document stored in Cloud Firestore under /users/{uid}
 */
data class UserProfile(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val role: String = "Student",    // "Student" or "Admin"
    val status: String = "Pending",  // "Pending" or "Approved"
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "uid" to uid,
            "fullName" to fullName,
            "email" to email,
            "role" to role,
            "status" to status,
            "createdAt" to createdAt
        )
    }

    val isStudent: Boolean get() = role.equals("Student", ignoreCase = true)
    val isAdmin: Boolean get() = role.equals("Admin", ignoreCase = true)
    val isPending: Boolean get() = status.equals("Pending", ignoreCase = true)
    val isApproved: Boolean get() = status.equals("Approved", ignoreCase = true)

    companion object {
        fun fromMap(map: Map<String, Any?>): UserProfile {
            return UserProfile(
                uid = map["uid"] as? String ?: "",
                fullName = map["fullName"] as? String ?: "",
                email = map["email"] as? String ?: "",
                role = map["role"] as? String ?: "Student",
                status = map["status"] as? String ?: "Pending",
                createdAt = (map["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
            )
        }
    }
}
