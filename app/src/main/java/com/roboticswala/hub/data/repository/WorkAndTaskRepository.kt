package com.roboticswala.hub.data.repository

import com.roboticswala.hub.data.models.DailyWorkUpdate
import com.roboticswala.hub.data.models.LabTask
import com.roboticswala.hub.utils.Resource
import kotlinx.coroutines.flow.Flow

interface WorkAndTaskRepository {

    // ── Daily Work Progress ──────────────────────────────────────────────────
    fun createDailyWorkUpdate(
        update: DailyWorkUpdate,
        imageBytes: ByteArray? = null
    ): Flow<Resource<DailyWorkUpdate>>

    fun updateDailyWorkUpdate(
        update: DailyWorkUpdate,
        imageBytes: ByteArray? = null
    ): Flow<Resource<Unit>>

    fun deleteDailyWorkUpdate(
        updateId: String,
        studentUid: String
    ): Flow<Resource<Unit>>

    fun observeStudentWorkHistory(
        studentUid: String
    ): Flow<List<DailyWorkUpdate>>

    fun observeLabWorkFeed(): Flow<List<DailyWorkUpdate>>

    // ── Tasks Management ─────────────────────────────────────────────────────
    fun createTask(task: LabTask): Flow<Resource<LabTask>>

    fun updateTask(task: LabTask): Flow<Resource<Unit>>

    fun deleteTask(taskId: String): Flow<Resource<Unit>>

    fun observeStudentTasks(studentUid: String): Flow<List<LabTask>>

    fun observeAllAdminTasks(): Flow<List<LabTask>>

    fun startTask(taskId: String, studentUid: String): Flow<Resource<Unit>>

    fun submitTaskWork(
        taskId: String,
        studentUid: String,
        submissionNote: String,
        fileBytes: ByteArray? = null
    ): Flow<Resource<Unit>>

    fun adminReviewTask(
        taskId: String,
        markCompleted: Boolean,
        feedbackNote: String? = null
    ): Flow<Resource<Unit>>
}
