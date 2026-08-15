package com.roboticswala.hub.data.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties

/**
 * Real Firestore Lab Slot Booking model stored in collection "labBookings".
 */
@IgnoreExtraProperties
data class LabBooking(
    @DocumentId
    val bookingId: String = "",
    val studentUid: String = "",
    val studentId: String = "",
    val studentName: String = "",
    val bookingDate: String = "",       // Format: "yyyy-MM-dd"
    val date: String = "",              // Compatibility alias for bookingDate
    val startTime: String = "",         // Format: "10:00 AM" or "10:00"
    val endTime: String = "",           // Format: "12:00 PM" or "12:00"
    val startMinutes: Int = 0,          // Minutes from midnight (e.g. 600 for 10:00 AM)
    val endMinutes: Int = 0,            // Minutes from midnight (e.g. 720 for 12:00 PM)
    val projectName: String = "",
    val workDescription: String = "",
    val requiredEquipment: String = "", // e.g. "TurtleBot4, 3D Printer, LiDAR Bench"
    val teamMembers: String = "",       // Comma-separated names or student IDs
    val status: String = STATUS_PENDING, // "Pending", "Approved", "Rejected", "Cancelled"
    val rejectionReason: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val approvedAt: Long? = null,
    val approvedBy: String? = null,
    val rejectedAt: Long? = null,
    val cancelledAt: Long? = null
) {
    companion object {
        const val STATUS_PENDING = "Pending"
        const val STATUS_APPROVED = "Approved"
        const val STATUS_REJECTED = "Rejected"
        const val STATUS_CANCELLED = "Cancelled"

        fun fromMap(id: String, map: Map<String, Any?>): LabBooking {
            val d = (map["bookingDate"] as? String) ?: (map["date"] as? String) ?: ""
            val sTime = (map["startTime"] as? String) ?: ""
            val eTime = (map["endTime"] as? String) ?: ""
            return LabBooking(
                bookingId = id,
                studentUid = (map["studentUid"] as? String) ?: "",
                studentId = (map["studentId"] as? String) ?: "",
                studentName = (map["studentName"] as? String) ?: "",
                bookingDate = d,
                date = d,
                startTime = sTime,
                endTime = eTime,
                startMinutes = (map["startMinutes"] as? Number)?.toInt() ?: 0,
                endMinutes = (map["endMinutes"] as? Number)?.toInt() ?: 0,
                projectName = (map["projectName"] as? String) ?: "",
                workDescription = (map["workDescription"] as? String) ?: "",
                requiredEquipment = (map["requiredEquipment"] as? String) ?: "",
                teamMembers = (map["teamMembers"] as? String) ?: "",
                status = (map["status"] as? String) ?: STATUS_PENDING,
                rejectionReason = map["rejectionReason"] as? String,
                createdAt = (map["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
                approvedAt = (map["approvedAt"] as? Number)?.toLong(),
                approvedBy = map["approvedBy"] as? String,
                rejectedAt = (map["rejectedAt"] as? Number)?.toLong(),
                cancelledAt = (map["cancelledAt"] as? Number)?.toLong()
            )
        }
    }

    val isPending: Boolean get() = status.equals(STATUS_PENDING, ignoreCase = true)
    val isApproved: Boolean get() = status.equals(STATUS_APPROVED, ignoreCase = true)
    val isRejected: Boolean get() = status.equals(STATUS_REJECTED, ignoreCase = true)
    val isCancelled: Boolean get() = status.equals(STATUS_CANCELLED, ignoreCase = true)

    val timeRangeDisplay: String
        get() = "$startTime - $endTime"

    val durationHoursMinutes: String
        get() {
            val totalMins = if (endMinutes > startMinutes) endMinutes - startMinutes else 0
            val hours = totalMins / 60
            val mins = totalMins % 60
            return if (hours > 0) {
                if (mins > 0) "${hours}h ${mins}m" else "${hours} hrs"
            } else {
                "${mins} mins"
            }
        }
}
