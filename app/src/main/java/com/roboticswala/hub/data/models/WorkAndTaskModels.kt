package com.roboticswala.hub.data.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties

/**
 * Real Firestore model for Daily Work Updates stored in collection "dailyWorkUpdates".
 */
@IgnoreExtraProperties
data class DailyWorkUpdate(
    @DocumentId
    val updateId: String = "",
    val studentUid: String = "",
    val studentId: String = "",
    val studentName: String = "",
    val studentProfilePhotoUrl: String = "",
    val title: String = "",
    val description: String = "",
    val relatedProjectId: String = "",
    val relatedProjectName: String = "",
    val hoursWorked: Double = 0.0,
    val problemsFaced: String = "",
    val nextSteps: String = "",
    val workDate: String = "",         // Format: "yyyy-MM-dd"
    val imageUrls: List<String> = emptyList(),
    val documentUrls: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Real Firestore model for Tasks assigned by Admin to Approved Students stored in collection "tasks".
 */
@IgnoreExtraProperties
data class LabTask(
    @DocumentId
    val taskId: String = "",
    val title: String = "",
    val description: String = "",
    val assignedStudentUid: String = "",
    val assignedStudentId: String = "",
    val assignedStudentName: String = "",
    val relatedProjectId: String = "",
    val relatedProjectName: String = "",
    val deadline: String = "",         // Format: "yyyy-MM-dd"
    val priority: String = PRIORITY_MEDIUM, // "Low", "Medium", "High", "Urgent"
    val status: String = STATUS_PENDING,    // "Pending", "In Progress", "Submitted", "Completed"
    val createdByAdminUid: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val submissionNote: String = "",
    val submissionFileUrls: List<String> = emptyList(),
    val submittedAt: Long? = null,
    val completedAt: Long? = null
) {
    companion object {
        const val PRIORITY_LOW = "Low"
        const val PRIORITY_MEDIUM = "Medium"
        const val PRIORITY_HIGH = "High"
        const val PRIORITY_URGENT = "Urgent"

        const val STATUS_PENDING = "Pending"
        const val STATUS_IN_PROGRESS = "In Progress"
        const val STATUS_SUBMITTED = "Submitted"
        const val STATUS_COMPLETED = "Completed"

        val ALL_PRIORITIES = listOf(PRIORITY_LOW, PRIORITY_MEDIUM, PRIORITY_HIGH, PRIORITY_URGENT)
        val ALL_STATUSES = listOf(STATUS_PENDING, STATUS_IN_PROGRESS, STATUS_SUBMITTED, STATUS_COMPLETED)
    }

    val isPending: Boolean get() = status.equals(STATUS_PENDING, ignoreCase = true)
    val isInProgress: Boolean get() = status.equals(STATUS_IN_PROGRESS, ignoreCase = true)
    val isSubmitted: Boolean get() = status.equals(STATUS_SUBMITTED, ignoreCase = true)
    val isCompleted: Boolean get() = status.equals(STATUS_COMPLETED, ignoreCase = true)
    val isOverdue: Boolean get() = !isCompleted && deadline.isNotBlank() && deadline < com.roboticswala.hub.utils.BookingTimeUtils.getTodayDateString()

    fun toStudentTask(): StudentTask {
        return StudentTask(
            id = taskId,
            title = title,
            priority = priority,
            deadline = deadline,
            status = status
        )
    }
}
