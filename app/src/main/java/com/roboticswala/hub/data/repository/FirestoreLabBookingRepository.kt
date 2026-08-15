package com.roboticswala.hub.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.roboticswala.hub.data.models.LabBooking
import com.roboticswala.hub.utils.BookingTimeUtils
import com.roboticswala.hub.utils.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class FirestoreLabBookingRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : LabBookingRepository {

    private val bookingsCollection = firestore.collection("labBookings")

    override fun createBookingRequest(booking: LabBooking): Flow<Resource<LabBooking>> = flow {
        emit(Resource.Loading())
        try {
            if (booking.startMinutes >= booking.endMinutes) {
                emit(Resource.Error("Invalid Time Range: Start time must be before end time."))
                return@flow
            }

            if (BookingTimeUtils.isDateInPast(booking.bookingDate)) {
                emit(Resource.Error("Invalid Date: Cannot book a lab slot in the past."))
                return@flow
            }

            val docRef = if (booking.bookingId.isNotBlank()) {
                bookingsCollection.document(booking.bookingId)
            } else {
                bookingsCollection.document()
            }

            val finalBooking = booking.copy(
                bookingId = docRef.id,
                status = LabBooking.STATUS_PENDING,
                createdAt = System.currentTimeMillis()
            )

            docRef.set(finalBooking).await()
            emit(Resource.Success(finalBooking))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to create lab booking request."))
        }
    }

    override fun cancelBooking(bookingId: String, studentUid: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val doc = bookingsCollection.document(bookingId).get().await()
            if (!doc.exists()) {
                emit(Resource.Error("Booking not found."))
                return@flow
            }

            val booking = doc.toObject(LabBooking::class.java)
            if (booking == null || booking.studentUid != studentUid) {
                emit(Resource.Error("Unauthorized: You can only cancel your own bookings."))
                return@flow
            }

            if (booking.status == LabBooking.STATUS_REJECTED || booking.status == LabBooking.STATUS_CANCELLED) {
                emit(Resource.Error("Booking is already ${booking.status}."))
                return@flow
            }

            bookingsCollection.document(bookingId).update(
                mapOf(
                    "status" to LabBooking.STATUS_CANCELLED,
                    "cancelledAt" to System.currentTimeMillis()
                )
            ).await()

            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to cancel booking."))
        }
    }

    override fun observeStudentBookings(studentUid: String): Flow<List<LabBooking>> = callbackFlow {
        val listener = bookingsCollection
            .whereEqualTo("studentUid", studentUid)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { it.toObject(LabBooking::class.java) } ?: emptyList()
                trySend(list)
            }

        awaitClose { listener.remove() }
    }

    override fun observeTodayStudentBooking(studentUid: String): Flow<LabBooking?> = callbackFlow {
        val todayStr = BookingTimeUtils.getTodayDateString()
        val listener = bookingsCollection
            .whereEqualTo("studentUid", studentUid)
            .whereEqualTo("bookingDate", todayStr)
            .whereEqualTo("status", LabBooking.STATUS_APPROVED)
            .limit(1)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(null)
                    return@addSnapshotListener
                }
                val booking = snapshot?.documents?.firstOrNull()?.toObject(LabBooking::class.java)
                trySend(booking)
            }

        awaitClose { listener.remove() }
    }

    override fun observeAllAdminBookings(
        dateFilter: String?,
        statusFilter: String?
    ): Flow<List<LabBooking>> = callbackFlow {
        var query: Query = bookingsCollection.orderBy("createdAt", Query.Direction.DESCENDING)

        if (!dateFilter.isNullOrBlank() && dateFilter != "All") {
            query = query.whereEqualTo("bookingDate", dateFilter)
        }

        if (!statusFilter.isNullOrBlank() && statusFilter != "All") {
            query = query.whereEqualTo("status", statusFilter)
        }

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(emptyList())
                return@addSnapshotListener
            }
            val list = snapshot?.documents?.mapNotNull { it.toObject(LabBooking::class.java) } ?: emptyList()
            trySend(list)
        }

        awaitClose { listener.remove() }
    }

    override suspend fun checkSlotConflict(
        bookingDate: String,
        startMinutes: Int,
        endMinutes: Int,
        excludeBookingId: String?
    ): LabBooking? {
        return try {
            val approvedSnap = bookingsCollection
                .whereEqualTo("bookingDate", bookingDate)
                .whereEqualTo("status", LabBooking.STATUS_APPROVED)
                .get()
                .await()

            val approvedList = approvedSnap.documents.mapNotNull { it.toObject(LabBooking::class.java) }

            approvedList.firstOrNull { existing ->
                existing.bookingId != excludeBookingId &&
                        BookingTimeUtils.isOverlapping(startMinutes, endMinutes, existing.startMinutes, existing.endMinutes)
            }
        } catch (e: Exception) {
            null
        }
    }

    override fun approveBooking(booking: LabBooking, adminUid: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            // Check for overlapping approved bookings
            val conflict = checkSlotConflict(
                bookingDate = booking.bookingDate,
                startMinutes = booking.startMinutes,
                endMinutes = booking.endMinutes,
                excludeBookingId = booking.bookingId
            )

            if (conflict != null) {
                emit(
                    Resource.Error(
                        "Booking Conflict: Slot overlaps with existing Approved booking '${conflict.projectName}' (${conflict.timeRangeDisplay}) by ${conflict.studentName}."
                    )
                )
                return@flow
            }

            bookingsCollection.document(booking.bookingId).update(
                mapOf(
                    "status" to LabBooking.STATUS_APPROVED,
                    "approvedAt" to System.currentTimeMillis(),
                    "approvedBy" to adminUid
                )
            ).await()

            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to approve booking."))
        }
    }

    override fun rejectBooking(
        bookingId: String,
        reason: String,
        adminUid: String
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            bookingsCollection.document(bookingId).update(
                mapOf(
                    "status" to LabBooking.STATUS_REJECTED,
                    "rejectionReason" to reason.ifBlank { "Slot unavailable or maintenance scheduled." },
                    "rejectedAt" to System.currentTimeMillis(),
                    "approvedBy" to adminUid
                )
            ).await()

            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to reject booking."))
        }
    }
}
