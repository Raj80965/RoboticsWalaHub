package com.roboticswala.hub.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.roboticswala.hub.data.models.AttendanceRecord
import com.roboticswala.hub.data.models.AttendanceScanResult
import com.roboticswala.hub.data.models.AttendanceSession
import com.roboticswala.hub.data.models.AttendanceStats
import com.roboticswala.hub.data.models.UserProfile
import com.roboticswala.hub.utils.QRCodeGenerator
import com.roboticswala.hub.utils.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class FirestoreAttendanceRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : AttendanceRepository {

    private val sessionsCollection = firestore.collection("attendanceSessions")
    private val recordsCollection = firestore.collection("attendanceRecords")

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    override fun createAttendanceSession(
        labName: String,
        durationMinutes: Int,
        adminUid: String
    ): Flow<Resource<AttendanceSession>> = flow {
        emit(Resource.Loading())
        try {
            val now = System.currentTimeMillis()
            val expiresAt = now + (durationMinutes * 60 * 1000L)
            val currentDateStr = dateFormat.format(Date(now))
            val token = UUID.randomUUID().toString().substring(0, 8).uppercase()

            val docRef = sessionsCollection.document()
            val session = AttendanceSession(
                sessionId = docRef.id,
                labName = labName.ifBlank { "Main Robotics Lab" },
                date = currentDateStr,
                createdAt = now,
                expiresAt = expiresAt,
                isActive = true,
                createdAdminUid = adminUid,
                sessionToken = token
            )

            docRef.set(session).await()
            emit(Resource.Success(session))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to create attendance session."))
        }
    }

    override fun stopAttendanceSession(sessionId: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            sessionsCollection.document(sessionId)
                .update("isActive", false)
                .await()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to stop session."))
        }
    }

    override fun observeActiveAttendanceSession(): Flow<AttendanceSession?> = callbackFlow {
        val listener = sessionsCollection
            .whereEqualTo("isActive", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(null)
                    return@addSnapshotListener
                }
                val activeSessions = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(AttendanceSession::class.java)?.copy(sessionId = doc.id)
                } ?: emptyList()

                val latestSession = activeSessions
                    .filter { it.remainingSeconds > 0 }
                    .maxByOrNull { it.createdAt }

                trySend(latestSession)
            }

        awaitClose { listener.remove() }
    }

    override fun observeAdminAttendanceRecords(dateFilter: String?): Flow<List<AttendanceRecord>> = callbackFlow {
        val queryDate = dateFilter ?: dateFormat.format(Date())
        val listener = recordsCollection
            .whereEqualTo("date", queryDate)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val records = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(AttendanceRecord::class.java)?.copy(recordId = doc.id)
                }?.sortedByDescending { it.checkInTime } ?: emptyList()
                trySend(records)
            }

        awaitClose { listener.remove() }
    }

    override fun observeStudentAttendanceHistory(studentUid: String): Flow<List<AttendanceRecord>> = callbackFlow {
        val listener = recordsCollection
            .whereEqualTo("studentUid", studentUid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val records = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(AttendanceRecord::class.java)?.copy(recordId = doc.id)
                }?.sortedByDescending { it.checkInTime } ?: emptyList()
                trySend(records)
            }

        awaitClose { listener.remove() }
    }

    override fun observeStudentAttendanceStats(studentUid: String): Flow<AttendanceStats> = callbackFlow {
        val listener = recordsCollection
            .whereEqualTo("studentUid", studentUid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(AttendanceStats())
                    return@addSnapshotListener
                }

                val records = snapshot?.documents?.mapNotNull { it.toObject(AttendanceRecord::class.java) } ?: emptyList()
                val distinctDays = records.map { it.date }.distinct().size
                val totalMinutes = records.sumOf { it.totalWorkingMinutes }
                val totalHours = (totalMinutes / 60).toInt()

                // Baseline 25 total working days per month or at least presentDays
                val totalDays = if (distinctDays > 0) maxOf(distinctDays, 25) else 0
                val percentage = if (totalDays > 0) {
                    ((distinctDays.toDouble() / totalDays.toDouble()) * 100.0).coerceIn(0.0, 100.0)
                } else 0.0

                trySend(
                    AttendanceStats(
                        percentage = percentage,
                        presentDays = distinctDays,
                        totalDays = totalDays,
                        totalWorkingHours = totalHours,
                        totalWorkingMinutes = totalMinutes
                    )
                )
            }

        awaitClose { listener.remove() }
    }

    override fun processAttendanceQRScan(
        qrPayload: String,
        student: UserProfile
    ): Flow<AttendanceScanResult> = flow {
        // 1. Validate Student Status
        if (!student.status.equals("Approved", ignoreCase = true)) {
            emit(AttendanceScanResult.Error("Access Denied: Only Approved students can mark lab attendance."))
            return@flow
        }

        // 2. Decode QR Payload
        val qrData = QRCodeGenerator.decodeSessionPayload(qrPayload)
        if (qrData == null) {
            emit(AttendanceScanResult.Error("Invalid QR Code: Format not recognized. Please scan the official Robotics Wala Hub session QR."))
            return@flow
        }

        // 3. Fetch Session from Firestore
        val sessionDoc = try {
            sessionsCollection.document(qrData.sessionId).get().await()
        } catch (e: Exception) {
            emit(AttendanceScanResult.Error("Network Error: Could not verify session with Firebase. ${e.localizedMessage}"))
            return@flow
        }

        if (!sessionDoc.exists()) {
            emit(AttendanceScanResult.Error("Session Not Found: This attendance session does not exist in the database."))
            return@flow
        }

        val session = sessionDoc.toObject(AttendanceSession::class.java)
        if (session == null || !session.isActive) {
            emit(AttendanceScanResult.Error("Inactive Session: This attendance session has been stopped by the Administrator."))
            return@flow
        }

        val now = System.currentTimeMillis()
        if (now > session.expiresAt) {
            emit(AttendanceScanResult.Error("QR Code Expired: This session expired at ${timeFormat.format(Date(session.expiresAt))}."))
            return@flow
        }

        if (session.sessionToken != qrData.sessionToken) {
            emit(AttendanceScanResult.Error("Security Error: Invalid session token. Please re-scan the live session QR."))
            return@flow
        }

        // 4. Query Existing Attendance Record for this Student & Session
        val existingRecordSnap = try {
            recordsCollection
                .whereEqualTo("sessionId", session.sessionId)
                .whereEqualTo("studentUid", student.uid)
                .limit(1)
                .get()
                .await()
        } catch (e: Exception) {
            emit(AttendanceScanResult.Error("Database query failed: ${e.localizedMessage}"))
            return@flow
        }

        val existingDoc = existingRecordSnap.documents.firstOrNull()

        if (existingDoc == null) {
            // STEP A: CREATE CHECK-IN
            val recordDocRef = recordsCollection.document()
            val newRecord = AttendanceRecord(
                recordId = recordDocRef.id,
                sessionId = session.sessionId,
                studentUid = student.uid,
                studentId = student.studentId.ifBlank { "RW-STD-${student.uid.take(4)}" },
                fullName = student.fullName.ifBlank { "Student" },
                labName = session.labName,
                date = session.date.ifBlank { dateFormat.format(Date(now)) },
                checkInTime = now,
                checkOutTime = null,
                totalWorkingMinutes = 0L,
                status = AttendanceRecord.STATUS_CHECKED_IN
            )

            try {
                recordDocRef.set(newRecord).await()
                emit(AttendanceScanResult.CheckInSuccess(newRecord, "Check-In Successful at ${timeFormat.format(Date(now))}"))
            } catch (e: Exception) {
                emit(AttendanceScanResult.Error("Failed to record check-in: ${e.localizedMessage}"))
            }
        } else {
            val record = existingDoc.toObject(AttendanceRecord::class.java)
            if (record == null) {
                emit(AttendanceScanResult.Error("Record parsing error."))
                return@flow
            }

            if (record.status == AttendanceRecord.STATUS_COMPLETED) {
                // STEP B: ALREADY COMPLETED
                emit(AttendanceScanResult.AlreadyCompleted(record, "Attendance already completed for this session."))
            } else {
                // STEP C: PERFORM CHECK-OUT
                val checkOutTime = now
                val durationMillis = checkOutTime - record.checkInTime
                val durationMinutes = maxOf(1L, durationMillis / (60 * 1000L))

                val updatedRecord = record.copy(
                    checkOutTime = checkOutTime,
                    totalWorkingMinutes = durationMinutes,
                    status = AttendanceRecord.STATUS_COMPLETED
                )

                try {
                    recordsCollection.document(record.recordId)
                        .update(
                            mapOf(
                                "checkOutTime" to checkOutTime,
                                "totalWorkingMinutes" to durationMinutes,
                                "status" to AttendanceRecord.STATUS_COMPLETED
                            )
                        ).await()
                    emit(AttendanceScanResult.CheckOutSuccess(updatedRecord, durationMinutes, "Check-Out Successful! Total: ${updatedRecord.formattedDuration}"))
                } catch (e: Exception) {
                    emit(AttendanceScanResult.Error("Failed to record check-out: ${e.localizedMessage}"))
                }
            }
        }
    }
}
