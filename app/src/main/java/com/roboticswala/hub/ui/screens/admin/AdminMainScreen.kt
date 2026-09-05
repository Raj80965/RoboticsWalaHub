package com.roboticswala.hub.ui.screens.admin

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.activity.compose.BackHandler
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.material3.Scaffold
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
import com.roboticswala.hub.ui.components.RoboticsBackground
import com.roboticswala.hub.ui.components.RoboticsLogo
import com.roboticswala.hub.ui.navigation.AdminNavRoute
import com.roboticswala.hub.ui.screens.admin.tabs.AdminAttendanceScreen
import com.roboticswala.hub.ui.screens.admin.tabs.AdminBookingsScreen
import com.roboticswala.hub.ui.screens.admin.tabs.AdminDashboardScreen
import com.roboticswala.hub.ui.screens.admin.tabs.AdminMoreScreen
import com.roboticswala.hub.ui.screens.admin.tabs.AdminStudentsScreen
import com.roboticswala.hub.ui.theme.CircuitError
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
    onNavigateToChat: () -> Unit = {},
    viewModel: AdminViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isDark = isSystemInDarkTheme()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = androidx.compose.ui.platform.LocalContext.current
    val currentUid = remember { com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid.orEmpty() }
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

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearMessage()
        }
    }

    BackHandler(enabled = uiState.selectedTab != AdminNavRoute.Dashboard.route && uiState.selectedSubScreen == null) {
        viewModel.onTabSelected(AdminNavRoute.Dashboard.route)
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
                                text = "RW HUB",
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
                        val adminPhotoUrl = uiState.adminProfile?.photoUrl.orEmpty()
                        val adminPhotoBitmap = remember(adminPhotoUrl) {
                            if (adminPhotoUrl.isNotBlank() && !adminPhotoUrl.startsWith("http")) {
                                try {
                                    val clean = if (adminPhotoUrl.contains(",")) adminPhotoUrl.substringAfter(",") else adminPhotoUrl
                                    val bytes = Base64.decode(clean, Base64.DEFAULT)
                                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                                } catch (e: Exception) {
                                    null
                                }
                            } else null
                        }

                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(if (isDark) CyberCyan.copy(alpha = 0.15f) else ElectricBlue.copy(alpha = 0.12f))
                                .border(
                                    width = 1.5.dp,
                                    color = if (isDark) CyberCyan.copy(alpha = 0.6f) else ElectricBlue.copy(alpha = 0.5f),
                                    shape = CircleShape
                                )
                                .clickable { viewModel.onTabSelected(AdminNavRoute.More.route) },
                            contentAlignment = Alignment.Center
                        ) {
                            if (adminPhotoBitmap != null) {
                                Image(
                                    bitmap = adminPhotoBitmap.asImageBitmap(),
                                    contentDescription = "Admin Avatar",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else if (adminPhotoUrl.startsWith("http")) {
                                AsyncImage(
                                    model = adminPhotoUrl,
                                    contentDescription = "Admin Avatar",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.Person,
                                    contentDescription = "Admin Profile",
                                    tint = if (isDark) CyberCyan else ElectricBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        IconButton(onClick = onNavigateToChat) {
                            androidx.compose.material3.BadgedBox(
                                badge = {
                                    if (unreadCount > 0) {
                                        androidx.compose.material3.Badge(
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
                    AdminNavRoute.items.filterNotNull().forEach { item ->
                        val isSelected = uiState.selectedTab == item.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { viewModel.onTabSelected(item.route) },
                            icon = {
                                if (item == AdminNavRoute.More && unreadCount > 0) {
                                    androidx.compose.material3.BadgedBox(
                                        badge = {
                                            androidx.compose.material3.Badge(
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
                                    ) {
                                        Icon(
                                            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                            contentDescription = item.title
                                        )
                                    }
                                } else {
                                    Icon(
                                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = item.title
                                    )
                                }
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
                    AdminNavRoute.Dashboard.route -> AdminDashboardScreen(
                        data = uiState.dashboardData,
                        onNavigateToStudents = { filter ->
                            viewModel.setStudentsFilter(filter)
                            viewModel.onTabSelected(AdminNavRoute.Students.route)
                        },
                        onNavigateToAttendance = { viewModel.onTabSelected(AdminNavRoute.Attendance.route) },
                        onNavigateToBookings = { viewModel.onTabSelected(AdminNavRoute.Bookings.route) },
                        onNavigateToProjects = { viewModel.onTabSelected(AdminNavRoute.More.route, subScreen = "projects", returnToDashboard = true) },
                        onNavigateToEquipment = { viewModel.onTabSelected(AdminNavRoute.More.route, subScreen = "equipment", returnToDashboard = true) },
                        onNavigateToMore = { viewModel.onTabSelected(AdminNavRoute.More.route) },
                        onNavigateToChat = onNavigateToChat,
                        unreadChatCount = unreadCount
                    )
                    AdminNavRoute.Attendance.route -> AdminAttendanceScreen()
                    AdminNavRoute.Students.route -> AdminStudentsScreen(
                        students = uiState.studentsList,
                        activeFilter = uiState.studentsFilter,
                        onFilterChanged = viewModel::setStudentsFilter,
                        onApproveStudent = viewModel::approveStudent,
                        onDeleteStudent = viewModel::deleteStudent
                    )
                    AdminNavRoute.Bookings.route -> AdminBookingsScreen()
                    AdminNavRoute.More.route -> AdminMoreScreen(
                        adminProfile = uiState.adminProfile,
                        onUpdateProfile = viewModel::updateAdminProfile,
                        onUpdatePhoto = viewModel::updateAdminPhoto,
                        onLogout = onLogout,
                        onNavigateToChat = onNavigateToChat,
                        unreadChatCount = unreadCount,
                        initialSubScreen = uiState.selectedSubScreen,
                        onSubScreenCleared = viewModel::onSubScreenDismissed
                    )
                    else -> AdminDashboardScreen(
                        data = uiState.dashboardData,
                        onNavigateToStudents = { filter ->
                            viewModel.setStudentsFilter(filter)
                            viewModel.onTabSelected(AdminNavRoute.Students.route)
                        },
                        onNavigateToAttendance = { viewModel.onTabSelected(AdminNavRoute.Attendance.route) },
                        onNavigateToBookings = { viewModel.onTabSelected(AdminNavRoute.Bookings.route) },
                        onNavigateToProjects = { viewModel.onTabSelected(AdminNavRoute.More.route, subScreen = "projects", returnToDashboard = true) },
                        onNavigateToEquipment = { viewModel.onTabSelected(AdminNavRoute.More.route, subScreen = "equipment", returnToDashboard = true) },
                        onNavigateToMore = { viewModel.onTabSelected(AdminNavRoute.More.route) },
                        onNavigateToChat = onNavigateToChat,
                        unreadChatCount = unreadCount
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
