package com.roboticswala.hub.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.roboticswala.hub.data.models.AttendanceData
import com.roboticswala.hub.data.models.EventItem
import com.roboticswala.hub.data.models.LabBooking
import com.roboticswala.hub.data.models.NoticeItem
import com.roboticswala.hub.data.models.StudentProject
import com.roboticswala.hub.data.models.StudentTask
import com.roboticswala.hub.data.models.UserProfile
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Day 5: Real Firestore implementation of StudentDashboardRepository.
 *
 * Firestore Collection Paths:
 *   /users/{uid}             → student profile
 *   /attendance/{uid}        → attendance document
 *   /labBookings             → query by studentUid + date == today
 *   /projects                → query by studentUid
 *   /tasks                   → query by studentUid
 *   /notices                 → orderBy date desc, limit 1
 *   /events                  → orderBy dateTimestamp asc, limit 1
 */
class FirestoreStudentDashboardRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : StudentDashboardRepository {

    // ── Student Profile ──────────────────────────────────────────────────────

    override fun observeStudentProfile(uid: String): Flow<Result<UserProfile>> = callbackFlow {
        var listener: ListenerRegistration? = null
        try {
            listener = firestore.collection("users")
                .document(uid)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(Result.failure(error))
                        return@addSnapshotListener
                    }
                    val data = snapshot?.data
                    if (data != null) {
                        trySend(Result.success(UserProfile.fromMap(data)))
                    } else {
                        trySend(Result.success(UserProfile(uid = uid)))
                    }
                }
        } catch (e: Exception) {
            trySend(Result.failure(e))
        }
        awaitClose { listener?.remove() }
    }

    // ── Attendance ───────────────────────────────────────────────────────────

    override fun observeAttendance(uid: String): Flow<Result<AttendanceData?>> = callbackFlow {
        var listener: ListenerRegistration? = null
        try {
            listener = firestore.collection("attendance")
                .document(uid)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(Result.failure(error))
                        return@addSnapshotListener
                    }
                    val data = snapshot?.data
                    if (data != null) {
                        trySend(Result.success(AttendanceData.fromMap(data)))
                    } else {
                        // No attendance record yet — show empty state
                        trySend(Result.success(null))
                    }
                }
        } catch (e: Exception) {
            trySend(Result.failure(e))
        }
        awaitClose { listener?.remove() }
    }

    // ── Today's Lab Booking ──────────────────────────────────────────────────

    override fun observeTodayLabBooking(uid: String): Flow<Result<LabBooking?>> = callbackFlow {
        val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        var listener: ListenerRegistration? = null
        try {
            listener = firestore.collection("labBookings")
                .whereEqualTo("studentUid", uid)
                .whereEqualTo("date", todayDate)
                .whereEqualTo("status", "Approved")
                .limit(1)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(Result.failure(error))
                        return@addSnapshotListener
                    }
                    val doc = snapshot?.documents?.firstOrNull()
                    if (doc != null) {
                        trySend(Result.success(LabBooking.fromMap(doc.id, doc.data ?: emptyMap())))
                    } else {
                        trySend(Result.success(null))
                    }
                }
        } catch (e: Exception) {
            trySend(Result.failure(e))
        }
        awaitClose { listener?.remove() }
    }

    // ── Projects ─────────────────────────────────────────────────────────────

    override fun observeProjects(uid: String): Flow<Result<List<StudentProject>>> = callbackFlow {
        var listener: ListenerRegistration? = null
        try {
            listener = firestore.collection("projects")
                .whereEqualTo("studentUid", uid)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(Result.failure(error))
                        return@addSnapshotListener
                    }
                    val projects = snapshot?.documents?.mapNotNull { doc ->
                        doc.data?.let { StudentProject.fromMap(doc.id, it) }
                    } ?: emptyList()
                    trySend(Result.success(projects))
                }
        } catch (e: Exception) {
            trySend(Result.failure(e))
        }
        awaitClose { listener?.remove() }
    }

    // ── Tasks ────────────────────────────────────────────────────────────────

    override fun observeTasks(uid: String): Flow<Result<List<StudentTask>>> = callbackFlow {
        var listener: ListenerRegistration? = null
        try {
            listener = firestore.collection("tasks")
                .whereEqualTo("studentUid", uid)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(Result.failure(error))
                        return@addSnapshotListener
                    }
                    val tasks = snapshot?.documents?.mapNotNull { doc ->
                        doc.data?.let { StudentTask.fromMap(doc.id, it) }
                    } ?: emptyList()
                    trySend(Result.success(tasks))
                }
        } catch (e: Exception) {
            trySend(Result.failure(e))
        }
        awaitClose { listener?.remove() }
    }

    // ── Latest Notice ────────────────────────────────────────────────────────

    override fun observeLatestNotice(): Flow<Result<NoticeItem?>> = callbackFlow {
        var listener: ListenerRegistration? = null
        try {
            listener = firestore.collection("notices")
                .orderBy("dateTimestamp", Query.Direction.DESCENDING)
                .limit(1)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(Result.failure(error))
                        return@addSnapshotListener
                    }
                    val doc = snapshot?.documents?.firstOrNull()
                    if (doc != null) {
                        trySend(Result.success(NoticeItem.fromMap(doc.id, doc.data ?: emptyMap())))
                    } else {
                        trySend(Result.success(null))
                    }
                }
        } catch (e: Exception) {
            trySend(Result.failure(e))
        }
        awaitClose { listener?.remove() }
    }

    // ── Upcoming Event ───────────────────────────────────────────────────────

    override fun observeUpcomingEvent(): Flow<Result<EventItem?>> = callbackFlow {
        val nowTimestamp = System.currentTimeMillis()
        var listener: ListenerRegistration? = null
        try {
            listener = firestore.collection("events")
                .whereGreaterThanOrEqualTo("dateTimestamp", nowTimestamp)
                .orderBy("dateTimestamp", Query.Direction.ASCENDING)
                .limit(1)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(Result.failure(error))
                        return@addSnapshotListener
                    }
                    val doc = snapshot?.documents?.firstOrNull()
                    if (doc != null) {
                        trySend(Result.success(EventItem.fromMap(doc.id, doc.data ?: emptyMap())))
                    } else {
                        trySend(Result.success(null))
                    }
                }
        } catch (e: Exception) {
            trySend(Result.failure(e))
        }
        awaitClose { listener?.remove() }
    }
}
