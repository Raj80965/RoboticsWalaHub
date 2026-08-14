package com.roboticswala.hub.ui.screens.admin

import androidx.lifecycle.ViewModel
import com.roboticswala.hub.data.models.AdminDashboardData
import com.roboticswala.hub.data.models.BookingRequestItem
import com.roboticswala.hub.data.models.EquipmentItem
import com.roboticswala.hub.data.models.StudentDirectoryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AdminUiState(
    val dashboardData: AdminDashboardData = AdminDashboardData(),
    val studentsList: List<StudentDirectoryItem> = listOf(
        StudentDirectoryItem("STU-01", "Aarav Sharma", "aarav@roboticswala.com", "Active", "Autonomous Rover", 94.5, "Approved"),
        StudentDirectoryItem("STU-02", "Priya Nair", "priya@roboticswala.com", "Active", "6-DOF Robotic Arm", 91.0, "Approved"),
        StudentDirectoryItem("STU-03", "Kabir Mehta", "kabir@roboticswala.com", "Pending Approval", "Drone Surveillance", 88.0, "Pending"),
        StudentDirectoryItem("STU-04", "Ananya Verma", "ananya@roboticswala.com", "Active", "Telepresence Bot", 96.2, "Approved"),
        StudentDirectoryItem("STU-05", "Dev Patel", "dev@roboticswala.com", "Pending Approval", "Exoskeleton Grip", 75.0, "Pending"),
        StudentDirectoryItem("STU-06", "Sneha Rao", "sneha@roboticswala.com", "Active", "Underwater ROV", 92.8, "Approved")
    ),
    val bookingsList: List<BookingRequestItem> = listOf(
        BookingRequestItem("BK-01", "Aarav Sharma", "RWH-STU-042", "Robotics Bay 3 (LiDAR Test)", "02:00 PM - 05:00 PM", "Today", "SLAM Sensor Bench", "Confirmed"),
        BookingRequestItem("BK-02", "Priya Nair", "RWH-STU-018", "Bambu X1 3D Printer 1", "03:30 PM - 06:00 PM", "Today", "Manipulator Gripper Prototype", "Confirmed"),
        BookingRequestItem("BK-03", "Kabir Mehta", "RWH-STU-077", "Aero Drone Flight Cage", "11:00 AM - 01:00 PM", "Tomorrow", "Quadcopter Hover Tuning", "Pending"),
        BookingRequestItem("BK-04", "Dev Patel", "RWH-STU-091", "Fiber Laser Cutter Bay", "04:00 PM - 05:30 PM", "Tomorrow", "Acrylic Sensor Enclosure", "Pending")
    ),
    val selectedTab: String = "admin_dashboard",
    val snackbarMessage: String? = null
)

class AdminViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    fun onTabSelected(route: String) {
        _uiState.update { it.copy(selectedTab = route) }
    }

    fun approveStudent(studentId: String) {
        _uiState.update { state ->
            val updated = state.studentsList.map {
                if (it.id == studentId) it.copy(status = "Approved", rfidStatus = "Active") else it
            }
            state.copy(
                studentsList = updated,
                snackbarMessage = "Student $studentId access approved!"
            )
        }
    }

    fun approveBooking(bookingId: String) {
        _uiState.update { state ->
            val updated = state.bookingsList.map {
                if (it.id == bookingId) it.copy(status = "Confirmed") else it
            }
            state.copy(
                bookingsList = updated,
                snackbarMessage = "Booking $bookingId confirmed!"
            )
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}
