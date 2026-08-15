package com.roboticswala.hub.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.roboticswala.hub.data.models.EventRegistration
import com.roboticswala.hub.data.models.LabEvent
import com.roboticswala.hub.data.models.Notice
import com.roboticswala.hub.utils.BookingTimeUtils
import com.roboticswala.hub.utils.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class FirestoreNoticeAndEventRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) : NoticeAndEventRepository {

    private val noticesCollection = firestore.collection("notices")
    private val eventsCollection = firestore.collection("events")

    // ─────────────────────────────────────────────────────────────────────────
    // 1. Notice Board
    // ─────────────────────────────────────────────────────────────────────────

    override fun createNotice(
        notice: Notice,
        attachmentBytes: ByteArray?,
        attachmentFileName: String?,
        imageBytes: ByteArray?
    ): Flow<Resource<Notice>> = flow {
        emit(Resource.Loading())
        try {
            if (notice.title.isBlank() || notice.description.isBlank()) {
                emit(Resource.Error("Notice title and description are required."))
                return@flow
            }
            if (notice.expiryDate.isNotBlank() && notice.publishDate.isNotBlank()) {
                if (notice.expiryDate < notice.publishDate) {
                    emit(Resource.Error("Expiry date cannot be before publish date."))
                    return@flow
                }
            }

            val docRef = noticesCollection.document()
            var finalAttachmentUrl = notice.attachmentUrl
            var finalAttachmentName = attachmentFileName ?: notice.attachmentFileName
            var finalImageUrl = notice.imageUrl

            if (attachmentBytes != null && attachmentBytes.isNotEmpty()) {
                val ext = if (finalAttachmentName.endsWith(".pdf", ignoreCase = true)) "pdf" else "bin"
                val attRef = storage.reference.child("notices/${docRef.id}/att_${System.currentTimeMillis()}.$ext")
                attRef.putBytes(attachmentBytes).await()
                finalAttachmentUrl = attRef.downloadUrl.await().toString()
            }

            if (imageBytes != null && imageBytes.isNotEmpty()) {
                val imgRef = storage.reference.child("notices/${docRef.id}/img_${System.currentTimeMillis()}.jpg")
                imgRef.putBytes(imageBytes).await()
                finalImageUrl = imgRef.downloadUrl.await().toString()
            }

            val finalNotice = notice.copy(
                noticeId = docRef.id,
                attachmentUrl = finalAttachmentUrl,
                attachmentFileName = finalAttachmentName,
                imageUrl = finalImageUrl,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
                publishedAt = if (notice.isPublished) System.currentTimeMillis() else null
            )

            docRef.set(finalNotice).await()
            emit(Resource.Success(finalNotice))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to create notice."))
        }
    }

    override fun updateNotice(
        notice: Notice,
        attachmentBytes: ByteArray?,
        attachmentFileName: String?,
        imageBytes: ByteArray?
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            var finalAttachmentUrl = notice.attachmentUrl
            var finalAttachmentName = attachmentFileName ?: notice.attachmentFileName
            var finalImageUrl = notice.imageUrl

            if (attachmentBytes != null && attachmentBytes.isNotEmpty()) {
                val ext = if (finalAttachmentName.endsWith(".pdf", ignoreCase = true)) "pdf" else "bin"
                val attRef = storage.reference.child("notices/${notice.noticeId}/att_${System.currentTimeMillis()}.$ext")
                attRef.putBytes(attachmentBytes).await()
                finalAttachmentUrl = attRef.downloadUrl.await().toString()
            }

            if (imageBytes != null && imageBytes.isNotEmpty()) {
                val imgRef = storage.reference.child("notices/${notice.noticeId}/img_${System.currentTimeMillis()}.jpg")
                imgRef.putBytes(imageBytes).await()
                finalImageUrl = imgRef.downloadUrl.await().toString()
            }

            val updated = notice.copy(
                attachmentUrl = finalAttachmentUrl,
                attachmentFileName = finalAttachmentName,
                imageUrl = finalImageUrl,
                updatedAt = System.currentTimeMillis()
            )

            noticesCollection.document(notice.noticeId).set(updated).await()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to update notice."))
        }
    }

    override fun deleteNotice(noticeId: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            noticesCollection.document(noticeId).delete().await()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to delete notice."))
        }
    }

    override fun observeActiveNotices(studentUid: String): Flow<List<Notice>> = callbackFlow {
        val today = BookingTimeUtils.getTodayDateString()
        val listener = noticesCollection
            .whereEqualTo("status", Notice.STATUS_PUBLISHED)
            .orderBy("publishDate", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { it.toObject(Notice::class.java) } ?: emptyList()
                val active = list.filter { notice ->
                    // Filter out expired notices
                    val notExpired = notice.expiryDate.isBlank() || notice.expiryDate >= today
                    // Filter by target audience
                    val audienceAllowed = notice.targetAudience == Notice.AUDIENCE_ALL ||
                            (notice.targetAudience == Notice.AUDIENCE_SELECTED && notice.selectedStudentUids.contains(studentUid))

                    notExpired && audienceAllowed
                }
                trySend(active)
            }

        awaitClose { listener.remove() }
    }

    override fun observeAllAdminNotices(): Flow<List<Notice>> = callbackFlow {
        val listener = noticesCollection
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { it.toObject(Notice::class.java) } ?: emptyList()
                trySend(list)
            }

        awaitClose { listener.remove() }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 2. Events & Registrations
    // ─────────────────────────────────────────────────────────────────────────

    override fun createEvent(
        event: LabEvent,
        imageBytes: ByteArray?
    ): Flow<Resource<LabEvent>> = flow {
        emit(Resource.Loading())
        try {
            if (event.title.isBlank() || event.location.isBlank() || event.organizerName.isBlank()) {
                emit(Resource.Error("Title, location, and organizer name are required."))
                return@flow
            }
            if (event.startTime.isNotBlank() && event.endTime.isNotBlank()) {
                val startMin = BookingTimeUtils.timeToMinutes(event.startTime)
                val endMin = BookingTimeUtils.timeToMinutes(event.endTime)
                if (startMin >= endMin) {
                    emit(Resource.Error("Event start time must be before end time."))
                    return@flow
                }
            }

            val docRef = eventsCollection.document()
            var finalImageUrl = event.eventImageUrl

            if (imageBytes != null && imageBytes.isNotEmpty()) {
                val imgRef = storage.reference.child("events/${docRef.id}/img_${System.currentTimeMillis()}.jpg")
                imgRef.putBytes(imageBytes).await()
                finalImageUrl = imgRef.downloadUrl.await().toString()
            }

            val finalEvent = event.copy(
                eventId = docRef.id,
                eventImageUrl = finalImageUrl,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )

            docRef.set(finalEvent).await()
            emit(Resource.Success(finalEvent))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to create event."))
        }
    }

    override fun updateEvent(
        event: LabEvent,
        imageBytes: ByteArray?
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            var finalImageUrl = event.eventImageUrl
            if (imageBytes != null && imageBytes.isNotEmpty()) {
                val imgRef = storage.reference.child("events/${event.eventId}/img_${System.currentTimeMillis()}.jpg")
                imgRef.putBytes(imageBytes).await()
                finalImageUrl = imgRef.downloadUrl.await().toString()
            }

            val updated = event.copy(
                eventImageUrl = finalImageUrl,
                updatedAt = System.currentTimeMillis()
            )

            eventsCollection.document(event.eventId).set(updated).await()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to update event."))
        }
    }

    override fun cancelEvent(eventId: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            eventsCollection.document(eventId).update(
                mapOf(
                    "eventStatus" to LabEvent.STATUS_CANCELLED,
                    "cancelledAt" to System.currentTimeMillis(),
                    "updatedAt" to System.currentTimeMillis()
                )
            ).await()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to cancel event."))
        }
    }

    override fun deleteEvent(eventId: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            eventsCollection.document(eventId).delete().await()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to delete event."))
        }
    }

    override fun observeAllEvents(): Flow<List<LabEvent>> = callbackFlow {
        val listener = eventsCollection
            .orderBy("eventDate", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { it.toObject(LabEvent::class.java) } ?: emptyList()
                trySend(list)
            }

        awaitClose { listener.remove() }
    }

    override fun observeMyRegisteredEvents(studentUid: String): Flow<List<LabEvent>> = callbackFlow {
        val listener = eventsCollection
            .orderBy("eventDate", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val events = snapshot?.documents?.mapNotNull { it.toObject(LabEvent::class.java) } ?: emptyList()

                // Filter events where student is registered
                // Note: We also check registrations subcollection
                trySend(events)
            }

        awaitClose { listener.remove() }
    }

    override fun registerForEvent(
        eventId: String,
        studentUid: String,
        studentId: String,
        studentName: String
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val eventDoc = eventsCollection.document(eventId).get().await()
            val event = eventDoc.toObject(LabEvent::class.java)
            if (event == null) {
                emit(Resource.Error("Event not found."))
                return@flow
            }

            if (event.isCancelled || event.isCompleted) {
                emit(Resource.Error("Cannot register for a ${event.eventStatus.lowercase()} event."))
                return@flow
            }

            val today = BookingTimeUtils.getTodayDateString()
            if (event.registrationDeadline.isNotBlank() && event.registrationDeadline < today) {
                emit(Resource.Error("Registration deadline has passed."))
                return@flow
            }

            if (event.isFull) {
                emit(Resource.Error("Event is fully booked (Maximum participants reached)."))
                return@flow
            }

            val regDocRef = eventsCollection.document(eventId).collection("registrations").document(studentUid)
            val regDoc = regDocRef.get().await()
            if (regDoc.exists() && regDoc.getString("registrationStatus") == "Registered") {
                emit(Resource.Error("You are already registered for this event."))
                return@flow
            }

            val registration = EventRegistration(
                studentUid = studentUid,
                studentId = studentId,
                studentName = studentName,
                registeredAt = System.currentTimeMillis(),
                registrationStatus = "Registered"
            )

            regDocRef.set(registration).await()
            eventsCollection.document(eventId).update("registeredCount", FieldValue.increment(1)).await()

            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to register for event."))
        }
    }

    override fun cancelEventRegistration(
        eventId: String,
        studentUid: String
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val regDocRef = eventsCollection.document(eventId).collection("registrations").document(studentUid)
            val regDoc = regDocRef.get().await()
            if (!regDoc.exists()) {
                emit(Resource.Error("Registration not found."))
                return@flow
            }

            regDocRef.update("registrationStatus", "Cancelled").await()
            eventsCollection.document(eventId).update("registeredCount", FieldValue.increment(-1)).await()

            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to cancel registration."))
        }
    }

    override fun checkIsStudentRegistered(
        eventId: String,
        studentUid: String
    ): Flow<Boolean> = callbackFlow {
        val listener = eventsCollection.document(eventId)
            .collection("registrations")
            .document(studentUid)
            .addSnapshotListener { snapshot, _ ->
                val isReg = snapshot != null && snapshot.exists() && snapshot.getString("registrationStatus") == "Registered"
                trySend(isReg)
            }

        awaitClose { listener.remove() }
    }
}
