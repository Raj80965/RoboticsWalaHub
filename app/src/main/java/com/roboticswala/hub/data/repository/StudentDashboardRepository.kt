package com.roboticswala.hub.data.repository

import com.roboticswala.hub.data.models.AttendanceData
import com.roboticswala.hub.data.models.EventItem
import com.roboticswala.hub.data.models.LabBooking
import com.roboticswala.hub.data.models.NoticeItem
import com.roboticswala.hub.data.models.StudentProject
import com.roboticswala.hub.data.models.StudentTask
import com.roboticswala.hub.data.models.UserProfile
import kotlinx.coroutines.flow.Flow

/**
 * Day 5: Repository interface for real-time Student Dashboard data from Firestore.
 * All methods return Flow for real-time updates via Firestore snapshot listeners.
 */
interface StudentDashboardRepository {

    /** Real-time stream of the student's own profile (from /users/{uid}) */
    fun observeStudentProfile(uid: String): Flow<Result<UserProfile>>

    /** Real-time stream of attendance data (from /attendance/{uid}) */
    fun observeAttendance(uid: String): Flow<Result<AttendanceData?>>

    /** Real-time stream of today's approved lab booking (from /labBookings) */
    fun observeTodayLabBooking(uid: String): Flow<Result<LabBooking?>>

    /** Real-time stream of student's projects (from /projects, query by studentUid) */
    fun observeProjects(uid: String): Flow<Result<List<StudentProject>>>

    /** Real-time stream of tasks assigned to student (from /tasks, query by studentUid) */
    fun observeTasks(uid: String): Flow<Result<List<StudentTask>>>

    /** Real-time stream of latest notice (from /notices, ordered by date desc, limit 1) */
    fun observeLatestNotice(): Flow<Result<NoticeItem?>>

    /** Real-time stream of next upcoming event (from /events, ordered by date asc, limit 1) */
    fun observeUpcomingEvent(): Flow<Result<EventItem?>>
}
