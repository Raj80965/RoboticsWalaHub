package com.roboticswala.hub.data.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/**
 * Represents an active or archived Lab Attendance Session created by Admin.
 * Stored in Firestore collection: "attendanceSessions"
 */
@IgnoreExtraProperties
data class AttendanceSession(
    @DocumentId
    val sessionId: String = "",
    val labName: String = "",
    val date: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val expiresAt: Long = System.currentTimeMillis() + (5 * 60 * 1000), // Default 5 mins
    @get:PropertyName("isActive")
    val isActive: Boolean = true,
    val createdAdminUid: String = "",
    val sessionToken: String = ""
) {
    val isExpired: Boolean
        get() = System.currentTimeMillis() > expiresAt || !isActive

    val remainingSeconds: Long
        get() {
            val remaining = (expiresAt - System.currentTimeMillis()) / 1000
            return if (remaining > 0) remaining else 0
        }
}

/**
 * Represents a student's attendance check-in / check-out entry for a session.
 * Stored in Firestore collection: "attendanceRecords"
 */
@IgnoreExtraProperties
data class AttendanceRecord(
    @DocumentId
    val recordId: String = "",
    val sessionId: String = "",
    val studentUid: String = "",
    val studentId: String = "",
    val fullName: String = "",
    val labName: String = "",
    val date: String = "",
    val checkInTime: Long = System.currentTimeMillis(),
    val checkOutTime: Long? = null,
    val totalWorkingMinutes: Long = 0L,
    val status: String = STATUS_CHECKED_IN // "Checked In" or "Completed"
) {
    companion object {
        const val STATUS_CHECKED_IN = "Checked In"
        const val STATUS_COMPLETED = "Completed"
    }

    val isCompleted: Boolean
        get() = status == STATUS_COMPLETED

    val formattedDuration: String
        get() {
            if (totalWorkingMinutes <= 0) return "0 min"
            val hours = totalWorkingMinutes / 60
            val minutes = totalWorkingMinutes % 60
            return if (hours > 0) {
                if (minutes > 0) "${hours}h ${minutes}m" else "${hours} hrs"
            } else {
                "${minutes} mins"
            }
        }
}

/**
 * Payload encoded inside the Attendance QR Code.
 */
data class SessionQRData(
    val sessionId: String = "",
    val sessionToken: String = "",
    val labName: String = "",
    val expiresAt: Long = 0L
)

/**
 * Calculated attendance statistics for dashboard & history.
 */
data class AttendanceStats(
    val percentage: Double = 0.0,
    val presentDays: Int = 0,
    val totalDays: Int = 0,
    val totalWorkingHours: Int = 0,
    val totalWorkingMinutes: Long = 0L
)

/**
 * Result state when student scans QR code.
 */
sealed class AttendanceScanResult {
    data class CheckInSuccess(
        val record: AttendanceRecord,
        val message: String = "Check-In Successful"
    ) : AttendanceScanResult()

    data class CheckOutSuccess(
        val record: AttendanceRecord,
        val durationMinutes: Long,
        val message: String = "Check-Out Successful"
    ) : AttendanceScanResult()

    data class AlreadyCompleted(
        val record: AttendanceRecord,
        val message: String = "Attendance already completed for this session"
    ) : AttendanceScanResult()

    data class Error(
        val message: String
    ) : AttendanceScanResult()
}
