package com.roboticswala.hub.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.roboticswala.hub.ui.components.RoboticsBackground
import com.roboticswala.hub.ui.components.RoboticsLogo
import com.roboticswala.hub.ui.navigation.AdminNavRoute
import com.roboticswala.hub.ui.screens.admin.tabs.AdminAttendanceScreen
import com.roboticswala.hub.ui.screens.admin.tabs.AdminBookingsScreen
import com.roboticswala.hub.ui.screens.admin.tabs.AdminDashboardScreen
import com.roboticswala.hub.ui.screens.admin.tabs.AdminMoreScreen
import com.roboticswala.hub.ui.screens.admin.tabs.AdminStudentsScreen
import com.roboticswala.hub.ui.theme.CircuitWarning
import com.roboticswala.hub.ui.theme.CyberCyan
import com.roboticswala.hub.ui.theme.DarkBackground
import com.roboticswala.hub.ui.theme.DarkSurface
import com.roboticswala.hub.ui.theme.DarkSurfaceBorder
import com.roboticswala.hub.ui.theme.DarkSurfaceElevated
import com.roboticswala.hub.ui.theme.ElectricBlue
import com.roboticswala.hub.ui.theme.LightBackground
import com.roboticswala.hub.ui.theme.LightSurface
import com.roboticswala.hub.ui.theme.LightSurfaceBorder
import com.roboticswala.hub.ui.theme.LightSurfaceElevated
import com.roboticswala.hub.ui.theme.TextPrimaryDark
import com.roboticswala.hub.ui.theme.TextPrimaryLight
import com.roboticswala.hub.ui.theme.TextSecondaryDark
import com.roboticswala.hub.ui.theme.TextSecondaryLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMainScreen(
    onLogout: () -> Unit,
    viewModel: AdminViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isDark = isSystemInDarkTheme()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearMessage()
        }
    }

    RoboticsBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RoboticsLogo(
                                size = 32.dp,
                                animate = false
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Robotics Wala Hub",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = (-0.3).sp
                                ),
                                color = if (isDark) TextPrimaryDark else TextPrimaryLight
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(CircuitWarning.copy(alpha = 0.2f))
                                    .clickable { viewModel.onTabSelected(AdminNavRoute.More.route) }
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "ADMIN",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = CircuitWarning
                                )
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = onLogout) {
                            Icon(
                                imageVector = Icons.Filled.Logout,
                                contentDescription = "Log Out",
                                tint = if (isDark) TextSecondaryDark else TextSecondaryLight
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = if (isDark) DarkBackground.copy(alpha = 0.95f) else LightBackground.copy(alpha = 0.95f)
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = if (isDark) DarkSurface.copy(alpha = 0.98f) else LightSurface.copy(alpha = 0.98f),
                    modifier = Modifier.border(
                        width = 1.dp,
                        color = if (isDark) DarkSurfaceBorder else LightSurfaceBorder
                    )
                ) {
                    AdminNavRoute.items.forEach { item ->
                        val isSelected = uiState.selectedTab == item.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { viewModel.onTabSelected(item.route) },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.title
                                )
                            },
                            label = {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = if (isDark) CyberCyan else ElectricBlue,
                                selectedTextColor = if (isDark) CyberCyan else ElectricBlue,
                                unselectedIconColor = if (isDark) TextSecondaryDark else TextSecondaryLight,
                                unselectedTextColor = if (isDark) TextSecondaryDark else TextSecondaryLight,
                                indicatorColor = if (isDark) DarkSurfaceElevated else LightSurfaceElevated
                            )
                        )
                    }
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (uiState.selectedTab) {
                    AdminNavRoute.Dashboard.route -> AdminDashboardScreen(data = uiState.dashboardData)
                    AdminNavRoute.Attendance.route -> AdminAttendanceScreen()
                    AdminNavRoute.Students.route -> AdminStudentsScreen(
                        students = uiState.studentsList,
                        onApproveStudent = viewModel::approveStudent,
                        onDeleteStudent = viewModel::deleteStudent
                    )
                    AdminNavRoute.Bookings.route -> AdminBookingsScreen()
                    AdminNavRoute.More.route -> AdminMoreScreen(
                        adminProfile = uiState.adminProfile,
                        onUpdateProfile = viewModel::updateAdminProfile,
                        onUpdatePhoto = viewModel::updateAdminPhoto,
                        onLogout = onLogout
                    )
                    else -> AdminDashboardScreen(data = uiState.dashboardData)
                }
            }
        }
    }
}
