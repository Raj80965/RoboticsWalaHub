package com.roboticswala.hub.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.roboticswala.hub.data.models.Achievement
import com.roboticswala.hub.data.models.AdminHubAnalytics
import com.roboticswala.hub.data.models.AttendanceRecord
import com.roboticswala.hub.data.models.Equipment
import com.roboticswala.hub.data.models.EventRegistration
import com.roboticswala.hub.data.models.LabEvent
import com.roboticswala.hub.data.models.LabTask
import com.roboticswala.hub.data.models.LeaderboardEntry
import com.roboticswala.hub.data.models.Project
import com.roboticswala.hub.data.models.ProjectExpense
import com.roboticswala.hub.data.models.StudentPerformanceReport
import com.roboticswala.hub.data.models.UserProfile
import com.roboticswala.hub.utils.BookingTimeUtils
import com.roboticswala.hub.utils.Resource
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class FirestoreAnalyticsRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : AnalyticsRepository {

    override fun getAdminHubAnalytics(): Flow<Resource<AdminHubAnalytics>> = flow {
        emit(Resource.Loading())
        try {
            coroutineScope {
                val usersDeferred = async { firestore.collection("users").get().await() }
                val projectsDeferred = async { firestore.collection("projects").get().await() }
                val attendanceDeferred = async { firestore.collection("attendanceRecords").get().await() }
                val tasksDeferred = async { firestore.collection("tasks").get().await() }
                val achievementsDeferred = async { firestore.collection("achievements").get().await() }
                val eventsDeferred = async { firestore.collection("events").get().await() }
                val registrationsDeferred = async { firestore.collection("eventRegistrations").get().await() }
                val equipmentDeferred = async { firestore.collection("equipment").get().await() }
                val expensesDeferred = async { firestore.collection("expenses").get().await() }

                val usersSnap = usersDeferred.await()
                val projectsSnap = projectsDeferred.await()
                val attSnap = attendanceDeferred.await()
                val tasksSnap = tasksDeferred.await()
                val achSnap = achievementsDeferred.await()
                val eventsSnap = eventsDeferred.await()
                val regSnap = registrationsDeferred.await()
                val eqSnap = equipmentDeferred.await()
                val expSnap = expensesDeferred.await()

                val users = usersSnap.toObjects(UserProfile::class.java).filter { it.isStudent }
                val totalStudents = users.size
                val approvedStudents = users.count { it.isApproved }
                val pendingStudents = users.count { it.isPending }
                val suspendedStudents = users.count { it.isSuspended }

                val projects = projectsSnap.toObjects(Project::class.java)
                val totalProjects = projects.size
                val activeProjects = projects.count { it.status.equals(Project.STATUS_IN_PROGRESS, ignoreCase = true) }
                val completedProjects = projects.count { it.isCompleted }
                val planningProjects = projects.count { it.status.equals(Project.STATUS_PLANNING, ignoreCase = true) }
                val onHoldProjects = projects.count { it.status.equals(Project.STATUS_ON_HOLD, ignoreCase = true) }
                val avgProg = if (totalProjects > 0) projects.map { it.progressPercentage }.average() else 0.0

                val todayDate = BookingTimeUtils.getTodayDateString()
                val attList = attSnap.toObjects(AttendanceRecord::class.java)
                val todayCheckIns = attList.count { it.date == todayDate }
                val totalHours = attList.sumOf { it.totalWorkingMinutes / 60.0 }
                val avgHoursPerStudent = if (approvedStudents > 0) totalHours / approvedStudents else 0.0

                val tasks = tasksSnap.toObjects(LabTask::class.java)
                val totalTasks = tasks.size
                val pendingTasks = tasks.count { it.status.equals(LabTask.STATUS_PENDING, ignoreCase = true) }
                val inProgressTasks = tasks.count { it.status.equals(LabTask.STATUS_IN_PROGRESS, ignoreCase = true) }
                val completedTasks = tasks.count { it.status.equals(LabTask.STATUS_COMPLETED, ignoreCase = true) }
                val overdueTasks = tasks.count { it.isOverdue }
                val taskRate = if (totalTasks > 0) (completedTasks.toDouble() / totalTasks) * 100.0 else 0.0

                val achievements = achSnap.toObjects(Achievement::class.java)
                val totalAch = achievements.size
                val approvedAch = achievements.count { it.status.equals("Approved", ignoreCase = true) }
                val pendingAch = achievements.count { it.status.equals("Pending", ignoreCase = true) }
                val rejectedAch = achievements.count { it.status.equals("Rejected", ignoreCase = true) }

                val events = eventsSnap.toObjects(LabEvent::class.java)
                val totalEvents = events.size
                val upcomingEvents = events.count { it.isUpcoming }
                val completedEvents = events.count { it.isCompleted }
                val totalEventRegs = regSnap.size()

                val equipment = eqSnap.toObjects(Equipment::class.java)
                val totalEqItems = equipment.size
                val totalStock = equipment.sumOf { it.totalQuantity }
                val issuedCount = equipment.sumOf { it.issuedQuantity }
                val lowStockCount = equipment.count { it.isLowStock }
                val outOfStockCount = equipment.count { it.isOutOfStock }

                val expenses = expSnap.toObjects(ProjectExpense::class.java)
                val totalApprovedBudget = projects.sumOf { if (it.approvedBudget > 0.0) it.approvedBudget else it.estimatedBudget }
                val totalApprovedExp = expenses.filter { it.isApproved }.sumOf { it.amount }
                val totalPendingExp = expenses.filter { it.isPending }.sumOf { it.amount }
                val totalRemBudget = (totalApprovedBudget - totalApprovedExp).coerceAtLeast(0.0)
                val budgetUtilPct = if (totalApprovedBudget > 0.0) (totalApprovedExp / totalApprovedBudget) * 100.0 else 0.0

                val analytics = AdminHubAnalytics(
                    totalStudents = totalStudents,
                    approvedStudents = approvedStudents,
                    pendingStudents = pendingStudents,
                    suspendedStudents = suspendedStudents,
                    activeStudents = approvedStudents,
                    totalProjects = totalProjects,
                    activeProjects = activeProjects,
                    completedProjects = completedProjects,
                    planningProjects = planningProjects,
                    onHoldProjects = onHoldProjects,
                    averageProjectProgress = avgProg,
                    todayCheckIns = todayCheckIns,
                    todayPresentStudents = todayCheckIns,
                    averageAttendancePercentage = if (approvedStudents > 0) (todayCheckIns.toDouble() / approvedStudents) * 100.0 else 0.0,
                    totalLabHours = totalHours,
                    averageWorkingHoursPerStudent = avgHoursPerStudent,
                    totalTasks = totalTasks,
                    pendingTasks = pendingTasks,
                    inProgressTasks = inProgressTasks,
                    completedTasks = completedTasks,
                    overdueTasks = overdueTasks,
                    taskCompletionRate = taskRate,
                    totalAchievements = totalAch,
                    approvedAchievements = approvedAch,
                    pendingAchievements = pendingAch,
                    rejectedAchievements = rejectedAch,
                    totalEvents = totalEvents,
                    upcomingEvents = upcomingEvents,
                    completedEvents = completedEvents,
                    totalEventRegistrations = totalEventRegs,
                    totalEquipmentItems = totalEqItems,
                    totalEquipmentStock = totalStock,
                    issuedEquipmentCount = issuedCount,
                    lowStockItemsCount = lowStockCount,
                    outOfStockItemsCount = outOfStockCount,
                    totalApprovedBudget = totalApprovedBudget,
                    totalApprovedExpenses = totalApprovedExp,
                    totalPendingExpenses = totalPendingExp,
                    totalRemainingBudget = totalRemBudget,
                    budgetUtilizationPercentage = budgetUtilPct,
                    lastUpdated = System.currentTimeMillis()
                )

                emit(Resource.Success(analytics))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to calculate hub analytics."))
        }
    }

    override fun getStudentPerformanceReport(studentUid: String): Flow<Resource<StudentPerformanceReport>> = flow {
        emit(Resource.Loading())
        try {
            coroutineScope {
                val userDoc = firestore.collection("users").document(studentUid).get().await()
                val user = userDoc.toObject(UserProfile::class.java) ?: UserProfile(uid = studentUid)

                val attSnap = firestore.collection("attendanceRecords").whereEqualTo("studentUid", studentUid).get().await()
                val projSnap = firestore.collection("projects").get().await()
                val taskSnap = firestore.collection("tasks").whereEqualTo("assignedStudentUid", studentUid).get().await()
                val achSnap = firestore.collection("achievements").whereEqualTo("studentUid", studentUid).get().await()
                val regSnap = firestore.collection("eventRegistrations").whereEqualTo("studentUid", studentUid).get().await()

                val attList = attSnap.toObjects(AttendanceRecord::class.java)
                val presentDays = attList.size
                val totalSessions = attList.size
                val totalHours = attList.sumOf { it.totalWorkingMinutes / 60.0 }
                val attRate = if (totalSessions > 0) 100.0 else (presentDays * 20.0).coerceAtMost(100.0)

                val allProjects = projSnap.toObjects(Project::class.java)
                val myProjects = allProjects.filter { it.isUserAuthorized(studentUid) }
                val activeProjects = myProjects.count { it.status == Project.STATUS_IN_PROGRESS }
                val completedProjects = myProjects.count { it.isCompleted }
                val avgProg = if (myProjects.isNotEmpty()) myProjects.map { it.progressPercentage }.average() else 0.0

                val tasks = taskSnap.toObjects(LabTask::class.java)
                val assignedTasks = tasks.size
                val completedTasks = tasks.count { it.status.equals(LabTask.STATUS_COMPLETED, ignoreCase = true) }
                val pendingTasks = tasks.count { it.status.equals(LabTask.STATUS_PENDING, ignoreCase = true) || it.status.equals(LabTask.STATUS_IN_PROGRESS, ignoreCase = true) }
                val overdueTasks = tasks.count { it.isOverdue }

                val achievements = achSnap.toObjects(Achievement::class.java)
                val approvedAch = achievements.count { it.status.equals("Approved", ignoreCase = true) }
                val pendingAch = achievements.count { it.status.equals("Pending", ignoreCase = true) }

                val eventRegs = regSnap.size()

                val scoreBreakdown = LeaderboardEntry.calculateScore(
                    attendanceRate = attRate,
                    completedProjects = completedProjects,
                    avgProjectProgress = avgProg,
                    completedTasks = completedTasks,
                    totalAssignedTasks = assignedTasks,
                    approvedAchievements = approvedAch,
                    eventsParticipated = eventRegs,
                    dailyWorkLogs = presentDays
                )

                val report = StudentPerformanceReport(
                    studentUid = studentUid,
                    studentName = user.fullName.ifBlank { "Robotics Student" },
                    studentId = user.studentId.ifBlank { "STU-${studentUid.take(4)}" },
                    department = user.branch.ifBlank { "Robotics & Automation" },
                    year = user.year.ifBlank { "3rd Year" },
                    profilePhotoUrl = user.photoUrl,
                    presentDays = presentDays,
                    totalSessions = totalSessions,
                    attendancePercentage = attRate,
                    totalWorkingHours = totalHours,
                    averageHoursPerSession = if (presentDays > 0) totalHours / presentDays else 0.0,
                    totalAssignedProjects = myProjects.size,
                    activeProjectsCount = activeProjects,
                    completedProjectsCount = completedProjects,
                    averageProjectProgress = avgProg,
                    assignedTasksCount = assignedTasks,
                    completedTasksCount = completedTasks,
                    pendingTasksCount = pendingTasks,
                    overdueTasksCount = overdueTasks,
                    dailyWorkUpdatesCount = presentDays,
                    approvedAchievementsCount = approvedAch,
                    pendingAchievementsCount = pendingAch,
                    eventParticipationCount = eventRegs,
                    performanceScore = scoreBreakdown.totalScore,
                    leaderboardRank = 1,
                    majorAchievementsSummary = if (approvedAch > 0) "$approvedAch Verified Achievements" else "Active Lab Contributor",
                    calculatedAt = System.currentTimeMillis()
                )

                emit(Resource.Success(report))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to generate student performance report."))
        }
    }

    override fun getLeaderboard(period: String): Flow<Resource<List<LeaderboardEntry>>> = flow {
        emit(Resource.Loading())
        try {
            coroutineScope {
                val usersSnap = firestore.collection("users").get().await()
                val students = usersSnap.toObjects(UserProfile::class.java).filter { it.isStudent }

                val attSnap = firestore.collection("attendanceRecords").get().await()
                val projSnap = firestore.collection("projects").get().await()
                val taskSnap = firestore.collection("tasks").get().await()
                val achSnap = firestore.collection("achievements").get().await()
                val regSnap = firestore.collection("eventRegistrations").get().await()

                val allAtt = attSnap.toObjects(AttendanceRecord::class.java)
                val allProj = projSnap.toObjects(Project::class.java)
                val allTasks = taskSnap.toObjects(LabTask::class.java)
                val allAch = achSnap.toObjects(Achievement::class.java)
                val allRegs = regSnap.toObjects(EventRegistration::class.java)

                val entries = students.map { student ->
                    val sUid = student.uid
                    val myAtt = allAtt.filter { it.studentUid == sUid }
                    val presentDays = myAtt.size
                    val totalAttSessions = myAtt.size
                    val attRate = if (totalAttSessions > 0) 100.0 else (presentDays * 20.0).coerceAtMost(100.0)

                    val myProj = allProj.filter { it.isUserAuthorized(sUid) }
                    val completedProj = myProj.count { it.isCompleted }
                    val avgProg = if (myProj.isNotEmpty()) myProj.map { it.progressPercentage }.average() else 0.0

                    val myTasks = allTasks.filter { it.assignedStudentUid == sUid }
                    val completedTasks = myTasks.count { it.status.equals(LabTask.STATUS_COMPLETED, ignoreCase = true) }

                    val myAch = allAch.filter { it.studentUid == sUid && it.status.equals("Approved", ignoreCase = true) }
                    val myRegs = allRegs.filter { it.studentUid == sUid }

                    val score = LeaderboardEntry.calculateScore(
                        attendanceRate = attRate,
                        completedProjects = completedProj,
                        avgProjectProgress = avgProg,
                        completedTasks = completedTasks,
                        totalAssignedTasks = myTasks.size,
                        approvedAchievements = myAch.size,
                        eventsParticipated = myRegs.size,
                        dailyWorkLogs = presentDays
                    )

                    LeaderboardEntry(
                        studentUid = sUid,
                        studentName = student.fullName.ifBlank { "Student ${sUid.take(4)}" },
                        studentId = student.studentId.ifBlank { "STU-${sUid.take(4)}" },
                        department = student.branch.ifBlank { "Robotics" },
                        photoUrl = student.photoUrl,
                        totalScore = score.totalScore,
                        attendanceScore = score.attendanceScore,
                        projectsScore = score.projectsScore,
                        progressScore = score.progressScore,
                        tasksScore = score.tasksScore,
                        achievementsScore = score.achievementsScore,
                        eventsScore = score.eventsScore,
                        consistencyScore = score.consistencyScore
                    )
                }

                // Sort descending by total score
                val ranked = entries.sortedByDescending { it.totalScore }.mapIndexed { index, entry ->
                    val rank = index + 1
                    val badge = when (rank) {
                        1 -> "🥇 1st Place (Gold)"
                        2 -> "🥈 2nd Place (Silver)"
                        3 -> "🥉 3rd Place (Bronze)"
                        else -> if (entry.totalScore >= 70.0) "🎖️ High Achiever" else "🚀 Lab Member"
                    }
                    entry.copy(rank = rank, badge = badge)
                }

                emit(Resource.Success(ranked))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to generate student leaderboard."))
        }
    }
}
