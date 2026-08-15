package com.roboticswala.hub.ui.screens.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.roboticswala.hub.data.models.ActivityLogItem
import com.roboticswala.hub.data.models.AttendanceData
import com.roboticswala.hub.data.models.EventItem
import com.roboticswala.hub.data.models.LabBooking
import com.roboticswala.hub.data.models.NoticeItem
import com.roboticswala.hub.data.models.ProjectItem
import com.roboticswala.hub.data.models.StudentProject
import com.roboticswala.hub.data.models.StudentTask
import com.roboticswala.hub.data.models.UserProfile
import com.roboticswala.hub.data.repository.FirestoreStudentDashboardRepository
import com.roboticswala.hub.data.repository.StudentDashboardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ─────────────────────────────────────────────
// UI State — Day 5 (Real Firestore Data)
// ─────────────────────────────────────────────

data class StudentUiState(
    // ── Loading / Error ───────────────────────
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: String? = null,

    // ── Real Profile Data ─────────────────────
    val userProfile: UserProfile? = null,

    // ── Real Dashboard Data ───────────────────
    val attendanceData: AttendanceData? = null,
    val todayLabBooking: LabBooking? = null,
    val projects: List<StudentProject> = emptyList(),
    val tasks: List<StudentTask> = emptyList(),
    val latestNotice: NoticeItem? = null,
    val upcomingEvent: EventItem? = null,

    // ── Projects Tab (preserved static until Day 6+) ──
    val projectsList: List<ProjectItem> = listOf(
        ProjectItem(
            id = "PRJ-01",
            title = "Autonomous Obstacle Avoidance Rover",
            category = "Mobile Robotics / SLAM",
            description = "4WD chassis with LiDAR mapping, Jetson Nano controller, and ROS2 obstacle navigation.",
            progress = 78,
            status = "Active Testing",
            techStack = listOf("ROS2", "LiDAR", "Python", "Jetson"),
            teamMembers = 3
        ),
        ProjectItem(
            id = "PRJ-02",
            title = "6-DOF Robotic Arm with Computer Vision",
            category = "Industrial Automation",
            description = "Precision inverse kinematics robotic manipulator with OpenCV color and shape sorting.",
            progress = 62,
            status = "In Progress",
            techStack = listOf("OpenCV", "Inverse Kinematics", "C++", "Servos"),
            teamMembers = 2
        ),
        ProjectItem(
            id = "PRJ-03",
            title = "Autonomous Drone Surveillance Quadcopter",
            category = "Aerial Robotics",
            description = "ArduPilot telemetry integration, waypoint mission tracking, and optical flow sensor.",
            progress = 45,
            status = "In Progress",
            techStack = listOf("Pixhawk", "ArduPilot", "MAVLink", "GPS"),
            teamMembers = 4
        ),
        ProjectItem(
            id = "PRJ-04",
            title = "IoT Telepresence Lab Bot",
            category = "Teleoperation",
            description = "Low-latency WebRTC video stream robot with omnidirectional mecanum wheels.",
            progress = 100,
            status = "Completed",
            techStack = listOf("WebRTC", "ESP32", "Mecanum", "MQTT"),
            teamMembers = 2
        )
    ),

    // ── Activity Tab (preserved static until Day 6+) ──
    val activityLogs: List<ActivityLogItem> = listOf(
        ActivityLogItem(
            id = "ACT-01",
            title = "LiDAR Sensor Calibration",
            location = "Robotics Bay 3",
            timestamp = "Today • 02:15 PM",
            duration = "1 hr 45 min",
            type = "Testing"
        ),
        ActivityLogItem(
            id = "ACT-02",
            title = "RFID Lab Entry Clock-In",
            location = "Main Automation Gate",
            timestamp = "Today • 01:58 PM",
            duration = "Pass verified",
            type = "Access"
        ),
        ActivityLogItem(
            id = "ACT-03",
            title = "3D Print Chassis Bracket (PETG)",
            location = "Additive Fab Lab Station 2",
            timestamp = "Yesterday • 04:30 PM",
            duration = "2 hrs 10 min",
            type = "Fabrication"
        ),
        ActivityLogItem(
            id = "ACT-04",
            title = "Motor Driver H-Bridge Circuit Soldering",
            location = "Electronics Workbench 4",
            timestamp = "Aug 12 • 11:00 AM",
            duration = "3 hrs",
            type = "Assembly"
        )
    ),

    // ── Navigation ────────────────────────────
    val selectedTab: String = "student_home",
    val snackbarMessage: String? = null
)

// ─────────────────────────────────────────────
// ViewModel — Day 5
// ─────────────────────────────────────────────

class StudentViewModel(
    private val currentUid: String,
    private val dashboardRepo: StudentDashboardRepository = FirestoreStudentDashboardRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudentUiState())
    val uiState: StateFlow<StudentUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    // ── Data Loading ──────────────────────────

    private fun loadDashboardData() {
        _uiState.update { it.copy(isLoading = true, error = null) }

        // 1. Profile — real-time
        viewModelScope.launch {
            dashboardRepo.observeStudentProfile(currentUid)
                .catch { e ->
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
                .collect { result ->
                    result.onSuccess { profile ->
                        _uiState.update { it.copy(userProfile = profile, isLoading = false) }
                    }.onFailure { e ->
                        _uiState.update { it.copy(error = e.message, isLoading = false) }
                    }
                }
        }

        // 2. Attendance — real-time
        viewModelScope.launch {
            dashboardRepo.observeAttendance(currentUid)
                .catch { /* silent — show empty state */ }
                .collect { result ->
                    result.onSuccess { data ->
                        _uiState.update { it.copy(attendanceData = data) }
                    }
                }
        }

        // 3. Today's Lab Booking — real-time
        viewModelScope.launch {
            dashboardRepo.observeTodayLabBooking(currentUid)
                .catch { /* silent — show empty state */ }
                .collect { result ->
                    result.onSuccess { booking ->
                        _uiState.update { it.copy(todayLabBooking = booking) }
                    }
                }
        }

        // 4. Projects — real-time
        viewModelScope.launch {
            dashboardRepo.observeProjects(currentUid)
                .catch { /* silent — show empty state */ }
                .collect { result ->
                    result.onSuccess { projects ->
                        _uiState.update { it.copy(projects = projects) }
                    }
                }
        }

        // 5. Tasks — real-time
        viewModelScope.launch {
            dashboardRepo.observeTasks(currentUid)
                .catch { /* silent — show empty state */ }
                .collect { result ->
                    result.onSuccess { tasks ->
                        _uiState.update { it.copy(tasks = tasks) }
                    }
                }
        }

        // 6. Latest Notice — real-time
        viewModelScope.launch {
            dashboardRepo.observeLatestNotice()
                .catch { /* silent — show empty state */ }
                .collect { result ->
                    result.onSuccess { notice ->
                        _uiState.update { it.copy(latestNotice = notice) }
                    }
                }
        }

        // 7. Upcoming Event — real-time
        viewModelScope.launch {
            dashboardRepo.observeUpcomingEvent()
                .catch { /* silent — show empty state */ }
                .collect { result ->
                    result.onSuccess { event ->
                        _uiState.update { it.copy(upcomingEvent = event) }
                    }
                }
        }
    }

    fun refresh() {
        _uiState.update { it.copy(isRefreshing = true, error = null) }
        loadDashboardData()
        viewModelScope.launch {
            kotlinx.coroutines.delay(800)
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }

    // ── Navigation ────────────────────────────

    fun onTabSelected(route: String) {
        _uiState.update { it.copy(selectedTab = route) }
    }

    // ── Messages ──────────────────────────────

    fun showMessage(message: String) {
        _uiState.update { it.copy(snackbarMessage = message) }
    }

    fun clearMessage() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    // ── Factory ───────────────────────────────

    companion object {
        fun factory(uid: String): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return StudentViewModel(currentUid = uid) as T
                }
            }
        }
    }
}
