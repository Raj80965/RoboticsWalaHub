package com.roboticswala.hub.ui.screens.student

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import com.roboticswala.hub.ui.theme.CircuitError
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import com.roboticswala.hub.ui.components.ChatInAppBanner
import com.roboticswala.hub.utils.ChatNotificationHelper
import kotlinx.coroutines.delay
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
    onNavigateToTasks: () -> Unit = {},
    onNavigateToAchievements: () -> Unit = {},
    onNavigateToCreateAchievement: () -> Unit = {},
    onNavigateToNotices: () -> Unit = {},
    onNavigateToEvents: () -> Unit = {},
    onNavigateToEquipment: () -> Unit = {},
    onNavigateToExpenses: () -> Unit = {},
    onNavigateToLeaderboard: () -> Unit = {},
    onNavigateToChat: () -> Unit = {}
) {
    val currentUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val viewModel: StudentViewModel = viewModel(
        factory = StudentViewModel.factory(uid = currentUid)
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isDark = isSystemInDarkTheme()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = androidx.compose.ui.platform.LocalContext.current
    val chatRepository = remember { com.roboticswala.hub.data.repository.ChatRepository() }
    val unreadCount by chatRepository.getUnreadCountFlow(context, currentUid).collectAsStateWithLifecycle(initialValue = 0)

    var bannerMessage by remember { mutableStateOf<com.roboticswala.hub.data.models.ChatMessage?>(null) }
    var showBanner by remember { mutableStateOf(false) }

    LaunchedEffect(currentUid) {
        if (currentUid.isNotBlank()) {
            chatRepository.getLatestIncomingMessageFlow(currentUid).collect { newMsg ->
                if (newMsg != null) {
                    bannerMessage = newMsg
                    showBanner = true
                    ChatNotificationHelper.showChatNotification(
                        context = context,
                        senderName = newMsg.senderName,
                        senderRole = newMsg.senderRole,
                        messageText = newMsg.message
                    )
                    delay(4500)
                    showBanner = false
                }
            }
        }
    }

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
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (isDark) DarkSurfaceElevated else LightSurfaceBorder.copy(alpha = 0.5f)
                                    )
                                    .border(
                                        1.dp,
                                        if (isDark) DarkSurfaceBorder else LightSurfaceBorder,
                                        RoundedCornerShape(10.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "🤖",
                                    fontSize = 18.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "RW HUB",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = (-0.3).sp
                                    ),
                                    color = if (isDark) TextPrimaryDark else TextPrimaryLight
                                )
                                Text(
                                    text = "STUDENT PORTAL",
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
                        IconButton(onClick = onNavigateToChat) {
                            BadgedBox(
                                badge = {
                                    if (unreadCount > 0) {
                                        Badge(
                                            containerColor = CircuitError,
                                            contentColor = Color.White
                                        ) {
                                            Text(
                                                text = if (unreadCount > 99) "99+" else unreadCount.toString(),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Chat,
                                    contentDescription = "Community Chat",
                                    tint = if (isDark) CyberCyan else ElectricBlue
                                )
                            }
                        }
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
                    StudentNavRoute.items.filterNotNull().forEach { item ->
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
            }
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
                            onNavigateToTasks = onNavigateToTasks,
                            onNavigateToAchievements = onNavigateToAchievements,
                            onNavigateToNotices = onNavigateToNotices,
                            onNavigateToEvents = onNavigateToEvents,
                            onNavigateToEquipment = onNavigateToEquipment,
                            onNavigateToExpenses = onNavigateToExpenses,
                            onNavigateToLeaderboard = onNavigateToLeaderboard
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
                            onLogout = onLogout,
                            onNavigateToAchievements = onNavigateToAchievements
                        )
                        else -> StudentHomeScreen(
                            uiState = uiState,
                            onRefresh = { viewModel.refresh() },
                            onNavigateToScanner = onNavigateToScanner,
                            onNavigateToAttendanceHistory = onNavigateToAttendanceHistory,
                            onNavigateToBookings = onNavigateToBookings,
                            onNavigateToProjects = { viewModel.onTabSelected(StudentNavRoute.Projects.route) },
                            onNavigateToProjectDetails = onNavigateToProjectDetails,
                            onNavigateToTasks = onNavigateToTasks,
                            onNavigateToAchievements = onNavigateToAchievements,
                            onNavigateToNotices = onNavigateToNotices,
                            onNavigateToEvents = onNavigateToEvents
                        )
                    }

                    ChatInAppBanner(
                        message = bannerMessage,
                        visible = showBanner,
                        onNavigateToChat = {
                            showBanner = false
                            onNavigateToChat()
                        },
                        onDismiss = { showBanner = false },
                        isDark = isDark,
                        modifier = Modifier.align(Alignment.TopCenter)
                    )
                }
            }
        }
    }
}
