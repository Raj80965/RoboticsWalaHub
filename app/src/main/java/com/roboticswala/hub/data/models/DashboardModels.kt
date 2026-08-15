package com.roboticswala.hub.data.models

// ─────────────────────────────────────────────
// Day 5: Real Firestore-Driven Student Models
// ─────────────────────────────────────────────

/**
 * Attendance data from Firestore: /attendance/{uid}
 */
data class AttendanceData(
    val attendancePercentage: Double = 0.0,
    val presentDays: Int = 0,
    val totalDays: Int = 0,
    val totalWorkingHours: Int = 0
) {
    companion object {
        fun fromMap(map: Map<String, Any?>): AttendanceData {
            return AttendanceData(
                attendancePercentage = (map["attendancePercentage"] as? Number)?.toDouble() ?: 0.0,
                presentDays = (map["presentDays"] as? Number)?.toInt() ?: 0,
                totalDays = (map["totalDays"] as? Number)?.toInt() ?: 0,
                totalWorkingHours = (map["totalWorkingHours"] as? Number)?.toInt() ?: 0
            )
        }
    }
}



/**
 * Student project from Firestore: /projects/{projectId}
 * Query by studentUid
 */
data class StudentProject(
    val id: String = "",
    val title: String = "",
    val status: String = "",         // "Active", "Completed", "On Hold"
    val progressPercent: Int = 0,
    val createdAt: Long = 0L
) {
    companion object {
        fun fromMap(id: String, map: Map<String, Any?>): StudentProject {
            return StudentProject(
                id = id,
                title = map["title"] as? String ?: "",
                status = map["status"] as? String ?: "",
                progressPercent = (map["progressPercent"] as? Number)?.toInt() ?: 0,
                createdAt = (map["createdAt"] as? Number)?.toLong() ?: 0L
            )
        }
    }
}

/**
 * Task assigned to student from Firestore: /tasks/{taskId}
 * Query by studentUid
 */
data class StudentTask(
    val id: String = "",
    val title: String = "",
    val priority: String = "Medium",   // "High", "Medium", "Low"
    val deadline: String = "",         // Display string e.g. "Aug 20, 2026"
    val status: String = "Pending"     // "Pending", "In Progress", "Done"
) {
    companion object {
        fun fromMap(id: String, map: Map<String, Any?>): StudentTask {
            return StudentTask(
                id = id,
                title = map["title"] as? String ?: "",
                priority = map["priority"] as? String ?: "Medium",
                deadline = map["deadline"] as? String ?: "",
                status = map["status"] as? String ?: "Pending"
            )
        }
    }
}

// ─────────────────────────────────────────────
// Shared Models (used by both Home and other tabs)
// ─────────────────────────────────────────────

data class NoticeItem(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val date: String = "",
    val tag: String = "",
    val priority: String = "Normal",   // "Urgent", "Normal"
    val isUrgent: Boolean = false
) {
    companion object {
        fun fromMap(id: String, map: Map<String, Any?>): NoticeItem {
            return NoticeItem(
                id = id,
                title = map["title"] as? String ?: "",
                description = map["description"] as? String ?: "",
                date = map["date"] as? String ?: "",
                tag = map["tag"] as? String ?: "",
                priority = map["priority"] as? String ?: "Normal",
                isUrgent = map["isUrgent"] as? Boolean ?: false
            )
        }
    }
}

data class EventItem(
    val id: String = "",
    val title: String = "",
    val date: String = "",           // Display string e.g. "Aug 22, 2026"
    val time: String = "",           // e.g. "10:00 AM - 04:00 PM"
    val location: String = "",
    val dateTimestamp: Long = 0L     // For sorting/filtering
) {
    companion object {
        fun fromMap(id: String, map: Map<String, Any?>): EventItem {
            return EventItem(
                id = id,
                title = map["title"] as? String ?: "",
                date = map["date"] as? String ?: "",
                time = map["time"] as? String ?: "",
                location = map["location"] as? String ?: "",
                dateTimestamp = (map["dateTimestamp"] as? Number)?.toLong() ?: 0L
            )
        }
    }
}

// ─────────────────────────────────────────────
// Projects & Activity Tab Models (preserved from Day 4)
// ─────────────────────────────────────────────

data class ProjectItem(
    val id: String,
    val title: String,
    val category: String,
    val description: String,
    val progress: Int,
    val status: String,
    val techStack: List<String>,
    val teamMembers: Int
)

data class ActivityLogItem(
    val id: String,
    val title: String,
    val location: String,
    val timestamp: String,
    val duration: String,
    val type: String
)

// ─────────────────────────────────────────────
// Admin Models (preserved from Day 4)
// ─────────────────────────────────────────────

data class AdminDashboardData(
    val totalStudents: Int = 0,
    val pendingApprovals: Int = 0,
    val activeStudents: Int = 0,
    val todayAttendancePercentage: Double = 0.0,
    val activeProjects: Int = 0,
    val pendingBookings: Int = 0,
    val lowStockEquipment: List<EquipmentItem> = emptyList()
)

data class EquipmentItem(
    val name: String,
    val stockDetail: String,
    val alertLevel: String,
    val quantity: Int
)

data class StudentDirectoryItem(
    val id: String,
    val name: String,
    val email: String,
    val rfidStatus: String,
    val currentProject: String,
    val attendance: Double,
    val status: String
)

data class BookingRequestItem(
    val id: String,
    val studentName: String,
    val studentId: String,
    val bayOrMachine: String,
    val timeSlot: String,
    val date: String,
    val purpose: String,
    val status: String
)
