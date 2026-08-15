package com.roboticswala.hub.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash_screen")
    object Login : Screen("login_screen")
    object Register : Screen("register_screen")
    object PendingApproval : Screen("pending_approval_screen")
    object StudentMain : Screen("student_main_screen")
    object AdminMain : Screen("admin_main_screen")
    object StudentQRScanner : Screen("student_qr_scanner")
    object StudentAttendanceHistory : Screen("student_attendance_history")
    object StudentBookings : Screen("student_bookings")
    object CreateProject : Screen("create_project")
    object ProjectDetails : Screen("project_details/{projectId}") {
        fun createRoute(projectId: String) = "project_details/$projectId"
    }
    object StudentTasks : Screen("student_tasks")
}
