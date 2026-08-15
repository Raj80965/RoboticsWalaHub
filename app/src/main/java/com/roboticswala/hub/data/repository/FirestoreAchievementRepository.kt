package com.roboticswala.hub.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.roboticswala.hub.data.models.Achievement
import com.roboticswala.hub.utils.BookingTimeUtils
import com.roboticswala.hub.utils.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class FirestoreAchievementRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) : AchievementRepository {

    private val achievementsCollection = firestore.collection("achievements")

    override fun submitAchievement(
        achievement: Achievement,
        certificateBytes: ByteArray?,
        certificateFileName: String?,
        imageBytes: ByteArray?
    ): Flow<Resource<Achievement>> = flow {
        emit(Resource.Loading())
        try {
            if (achievement.title.isBlank() || achievement.organizationName.isBlank()) {
                emit(Resource.Error("Achievement title and organization name are required."))
                return@flow
            }
            if (BookingTimeUtils.isFutureDate(achievement.achievementDate)) {
                emit(Resource.Error("Achievement date cannot be in the future."))
                return@flow
            }
            if (achievement.verificationLink.isNotBlank() &&
                !achievement.verificationLink.startsWith("http://") &&
                !achievement.verificationLink.startsWith("https://")
            ) {
                emit(Resource.Error("Verification link must start with http:// or https://"))
                return@flow
            }

            val docRef = achievementsCollection.document()
            var finalCertUrl = achievement.certificateUrl
            var finalCertName = certificateFileName ?: achievement.certificateFileName
            var finalImageUrl = achievement.achievementImageUrl

            // Upload Certificate file if provided
            if (certificateBytes != null && certificateBytes.isNotEmpty()) {
                val ext = if (finalCertName.endsWith(".pdf", ignoreCase = true)) "pdf" else "jpg"
                val certStorageRef = storage.reference.child("achievements/${achievement.studentUid}/${docRef.id}/cert_${System.currentTimeMillis()}.$ext")
                certStorageRef.putBytes(certificateBytes).await()
                finalCertUrl = certStorageRef.downloadUrl.await().toString()
            }

            // Upload Image file if provided
            if (imageBytes != null && imageBytes.isNotEmpty()) {
                val imgStorageRef = storage.reference.child("achievements/${achievement.studentUid}/${docRef.id}/img_${System.currentTimeMillis()}.jpg")
                imgStorageRef.putBytes(imageBytes).await()
                finalImageUrl = imgStorageRef.downloadUrl.await().toString()
            }

            val finalAchievement = achievement.copy(
                achievementId = docRef.id,
                certificateUrl = finalCertUrl,
                certificateFileName = finalCertName,
                achievementImageUrl = finalImageUrl,
                status = Achievement.STATUS_PENDING,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )

            docRef.set(finalAchievement).await()
            emit(Resource.Success(finalAchievement))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to submit achievement."))
        }
    }

    override fun updateAchievement(
        achievement: Achievement,
        certificateBytes: ByteArray?,
        certificateFileName: String?,
        imageBytes: ByteArray?
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            if (BookingTimeUtils.isFutureDate(achievement.achievementDate)) {
                emit(Resource.Error("Achievement date cannot be in the future."))
                return@flow
            }

            var finalCertUrl = achievement.certificateUrl
            var finalCertName = certificateFileName ?: achievement.certificateFileName
            var finalImageUrl = achievement.achievementImageUrl

            if (certificateBytes != null && certificateBytes.isNotEmpty()) {
                val ext = if (finalCertName.endsWith(".pdf", ignoreCase = true)) "pdf" else "jpg"
                val certStorageRef = storage.reference.child("achievements/${achievement.studentUid}/${achievement.achievementId}/cert_${System.currentTimeMillis()}.$ext")
                certStorageRef.putBytes(certificateBytes).await()
                finalCertUrl = certStorageRef.downloadUrl.await().toString()
            }

            if (imageBytes != null && imageBytes.isNotEmpty()) {
                val imgStorageRef = storage.reference.child("achievements/${achievement.studentUid}/${achievement.achievementId}/img_${System.currentTimeMillis()}.jpg")
                imgStorageRef.putBytes(imageBytes).await()
                finalImageUrl = imgStorageRef.downloadUrl.await().toString()
            }

            val updated = achievement.copy(
                certificateUrl = finalCertUrl,
                certificateFileName = finalCertName,
                achievementImageUrl = finalImageUrl,
                status = Achievement.STATUS_PENDING, // reset to pending on resubmission
                correctionMessage = "",
                updatedAt = System.currentTimeMillis()
            )

            achievementsCollection.document(achievement.achievementId).set(updated).await()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to update achievement."))
        }
    }

    override fun deleteAchievement(
        achievementId: String,
        studentUid: String
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val doc = achievementsCollection.document(achievementId).get().await()
            if (!doc.exists()) {
                emit(Resource.Error("Achievement not found."))
                return@flow
            }
            val existing = doc.toObject(Achievement::class.java)
            if (existing == null || existing.studentUid != studentUid) {
                emit(Resource.Error("Unauthorized: You can only delete your own achievements."))
                return@flow
            }
            if (existing.isApproved) {
                emit(Resource.Error("Approved achievements cannot be deleted."))
                return@flow
            }

            achievementsCollection.document(achievementId).delete().await()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to delete achievement."))
        }
    }

    override fun observeStudentAchievements(
        studentUid: String
    ): Flow<List<Achievement>> = callbackFlow {
        val listener = achievementsCollection
            .whereEqualTo("studentUid", studentUid)
            .orderBy("achievementDate", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { it.toObject(Achievement::class.java) } ?: emptyList()
                trySend(list)
            }

        awaitClose { listener.remove() }
    }

    override fun observeApprovedAchievements(
        studentUid: String
    ): Flow<List<Achievement>> = callbackFlow {
        val listener = achievementsCollection
            .whereEqualTo("studentUid", studentUid)
            .whereEqualTo("status", Achievement.STATUS_APPROVED)
            .orderBy("achievementDate", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { it.toObject(Achievement::class.java) } ?: emptyList()
                trySend(list)
            }

        awaitClose { listener.remove() }
    }

    override fun observeAllAdminAchievements(): Flow<List<Achievement>> = callbackFlow {
        val listener = achievementsCollection
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { it.toObject(Achievement::class.java) } ?: emptyList()
                trySend(list)
            }

        awaitClose { listener.remove() }
    }

    override fun adminApproveAchievement(
        achievementId: String,
        adminUid: String,
        adminName: String
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            achievementsCollection.document(achievementId).update(
                mapOf(
                    "status" to Achievement.STATUS_APPROVED,
                    "approvedAt" to System.currentTimeMillis(),
                    "approvedByAdminUid" to adminUid,
                    "approvedByAdminName" to adminName,
                    "rejectionReason" to "",
                    "correctionMessage" to "",
                    "updatedAt" to System.currentTimeMillis()
                )
            ).await()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to approve achievement."))
        }
    }

    override fun adminRejectAchievement(
        achievementId: String,
        rejectionReason: String
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            achievementsCollection.document(achievementId).update(
                mapOf(
                    "status" to Achievement.STATUS_REJECTED,
                    "rejectedAt" to System.currentTimeMillis(),
                    "rejectionReason" to rejectionReason.trim(),
                    "updatedAt" to System.currentTimeMillis()
                )
            ).await()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to reject achievement."))
        }
    }

    override fun adminReturnForCorrection(
        achievementId: String,
        correctionMessage: String
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            achievementsCollection.document(achievementId).update(
                mapOf(
                    "status" to Achievement.STATUS_NEEDS_CORRECTION,
                    "correctionMessage" to correctionMessage.trim(),
                    "updatedAt" to System.currentTimeMillis()
                )
            ).await()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to return achievement for correction."))
        }
    }
}
