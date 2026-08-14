package com.roboticswala.hub.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash_screen")
    object Login : Screen("login_screen")
    object Register : Screen("register_screen")
    object PendingApproval : Screen("pending_approval_screen")
    object StudentMain : Screen("student_main_screen")
    object AdminMain : Screen("admin_main_screen")
}
