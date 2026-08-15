package com.roboticswala.hub.data.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Achievement(
    @DocumentId
    val achievementId: String = "",
    val studentUid: String = "",
    val studentId: String = "",
    val studentName: String = "",
    val studentProfilePhotoUrl: String = "",
    val title: String = "",
    val category: String = CATEGORY_COMPETITION,
    val description: String = "",
    val achievementDate: String = "",       // Format: "yyyy-MM-dd"
    val organizationName: String = "",
    val achievementLevel: String = LEVEL_COLLEGE,
    val certificateUrl: String = "",
    val certificateFileName: String = "",
    val supportingDocumentUrls: List<String> = emptyList(),
    val achievementImageUrl: String = "",
    val verificationLink: String = "",
    val status: String = STATUS_PENDING,    // "Pending", "Approved", "Rejected", "Needs Correction"
    val rejectionReason: String = "",
    val correctionMessage: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val approvedAt: Long? = null,
    val approvedByAdminUid: String = "",
    val approvedByAdminName: String = "",
    val rejectedAt: Long? = null
) {
    companion object {
        // Categories
        const val CATEGORY_COMPETITION = "Competition"
        const val CATEGORY_HACKATHON = "Hackathon"
        const val CATEGORY_WORKSHOP = "Workshop"
        const val CATEGORY_INTERNSHIP = "Internship"
        const val CATEGORY_CERTIFICATION = "Certification"
        const val CATEGORY_RESEARCH_PAPER = "Research Paper"
        const val CATEGORY_PUBLICATION = "Publication"
        const val CATEGORY_PATENT = "Patent"
        const val CATEGORY_AWARD = "Award"
        const val CATEGORY_OTHER = "Other"

        val ALL_CATEGORIES = listOf(
            CATEGORY_COMPETITION,
            CATEGORY_HACKATHON,
            CATEGORY_WORKSHOP,
            CATEGORY_INTERNSHIP,
            CATEGORY_CERTIFICATION,
            CATEGORY_RESEARCH_PAPER,
            CATEGORY_PUBLICATION,
            CATEGORY_PATENT,
            CATEGORY_AWARD,
            CATEGORY_OTHER
        )

        // Levels
        const val LEVEL_COLLEGE = "College"
        const val LEVEL_DISTRICT = "District"
        const val LEVEL_STATE = "State"
        const val LEVEL_NATIONAL = "National"
        const val LEVEL_INTERNATIONAL = "International"
        const val LEVEL_OTHER = "Other"

        val ALL_LEVELS = listOf(
            LEVEL_COLLEGE,
            LEVEL_DISTRICT,
            LEVEL_STATE,
            LEVEL_NATIONAL,
            LEVEL_INTERNATIONAL,
            LEVEL_OTHER
        )

        // Statuses
        const val STATUS_PENDING = "Pending"
        const val STATUS_APPROVED = "Approved"
        const val STATUS_REJECTED = "Rejected"
        const val STATUS_NEEDS_CORRECTION = "Needs Correction"

        val ALL_STATUSES = listOf(
            STATUS_PENDING,
            STATUS_APPROVED,
            STATUS_REJECTED,
            STATUS_NEEDS_CORRECTION
        )
    }

    val isPending: Boolean get() = status.equals(STATUS_PENDING, ignoreCase = true)
    val isApproved: Boolean get() = status.equals(STATUS_APPROVED, ignoreCase = true)
    val isRejected: Boolean get() = status.equals(STATUS_REJECTED, ignoreCase = true)
    val needsCorrection: Boolean get() = status.equals(STATUS_NEEDS_CORRECTION, ignoreCase = true)

    val canStudentEdit: Boolean get() = isPending || needsCorrection
    val canStudentDelete: Boolean get() = isPending
}
