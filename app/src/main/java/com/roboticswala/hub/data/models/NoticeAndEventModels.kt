package com.roboticswala.hub.data.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Notice(
    @DocumentId
    val noticeId: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = CATEGORY_GENERAL,
    val priority: String = PRIORITY_NORMAL,
    val targetAudience: String = AUDIENCE_ALL,
    val selectedStudentUids: List<String> = emptyList(),
    val publishDate: String = "",       // Format: "yyyy-MM-dd"
    val expiryDate: String = "",        // Format: "yyyy-MM-dd"
    val status: String = STATUS_PUBLISHED, // "Published", "Draft", "Unpublished"
    val attachmentUrl: String = "",
    val attachmentFileName: String = "",
    val imageUrl: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val publishedAt: Long? = System.currentTimeMillis(),
    val createdByAdminUid: String = ""
) {
    companion object {
        // Categories
        const val CATEGORY_GENERAL = "General"
        const val CATEGORY_IMPORTANT = "Important"
        const val CATEGORY_LAB = "Lab"
        const val CATEGORY_PROJECT = "Project"
        const val CATEGORY_EVENT = "Event"
        const val CATEGORY_TRAINING = "Training"
        const val CATEGORY_WORKSHOP = "Workshop"
        const val CATEGORY_COMPETITION = "Competition"
        const val CATEGORY_OTHER = "Other"

        val ALL_CATEGORIES = listOf(
            CATEGORY_GENERAL,
            CATEGORY_IMPORTANT,
            CATEGORY_LAB,
            CATEGORY_PROJECT,
            CATEGORY_EVENT,
            CATEGORY_TRAINING,
            CATEGORY_WORKSHOP,
            CATEGORY_COMPETITION,
            CATEGORY_OTHER
        )

        // Priorities
        const val PRIORITY_LOW = "Low"
        const val PRIORITY_NORMAL = "Normal"
        const val PRIORITY_HIGH = "High"
        const val PRIORITY_URGENT = "Urgent"

        val ALL_PRIORITIES = listOf(
            PRIORITY_LOW,
            PRIORITY_NORMAL,
            PRIORITY_HIGH,
            PRIORITY_URGENT
        )

        // Target Audiences
        const val AUDIENCE_ALL = "All Students"
        const val AUDIENCE_SELECTED = "Selected Students"
        const val AUDIENCE_ADMIN_ONLY = "Admin Only"

        val ALL_AUDIENCES = listOf(
            AUDIENCE_ALL,
            AUDIENCE_SELECTED,
            AUDIENCE_ADMIN_ONLY
        )

        // Status
        const val STATUS_PUBLISHED = "Published"
        const val STATUS_DRAFT = "Draft"
        const val STATUS_UNPUBLISHED = "Unpublished"

        val ALL_STATUSES = listOf(
            STATUS_PUBLISHED,
            STATUS_DRAFT,
            STATUS_UNPUBLISHED
        )
    }

    val isPublished: Boolean get() = status.equals(STATUS_PUBLISHED, ignoreCase = true)
    val isUrgent: Boolean get() = priority.equals(PRIORITY_URGENT, ignoreCase = true) || priority.equals(PRIORITY_HIGH, ignoreCase = true)

    fun toNoticeItem(): NoticeItem {
        return NoticeItem(
            id = noticeId,
            title = title,
            description = description,
            date = publishDate,
            priority = priority
        )
    }
}

@IgnoreExtraProperties
data class LabEvent(
    @DocumentId
    val eventId: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = CATEGORY_ROBOTICS,
    val eventDate: String = "",         // Format: "yyyy-MM-dd"
    val startTime: String = "",         // Format: "10:00 AM"
    val endTime: String = "",           // Format: "12:00 PM"
    val location: String = "",
    val organizerName: String = "",
    val maximumParticipants: Int = 0,   // 0 = unlimited
    val registrationDeadline: String = "", // Format: "yyyy-MM-dd"
    val eventImageUrl: String = "",
    val attachmentUrl: String = "",
    val externalRegistrationLink: String = "",
    val eventStatus: String = STATUS_UPCOMING, // "Upcoming", "Ongoing", "Completed", "Cancelled"
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val createdByAdminUid: String = "",
    val cancelledAt: Long? = null,
    val completedAt: Long? = null,
    val registeredCount: Int = 0
) {
    companion object {
        // Categories
        const val CATEGORY_ROBOTICS = "Robotics"
        const val CATEGORY_WORKSHOP = "Workshop"
        const val CATEGORY_HACKATHON = "Hackathon"
        const val CATEGORY_COMPETITION = "Competition"
        const val CATEGORY_TRAINING = "Training"
        const val CATEGORY_SEMINAR = "Seminar"
        const val CATEGORY_WEBINAR = "Webinar"
        const val CATEGORY_EXHIBITION = "Exhibition"
        const val CATEGORY_OTHER = "Other"

        val ALL_CATEGORIES = listOf(
            CATEGORY_ROBOTICS,
            CATEGORY_WORKSHOP,
            CATEGORY_HACKATHON,
            CATEGORY_COMPETITION,
            CATEGORY_TRAINING,
            CATEGORY_SEMINAR,
            CATEGORY_WEBINAR,
            CATEGORY_EXHIBITION,
            CATEGORY_OTHER
        )

        // Status
        const val STATUS_UPCOMING = "Upcoming"
        const val STATUS_ONGOING = "Ongoing"
        const val STATUS_COMPLETED = "Completed"
        const val STATUS_CANCELLED = "Cancelled"

        val ALL_STATUSES = listOf(
            STATUS_UPCOMING,
            STATUS_ONGOING,
            STATUS_COMPLETED,
            STATUS_CANCELLED
        )
    }

    val isUpcoming: Boolean get() = eventStatus.equals(STATUS_UPCOMING, ignoreCase = true)
    val isOngoing: Boolean get() = eventStatus.equals(STATUS_ONGOING, ignoreCase = true)
    val isCompleted: Boolean get() = eventStatus.equals(STATUS_COMPLETED, ignoreCase = true)
    val isCancelled: Boolean get() = eventStatus.equals(STATUS_CANCELLED, ignoreCase = true)

    val isFull: Boolean get() = maximumParticipants > 0 && registeredCount >= maximumParticipants

    fun toEventItem(): EventItem {
        return EventItem(
            id = eventId,
            title = title,
            date = eventDate,
            time = "$startTime - $endTime",
            location = location
        )
    }
}

@IgnoreExtraProperties
data class EventRegistration(
    @DocumentId
    val studentUid: String = "",
    val studentId: String = "",
    val studentName: String = "",
    val registeredAt: Long = System.currentTimeMillis(),
    val registrationStatus: String = "Registered" // "Registered", "Cancelled"
)
