package com.roboticswala.hub.data.repository

import com.roboticswala.hub.data.models.AttendanceRecord
import com.roboticswala.hub.data.models.AttendanceScanResult
import com.roboticswala.hub.data.models.AttendanceSession
import com.roboticswala.hub.data.models.AttendanceStats
import com.roboticswala.hub.data.models.UserProfile
import com.roboticswala.hub.utils.Resource
import kotlinx.coroutines.flow.Flow

interface AttendanceRepository {

    /**
     * Creates a new QR attendance session in Firestore collection "attendanceSessions".
     */
    fun createAttendanceSession(
        labName: String,
        durationMinutes: Int,
        adminUid: String
    ): Flow<Resource<AttendanceSession>>

    /**
     * Deactivates / stops an active attendance session in Firestore.
     */
    fun stopAttendanceSession(sessionId: String): Flow<Resource<Unit>>

    /**
     * Real-time listener for the currently active attendance session.
     */
    fun observeActiveAttendanceSession(): Flow<AttendanceSession?>

    /**
     * Real-time listener for all attendance records created today or for a specific session.
     */
    fun observeAdminAttendanceRecords(dateFilter: String? = null): Flow<List<AttendanceRecord>>

    /**
     * Real-time listener for a student's full attendance history.
     */
    fun observeStudentAttendanceHistory(studentUid: String): Flow<List<AttendanceRecord>>

    /**
     * Real-time stream of calculated stats (present days, total days, hours, percentage).
     */
    fun observeStudentAttendanceStats(studentUid: String): Flow<AttendanceStats>

    /**
     * Validates scanned QR payload and atomically marks Check-In or Check-Out for the student.
     */
    fun processAttendanceQRScan(
        qrPayload: String,
        student: UserProfile
    ): Flow<AttendanceScanResult>
}
