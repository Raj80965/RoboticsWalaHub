package com.roboticswala.hub.ui.screens.student

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
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
import com.google.firebase.auth.FirebaseAuth
import com.roboticswala.hub.ui.components.RoboticsBackground
import com.roboticswala.hub.ui.components.RoboticsLogo
import com.roboticswala.hub.ui.navigation.StudentNavRoute
import com.roboticswala.hub.ui.screens.student.tabs.StudentActivityScreen
import com.roboticswala.hub.ui.screens.student.tabs.StudentHomeScreen
import com.roboticswala.hub.ui.screens.student.tabs.StudentProfileScreen
import com.roboticswala.hub.ui.screens.student.tabs.StudentProjectsScreen
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
fun StudentMainScreen(
    onLogout: () -> Unit,
    onNavigateToScanner: () -> Unit = {},
    onNavigateToAttendanceHistory: () -> Unit = {},
    onNavigateToBookings: () -> Unit = {},
    onNavigateToCreateProject: () -> Unit = {},
    onNavigateToProjectDetails: (String) -> Unit = {},
    onNavigateToTasks: () -> Unit = {}
) {
    val currentUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val viewModel: StudentViewModel = viewModel(
        factory = StudentViewModel.factory(uid = currentUid)
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isDark = isSystemInDarkTheme()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { msg ->
            snackbarHostState.showSnackbar("Error: $msg")
            viewModel.clearError()
        }
    }

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
                            RoboticsLogo(size = 32.dp, animate = false)
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
                                    .background(ElectricBlue.copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "STUDENT",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = if (isDark) CyberCyan else ElectricBlue
                                )
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = onLogout) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Logout,
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
                    StudentNavRoute.items.forEach { item ->
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
                Crossfade(
                    targetState = uiState.selectedTab,
                    label = "tab_crossfade"
                ) { targetTab ->
                    when (targetTab) {
                        StudentNavRoute.Home.route -> StudentHomeScreen(
                            uiState = uiState,
                            onRefresh = { viewModel.refresh() },
                            onNavigateToScanner = onNavigateToScanner,
                            onNavigateToAttendanceHistory = onNavigateToAttendanceHistory,
                            onNavigateToBookings = onNavigateToBookings,
                            onNavigateToProjects = { viewModel.onTabSelected(StudentNavRoute.Projects.route) },
                            onNavigateToProjectDetails = onNavigateToProjectDetails,
                            onNavigateToTasks = onNavigateToTasks
                        )
                        StudentNavRoute.Projects.route -> StudentProjectsScreen(
                            currentUid = currentUid,
                            onNavigateToCreate = onNavigateToCreateProject,
                            onNavigateToDetails = onNavigateToProjectDetails
                        )
                        StudentNavRoute.Activity.route -> StudentActivityScreen(
                            activities = uiState.activityLogs,
                            currentUid = currentUid
                        )
                        StudentNavRoute.Profile.route -> StudentProfileScreen(
                            userProfile = uiState.userProfile,
                            onLogout = onLogout
                        )
                        else -> StudentHomeScreen(
                            uiState = uiState,
                            onRefresh = { viewModel.refresh() },
                            onNavigateToScanner = onNavigateToScanner,
                            onNavigateToAttendanceHistory = onNavigateToAttendanceHistory,
                            onNavigateToBookings = onNavigateToBookings,
                            onNavigateToProjects = { viewModel.onTabSelected(StudentNavRoute.Projects.route) },
                            onNavigateToProjectDetails = onNavigateToProjectDetails,
                            onNavigateToTasks = onNavigateToTasks
                        )
                    }
                }
            }
        }
    }
}
