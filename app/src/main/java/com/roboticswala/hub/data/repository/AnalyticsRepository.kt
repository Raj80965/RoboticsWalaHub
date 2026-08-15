package com.roboticswala.hub.data.repository

import com.roboticswala.hub.data.models.AdminHubAnalytics
import com.roboticswala.hub.data.models.LeaderboardEntry
import com.roboticswala.hub.data.models.StudentPerformanceReport
import com.roboticswala.hub.utils.Resource
import kotlinx.coroutines.flow.Flow

interface AnalyticsRepository {

    /**
     * Aggregates real-time hub analytics for admin across all 13 modules.
     */
    fun getAdminHubAnalytics(): Flow<Resource<AdminHubAnalytics>>

    /**
     * Calculates real personal performance data and score for a specific student.
     */
    fun getStudentPerformanceReport(studentUid: String): Flow<Resource<StudentPerformanceReport>>

    /**
     * Calculates the real leaderboard ranking for all approved students.
     */
    fun getLeaderboard(period: String = "ALL_TIME"): Flow<Resource<List<LeaderboardEntry>>>
}
