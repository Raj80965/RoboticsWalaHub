package com.roboticswala.hub.data.models

/**
 * User profile document stored in Cloud Firestore under /users/{uid}
 * Day 5: Extended with studentId, college, branch, year, photoUrl, approvedAt
 */
data class UserProfile(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val role: String = "Student",       // "Student" or "Admin"
    val status: String = "Pending",     // "Pending", "Approved", "Rejected", "Suspended"
    val studentId: String = "",         // e.g. "RWH-STU-2026-042"
    val college: String = "",           // e.g. "IIT Robotics College"
    val branch: String = "",            // e.g. "Robotics & Mechatronics"
    val year: String = "",              // e.g. "Year 3"
    val phone: String = "",             // Student Mobile Number
    val parentName: String = "",        // Parent / Guardian Name
    val parentPhone: String = "",       // Parent Contact Number
    val emergencyContact: String = "",  // Emergency Contact Number
    val aadharNumber: String = "",      // Aadhaar Card Number
    val aadharCardUrl: String = "",     // Aadhaar Card Document Image URL
    val aadharUploadedAt: Long = 0L,    // Timestamp when Aadhaar was uploaded
    val photoUrl: String = "",          // Firebase Storage URL or Base64
    val createdAt: Long = System.currentTimeMillis(),
    val approvedAt: Long = 0L           // Set when admin approves
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "uid" to uid,
            "fullName" to fullName,
            "email" to email,
            "role" to role,
            "status" to status,
            "studentId" to studentId,
            "college" to college,
            "branch" to branch,
            "year" to year,
            "phone" to phone,
            "parentName" to parentName,
            "parentPhone" to parentPhone,
            "emergencyContact" to emergencyContact,
            "aadharNumber" to aadharNumber,
            "aadharCardUrl" to aadharCardUrl,
            "aadharUploadedAt" to aadharUploadedAt,
            "photoUrl" to photoUrl,
            "createdAt" to createdAt,
            "approvedAt" to approvedAt
        )
    }

    val isStudent: Boolean get() = role.equals("Student", ignoreCase = true)
    val isAdmin: Boolean get() = role.equals("Admin", ignoreCase = true)
    val isPending: Boolean get() = status.equals("Pending", ignoreCase = true)
    val isApproved: Boolean get() = status.equals("Approved", ignoreCase = true)
    val isRejected: Boolean get() = status.equals("Rejected", ignoreCase = true)
    val isSuspended: Boolean get() = status.equals("Suspended", ignoreCase = true)

    val displayStudentId: String
        get() {
            if (studentId.isNotBlank()) return studentId
            val year = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
            val seq = String.format(java.util.Locale.getDefault(), "%03d", (Math.abs(uid.hashCode()) % 100) + 1)
            return "RWH-$year-$seq"
        }

    val displayAdminId: String
        get() {
            if (studentId.isNotBlank() && studentId.startsWith("RWH-ADM")) return studentId
            val year = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
            val seq = String.format(java.util.Locale.getDefault(), "%03d", (Math.abs(uid.hashCode()) % 50) + 1)
            return "RWH-ADM-$year-$seq"
        }

    val displayId: String
        get() = if (isAdmin) displayAdminId else displayStudentId

    /** Initials for avatar fallback (e.g. "Raj Kumar" → "RK") */
    val initials: String
        get() {
            val parts = fullName.trim().split(" ").filter { it.isNotBlank() }
            return when {
                parts.size >= 2 -> "${parts.first().first()}${parts.last().first()}".uppercase()
                parts.size == 1 -> parts.first().take(2).uppercase()
                else -> if (isAdmin) "AD" else "ST"
            }
        }

    companion object {
        fun fromMap(map: Map<String, Any?>): UserProfile {
            return UserProfile(
                uid = map["uid"] as? String ?: "",
                fullName = map["fullName"] as? String ?: "",
                email = map["email"] as? String ?: "",
                role = map["role"] as? String ?: "Student",
                status = map["status"] as? String ?: "Pending",
                studentId = map["studentId"] as? String ?: "",
                college = map["college"] as? String ?: "",
                branch = map["branch"] as? String ?: "",
                year = map["year"] as? String ?: "",
                phone = map["phone"] as? String ?: "",
                parentName = map["parentName"] as? String ?: "",
                parentPhone = map["parentPhone"] as? String ?: "",
                emergencyContact = map["emergencyContact"] as? String ?: "",
                aadharNumber = map["aadharNumber"] as? String ?: "",
                aadharCardUrl = map["aadharCardUrl"] as? String ?: "",
                aadharUploadedAt = (map["aadharUploadedAt"] as? Number)?.toLong() ?: 0L,
                photoUrl = map["photoUrl"] as? String ?: "",
                createdAt = (map["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
                approvedAt = (map["approvedAt"] as? Number)?.toLong() ?: 0L
            )
        }
    }
}
