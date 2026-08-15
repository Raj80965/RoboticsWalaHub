package com.roboticswala.hub.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.roboticswala.hub.data.models.DailyWorkUpdate
import com.roboticswala.hub.data.models.LabTask
import com.roboticswala.hub.utils.BookingTimeUtils
import com.roboticswala.hub.utils.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class FirestoreWorkAndTaskRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) : WorkAndTaskRepository {

    private val workCollection = firestore.collection("dailyWorkUpdates")
    private val tasksCollection = firestore.collection("tasks")

    // ─────────────────────────────────────────────────────────────────────────
    // 1. Daily Work Progress
    // ─────────────────────────────────────────────────────────────────────────

    override fun createDailyWorkUpdate(
        update: DailyWorkUpdate,
        imageBytes: ByteArray?
    ): Flow<Resource<DailyWorkUpdate>> = flow {
        emit(Resource.Loading())
        try {
            if (update.title.isBlank() || update.description.isBlank()) {
                emit(Resource.Error("Work title and description are required."))
                return@flow
            }
            if (update.hoursWorked <= 0.0) {
                emit(Resource.Error("Hours worked must be greater than 0."))
                return@flow
            }
            if (BookingTimeUtils.isFutureDate(update.workDate)) {
                emit(Resource.Error("Work date cannot be in the future."))
                return@flow
            }

            val docRef = workCollection.document()
            var imageUrlsList = update.imageUrls

            if (imageBytes != null && imageBytes.isNotEmpty()) {
                val storageRef = storage.reference.child("daily_work/${docRef.id}/img_${System.currentTimeMillis()}.jpg")
                storageRef.putBytes(imageBytes).await()
                val downloadUrl = storageRef.downloadUrl.await().toString()
                imageUrlsList = listOf(downloadUrl)
            }

            val finalUpdate = update.copy(
                updateId = docRef.id,
                imageUrls = imageUrlsList,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )

            docRef.set(finalUpdate).await()
            emit(Resource.Success(finalUpdate))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to log daily work update."))
        }
    }

    override fun updateDailyWorkUpdate(
        update: DailyWorkUpdate,
        imageBytes: ByteArray?
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            var imageUrlsList = update.imageUrls
            if (imageBytes != null && imageBytes.isNotEmpty()) {
                val storageRef = storage.reference.child("daily_work/${update.updateId}/img_${System.currentTimeMillis()}.jpg")
                storageRef.putBytes(imageBytes).await()
                val downloadUrl = storageRef.downloadUrl.await().toString()
                imageUrlsList = listOf(downloadUrl)
            }

            val updated = update.copy(
                imageUrls = imageUrlsList,
                updatedAt = System.currentTimeMillis()
            )

            workCollection.document(update.updateId).set(updated).await()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to update work entry."))
        }
    }

    override fun deleteDailyWorkUpdate(
        updateId: String,
        studentUid: String
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val doc = workCollection.document(updateId).get().await()
            if (!doc.exists()) {
                emit(Resource.Error("Work update not found."))
                return@flow
            }
            val existing = doc.toObject(DailyWorkUpdate::class.java)
            if (existing == null || existing.studentUid != studentUid) {
                emit(Resource.Error("Unauthorized: You can only delete your own work logs."))
                return@flow
            }

            workCollection.document(updateId).delete().await()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to delete work log."))
        }
    }

    override fun observeStudentWorkHistory(
        studentUid: String
    ): Flow<List<DailyWorkUpdate>> = callbackFlow {
        val listener = workCollection
            .whereEqualTo("studentUid", studentUid)
            .orderBy("workDate", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { it.toObject(DailyWorkUpdate::class.java) } ?: emptyList()
                trySend(list)
            }

        awaitClose { listener.remove() }
    }

    override fun observeLabWorkFeed(): Flow<List<DailyWorkUpdate>> = callbackFlow {
        val listener = workCollection
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(50)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { it.toObject(DailyWorkUpdate::class.java) } ?: emptyList()
                trySend(list)
            }

        awaitClose { listener.remove() }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 2. Weekly Tasks Management
    // ─────────────────────────────────────────────────────────────────────────

    override fun createTask(task: LabTask): Flow<Resource<LabTask>> = flow {
        emit(Resource.Loading())
        try {
            if (task.title.isBlank() || task.assignedStudentUid.isBlank()) {
                emit(Resource.Error("Task title and assigned student are required."))
                return@flow
            }

            val docRef = tasksCollection.document()
            val finalTask = task.copy(
                taskId = docRef.id,
                status = LabTask.STATUS_PENDING,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )

            docRef.set(finalTask).await()
            emit(Resource.Success(finalTask))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to create task."))
        }
    }

    override fun updateTask(task: LabTask): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            tasksCollection.document(task.taskId).set(
                task.copy(updatedAt = System.currentTimeMillis())
            ).await()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to update task."))
        }
    }

    override fun deleteTask(taskId: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            tasksCollection.document(taskId).delete().await()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to delete task."))
        }
    }

    override fun observeStudentTasks(studentUid: String): Flow<List<LabTask>> = callbackFlow {
        val listener = tasksCollection
            .whereEqualTo("assignedStudentUid", studentUid)
            .orderBy("deadline", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { it.toObject(LabTask::class.java) } ?: emptyList()
                trySend(list)
            }

        awaitClose { listener.remove() }
    }

    override fun observeAllAdminTasks(): Flow<List<LabTask>> = callbackFlow {
        val listener = tasksCollection
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { it.toObject(LabTask::class.java) } ?: emptyList()
                trySend(list)
            }

        awaitClose { listener.remove() }
    }

    override fun startTask(taskId: String, studentUid: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val doc = tasksCollection.document(taskId).get().await()
            val task = doc.toObject(LabTask::class.java)
            if (task == null || task.assignedStudentUid != studentUid) {
                emit(Resource.Error("Unauthorized action."))
                return@flow
            }

            tasksCollection.document(taskId).update(
                mapOf(
                    "status" to LabTask.STATUS_IN_PROGRESS,
                    "updatedAt" to System.currentTimeMillis()
                )
            ).await()

            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to start task."))
        }
    }

    override fun submitTaskWork(
        taskId: String,
        studentUid: String,
        submissionNote: String,
        fileBytes: ByteArray?
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            var fileUrls: List<String> = emptyList()
            if (fileBytes != null && fileBytes.isNotEmpty()) {
                val storageRef = storage.reference.child("task_submissions/$taskId/file_${System.currentTimeMillis()}.jpg")
                storageRef.putBytes(fileBytes).await()
                val url = storageRef.downloadUrl.await().toString()
                fileUrls = listOf(url)
            }

            val map = mutableMapOf<String, Any>(
                "status" to LabTask.STATUS_SUBMITTED,
                "submissionNote" to submissionNote.trim(),
                "submittedAt" to System.currentTimeMillis(),
                "updatedAt" to System.currentTimeMillis()
            )
            if (fileUrls.isNotEmpty()) {
                map["submissionFileUrls"] = fileUrls
            }

            tasksCollection.document(taskId).update(map).await()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to submit task."))
        }
    }

    override fun adminReviewTask(
        taskId: String,
        markCompleted: Boolean,
        feedbackNote: String?
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val status = if (markCompleted) LabTask.STATUS_COMPLETED else LabTask.STATUS_IN_PROGRESS
            val map = mutableMapOf<String, Any>(
                "status" to status,
                "updatedAt" to System.currentTimeMillis()
            )
            if (markCompleted) {
                map["completedAt"] = System.currentTimeMillis()
            }
            if (feedbackNote != null) {
                map["submissionNote"] = feedbackNote
            }

            tasksCollection.document(taskId).update(map).await()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to review task."))
        }
    }
}
