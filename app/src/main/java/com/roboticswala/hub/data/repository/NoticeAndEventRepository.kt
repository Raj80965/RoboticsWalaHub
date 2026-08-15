package com.roboticswala.hub.data.repository

import com.roboticswala.hub.data.models.EventRegistration
import com.roboticswala.hub.data.models.LabEvent
import com.roboticswala.hub.data.models.Notice
import com.roboticswala.hub.utils.Resource
import kotlinx.coroutines.flow.Flow

interface NoticeAndEventRepository {

    // ── Notice Board ─────────────────────────────────────────────────────────
    fun createNotice(
        notice: Notice,
        attachmentBytes: ByteArray? = null,
        attachmentFileName: String? = null,
        imageBytes: ByteArray? = null
    ): Flow<Resource<Notice>>

    fun updateNotice(
        notice: Notice,
        attachmentBytes: ByteArray? = null,
        attachmentFileName: String? = null,
        imageBytes: ByteArray? = null
    ): Flow<Resource<Unit>>

    fun deleteNotice(noticeId: String): Flow<Resource<Unit>>

    fun observeActiveNotices(studentUid: String): Flow<List<Notice>>

    fun observeAllAdminNotices(): Flow<List<Notice>>

    // ── Event Management ─────────────────────────────────────────────────────
    fun createEvent(
        event: LabEvent,
        imageBytes: ByteArray? = null
    ): Flow<Resource<LabEvent>>

    fun updateEvent(
        event: LabEvent,
        imageBytes: ByteArray? = null
    ): Flow<Resource<Unit>>

    fun cancelEvent(eventId: String): Flow<Resource<Unit>>

    fun deleteEvent(eventId: String): Flow<Resource<Unit>>

    fun observeAllEvents(): Flow<List<LabEvent>>

    fun observeMyRegisteredEvents(studentUid: String): Flow<List<LabEvent>>

    fun registerForEvent(
        eventId: String,
        studentUid: String,
        studentId: String,
        studentName: String
    ): Flow<Resource<Unit>>

    fun cancelEventRegistration(
        eventId: String,
        studentUid: String
    ): Flow<Resource<Unit>>

    fun checkIsStudentRegistered(
        eventId: String,
        studentUid: String
    ): Flow<Boolean>
}
