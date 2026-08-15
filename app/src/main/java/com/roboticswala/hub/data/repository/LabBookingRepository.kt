package com.roboticswala.hub.data.repository

import com.roboticswala.hub.data.models.LabBooking
import com.roboticswala.hub.utils.Resource
import kotlinx.coroutines.flow.Flow

interface LabBookingRepository {

    /**
     * Creates a new booking request in Firestore collection "labBookings" with status "Pending".
     */
    fun createBookingRequest(booking: LabBooking): Flow<Resource<LabBooking>>

    /**
     * Cancels a pending or approved booking (if cancelled before start time).
     */
    fun cancelBooking(bookingId: String, studentUid: String): Flow<Resource<Unit>>

    /**
     * Real-time listener for a student's own bookings list.
     */
    fun observeStudentBookings(studentUid: String): Flow<List<LabBooking>>

    /**
     * Real-time listener for the student's active Approved booking for today.
     */
    fun observeTodayStudentBooking(studentUid: String): Flow<LabBooking?>

    /**
     * Real-time listener for all bookings for Admin management with optional date & status filters.
     */
    fun observeAllAdminBookings(
        dateFilter: String? = null,
        statusFilter: String? = null
    ): Flow<List<LabBooking>>

    /**
     * Approves a booking request after validating there are no overlapping Approved bookings.
     */
    fun approveBooking(booking: LabBooking, adminUid: String): Flow<Resource<Unit>>

    /**
     * Rejects a booking request and stores the admin rejection reason.
     */
    fun rejectBooking(bookingId: String, reason: String, adminUid: String): Flow<Resource<Unit>>

    /**
     * Checks if a requested slot [startMinutes, endMinutes] on bookingDate overlaps with any Approved booking.
     * Returns the conflicting booking if found, or null if slot is available.
     */
    suspend fun checkSlotConflict(
        bookingDate: String,
        startMinutes: Int,
        endMinutes: Int,
        excludeBookingId: String? = null
    ): LabBooking?
}
