package com.roboticswala.hub.ui.screens.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roboticswala.hub.data.models.ActivityLogItem
import com.roboticswala.hub.data.models.ProjectItem
import com.roboticswala.hub.data.models.StudentDashboardData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StudentUiState(
    val dashboardData: StudentDashboardData = StudentDashboardData(),
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
    val selectedTab: String = "student_home",
    val snackbarMessage: String? = null
)

class StudentViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(StudentUiState())
    val uiState: StateFlow<StudentUiState> = _uiState.asStateFlow()

    fun onTabSelected(route: String) {
        _uiState.update { it.copy(selectedTab = route) }
    }

    fun showMessage(message: String) {
        _uiState.update { it.copy(snackbarMessage = message) }
    }

    fun clearMessage() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}
