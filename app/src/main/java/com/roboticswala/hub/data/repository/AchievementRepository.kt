package com.roboticswala.hub.data.repository

import com.roboticswala.hub.data.models.Achievement
import com.roboticswala.hub.utils.Resource
import kotlinx.coroutines.flow.Flow

interface AchievementRepository {

    fun submitAchievement(
        achievement: Achievement,
        certificateBytes: ByteArray? = null,
        certificateFileName: String? = null,
        imageBytes: ByteArray? = null
    ): Flow<Resource<Achievement>>

    fun updateAchievement(
        achievement: Achievement,
        certificateBytes: ByteArray? = null,
        certificateFileName: String? = null,
        imageBytes: ByteArray? = null
    ): Flow<Resource<Unit>>

    fun deleteAchievement(
        achievementId: String,
        studentUid: String
    ): Flow<Resource<Unit>>

    fun observeStudentAchievements(
        studentUid: String
    ): Flow<List<Achievement>>

    fun observeApprovedAchievements(
        studentUid: String
    ): Flow<List<Achievement>>

    fun observeAllAdminAchievements(): Flow<List<Achievement>>

    fun adminApproveAchievement(
        achievementId: String,
        adminUid: String,
        adminName: String
    ): Flow<Resource<Unit>>

    fun adminRejectAchievement(
        achievementId: String,
        rejectionReason: String
    ): Flow<Resource<Unit>>

    fun adminReturnForCorrection(
        achievementId: String,
        correctionMessage: String
    ): Flow<Resource<Unit>>
}
