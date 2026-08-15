package com.roboticswala.hub.data.models

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class AdminHubAnalytics(
    // Student Metrics
    val totalStudents: Int = 0,
    val approvedStudents: Int = 0,
    val pendingStudents: Int = 0,
    val suspendedStudents: Int = 0,
    val activeStudents: Int = 0,

    // Project Metrics
    val totalProjects: Int = 0,
    val activeProjects: Int = 0,
    val completedProjects: Int = 0,
    val planningProjects: Int = 0,
    val onHoldProjects: Int = 0,
    val averageProjectProgress: Double = 0.0,

    // Attendance Metrics
    val todayCheckIns: Int = 0,
    val todayPresentStudents: Int = 0,
    val averageAttendancePercentage: Double = 0.0,
    val totalLabHours: Double = 0.0,
    val averageWorkingHoursPerStudent: Double = 0.0,

    // Task Metrics
    val totalTasks: Int = 0,
    val pendingTasks: Int = 0,
    val inProgressTasks: Int = 0,
    val completedTasks: Int = 0,
    val overdueTasks: Int = 0,
    val taskCompletionRate: Double = 0.0,

    // Achievement Metrics
    val totalAchievements: Int = 0,
    val approvedAchievements: Int = 0,
    val pendingAchievements: Int = 0,
    val rejectedAchievements: Int = 0,

    // Event Metrics
    val totalEvents: Int = 0,
    val upcomingEvents: Int = 0,
    val completedEvents: Int = 0,
    val totalEventRegistrations: Int = 0,

    // Equipment & Inventory Metrics
    val totalEquipmentItems: Int = 0,
    val totalEquipmentStock: Int = 0,
    val issuedEquipmentCount: Int = 0,
    val lowStockItemsCount: Int = 0,
    val outOfStockItemsCount: Int = 0,
    val overdueReturnsCount: Int = 0,

    // Budget & Financial Metrics
    val totalApprovedBudget: Double = 0.0,
    val totalApprovedExpenses: Double = 0.0,
    val totalPendingExpenses: Double = 0.0,
    val totalRemainingBudget: Double = 0.0,
    val budgetUtilizationPercentage: Double = 0.0,

    val lastUpdated: Long = System.currentTimeMillis()
)

@IgnoreExtraProperties
data class StudentPerformanceReport(
    val studentUid: String = "",
    val studentName: String = "",
    val studentId: String = "",
    val department: String = "",
    val year: String = "",
    val profilePhotoUrl: String? = null,

    // Attendance
    val presentDays: Int = 0,
    val totalSessions: Int = 0,
    val attendancePercentage: Double = 0.0,
    val totalWorkingHours: Double = 0.0,
    val averageHoursPerSession: Double = 0.0,

    // Projects
    val totalAssignedProjects: Int = 0,
    val activeProjectsCount: Int = 0,
    val completedProjectsCount: Int = 0,
    val averageProjectProgress: Double = 0.0,

    // Tasks & Daily Work
    val assignedTasksCount: Int = 0,
    val completedTasksCount: Int = 0,
    val pendingTasksCount: Int = 0,
    val overdueTasksCount: Int = 0,
    val dailyWorkUpdatesCount: Int = 0,

    // Achievements & Events
    val approvedAchievementsCount: Int = 0,
    val pendingAchievementsCount: Int = 0,
    val eventParticipationCount: Int = 0,
    val equipmentRequestsCount: Int = 0,

    // Scoring & Rank
    val performanceScore: Double = 0.0,
    val leaderboardRank: Int = 0,
    val majorAchievementsSummary: String = "",
    val calculatedAt: Long = System.currentTimeMillis()
)

@IgnoreExtraProperties
data class LeaderboardEntry(
    val rank: Int = 1,
    val studentUid: String = "",
    val studentName: String = "",
    val studentId: String = "",
    val department: String = "Robotics & AI",
    val photoUrl: String? = null,
    val totalScore: Double = 0.0,
    val attendanceScore: Double = 0.0,    // max 25
    val projectsScore: Double = 0.0,      // max 20
    val progressScore: Double = 0.0,      // max 15
    val tasksScore: Double = 0.0,         // max 15
    val achievementsScore: Double = 0.0,  // max 15
    val eventsScore: Double = 0.0,        // max 5
    val consistencyScore: Double = 0.0,   // max 5
    val badge: String = ""                // 🥇 Gold, 🥈 Silver, 🥉 Bronze, 🎖️ Top Contributor
) {
    companion object {
        const val MAX_ATTENDANCE_PTS = 25.0
        const val MAX_COMPLETED_PROJECTS_PTS = 20.0
        const val MAX_PROJECT_PROGRESS_PTS = 15.0
        const val MAX_TASKS_PTS = 15.0
        const val MAX_ACHIEVEMENTS_PTS = 15.0
        const val MAX_EVENTS_PTS = 5.0
        const val MAX_CONSISTENCY_PTS = 5.0
        const val TOTAL_MAX_PTS = 100.0

        /**
         * Transparent, deterministic performance scoring formula out of 100 points.
         */
        fun calculateScore(
            attendanceRate: Double,        // 0.0 to 100.0
            completedProjects: Int,        // count
            avgProjectProgress: Double,    // 0.0 to 100.0
            completedTasks: Int,           // count
            totalAssignedTasks: Int,       // count
            approvedAchievements: Int,     // count
            eventsParticipated: Int,       // count
            dailyWorkLogs: Int             // count
        ): ScoreBreakdown {
            val attScore = ((attendanceRate / 100.0) * MAX_ATTENDANCE_PTS).coerceIn(0.0, MAX_ATTENDANCE_PTS)
            val projScore = (completedProjects * 10.0).coerceIn(0.0, MAX_COMPLETED_PROJECTS_PTS)
            val progScore = ((avgProjectProgress / 100.0) * MAX_PROJECT_PROGRESS_PTS).coerceIn(0.0, MAX_PROJECT_PROGRESS_PTS)
            
            val taskRate = if (totalAssignedTasks > 0) (completedTasks.toDouble() / totalAssignedTasks) else if (completedTasks > 0) 1.0 else 0.0
            val taskScore = (taskRate * MAX_TASKS_PTS).coerceIn(0.0, MAX_TASKS_PTS)
            
            val achScore = (approvedAchievements * 5.0).coerceIn(0.0, MAX_ACHIEVEMENTS_PTS)
            val evtScore = (eventsParticipated * 2.5).coerceIn(0.0, MAX_EVENTS_PTS)
            val conScore = (dailyWorkLogs * 0.5).coerceIn(0.0, MAX_CONSISTENCY_PTS)

            val total = (attScore + projScore + progScore + taskScore + achScore + evtScore + conScore).coerceIn(0.0, TOTAL_MAX_PTS)

            return ScoreBreakdown(
                totalScore = (Math.round(total * 10.0) / 10.0),
                attendanceScore = (Math.round(attScore * 10.0) / 10.0),
                projectsScore = (Math.round(projScore * 10.0) / 10.0),
                progressScore = (Math.round(progScore * 10.0) / 10.0),
                tasksScore = (Math.round(taskScore * 10.0) / 10.0),
                achievementsScore = (Math.round(achScore * 10.0) / 10.0),
                eventsScore = (Math.round(evtScore * 10.0) / 10.0),
                consistencyScore = (Math.round(conScore * 10.0) / 10.0)
            )
        }
    }
}

data class ScoreBreakdown(
    val totalScore: Double,
    val attendanceScore: Double,
    val projectsScore: Double,
    val progressScore: Double,
    val tasksScore: Double,
    val achievementsScore: Double,
    val eventsScore: Double,
    val consistencyScore: Double
)
