package com.roboticswala.hub.data.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties

/**
 * Team member in a project with an assigned role.
 */
@IgnoreExtraProperties
data class ProjectTeamMember(
    val studentUid: String = "",
    val studentId: String = "",
    val studentName: String = "",
    val role: String = ROLE_MEMBER
) {
    companion object {
        const val ROLE_OWNER = "Project Owner"
        const val ROLE_LEAD = "Team Lead"
        const val ROLE_DEVELOPER = "Software / AI Dev"
        const val ROLE_HARDWARE = "Hardware / Robotics Eng"
        const val ROLE_MEMBER = "Team Member"

        val ALL_ROLES = listOf(
            ROLE_OWNER,
            ROLE_LEAD,
            ROLE_DEVELOPER,
            ROLE_HARDWARE,
            ROLE_MEMBER
        )
    }
}

/**
 * Real-time chronological progress update in a project subcollection: /projects/{projectId}/updates/{updateId}
 */
@IgnoreExtraProperties
data class ProjectUpdate(
    @DocumentId
    val updateId: String = "",
    val projectId: String = "",
    val title: String = "",
    val workDescription: String = "",
    val progressPercentage: Int = 0,
    val problemsFaced: String = "",
    val nextSteps: String = "",
    val imageUrl: String? = null,
    val createdByUid: String = "",
    val createdByName: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Real Firestore Project Model stored in collection "projects".
 */
@IgnoreExtraProperties
data class Project(
    @DocumentId
    val projectId: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = CATEGORY_ROBOTICS,
    val type: String = TYPE_HARDWARE,
    val ownerUid: String = "",
    val ownerStudentId: String = "",
    val ownerName: String = "",
    val teamMembers: List<ProjectTeamMember> = emptyList(),
    val mentorName: String = "",
    val startDate: String = "",
    val expectedCompletionDate: String = "",
    val status: String = STATUS_IN_PROGRESS,
    val progressPercentage: Int = 0,
    val requiredComponents: String = "",
    val estimatedBudget: Double = 0.0,
    val actualExpense: Double = 0.0,
    val githubLink: String = "",
    val projectImageUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    companion object {
        const val STATUS_PLANNING = "Planning"
        const val STATUS_IN_PROGRESS = "In Progress"
        const val STATUS_ON_HOLD = "On Hold"
        const val STATUS_COMPLETED = "Completed"

        const val CATEGORY_ROBOTICS = "Robotics"
        const val CATEGORY_IOT = "IoT"
        const val CATEGORY_AI = "AI"
        const val CATEGORY_AUTOMATION = "Automation"
        const val CATEGORY_EMBEDDED = "Embedded Systems"
        const val CATEGORY_OTHER = "Other"

        const val TYPE_HARDWARE = "Hardware"
        const val TYPE_SOFTWARE = "Software"
        const val TYPE_RESEARCH = "Research"
        const val TYPE_COMPETITION = "Competition"
        const val TYPE_COURSEWORK = "Coursework"

        val ALL_CATEGORIES = listOf(
            CATEGORY_ROBOTICS,
            CATEGORY_IOT,
            CATEGORY_AI,
            CATEGORY_AUTOMATION,
            CATEGORY_EMBEDDED,
            CATEGORY_OTHER
        )

        val ALL_STATUSES = listOf(
            STATUS_PLANNING,
            STATUS_IN_PROGRESS,
            STATUS_ON_HOLD,
            STATUS_COMPLETED
        )

        val ALL_TYPES = listOf(
            TYPE_HARDWARE,
            TYPE_SOFTWARE,
            TYPE_RESEARCH,
            TYPE_COMPETITION,
            TYPE_COURSEWORK
        )
    }

    val isCompleted: Boolean
        get() = status.equals(STATUS_COMPLETED, ignoreCase = true) || progressPercentage >= 100

    fun isUserAuthorized(uid: String): Boolean {
        return ownerUid == uid || teamMembers.any { it.studentUid == uid }
    }

    fun isOwner(uid: String): Boolean = ownerUid == uid
}
