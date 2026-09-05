package com.roboticswala.hub.ui.screens.admin.tabs

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.activity.compose.BackHandler
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.roboticswala.hub.ui.components.RoboticsOutlinedButton
import com.roboticswala.hub.ui.theme.CyberCyan
import com.roboticswala.hub.ui.theme.CyberCyanGlow
import com.roboticswala.hub.ui.theme.DarkSurface
import com.roboticswala.hub.ui.theme.DarkSurfaceBorder
import com.roboticswala.hub.ui.theme.ElectricBlue
import com.roboticswala.hub.ui.theme.LightSurface
import com.roboticswala.hub.ui.theme.LightSurfaceBorder
import com.roboticswala.hub.ui.theme.TextPrimaryDark
import com.roboticswala.hub.ui.theme.TextPrimaryLight
import com.roboticswala.hub.ui.theme.TextSecondaryDark
import com.roboticswala.hub.ui.theme.TextSecondaryLight

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.Person
import coil.compose.AsyncImage
import com.roboticswala.hub.data.models.UserProfile

import androidx.compose.runtime.LaunchedEffect

@Composable
fun AdminMoreScreen(
    onLogout: () -> Unit,
    adminProfile: UserProfile? = null,
    onUpdateProfile: (fullName: String, phone: String, college: String, branch: String, year: String, adminId: String, emergencyContact: String) -> Unit = { _, _, _, _, _, _, _ -> },
    onUpdatePhoto: (photoBase64: String) -> Unit = {},
    onNavigateToChat: () -> Unit = {},
    unreadChatCount: Int = 0,
    initialSubScreen: String? = null,
    onSubScreenCleared: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val scrollState = rememberScrollState()
    var showingProfile by remember { mutableStateOf(false) }
    var showingProjects by remember { mutableStateOf(false) }
    var showingTasks by remember { mutableStateOf(false) }
    var showingAchievements by remember { mutableStateOf(false) }
    var showingNotices by remember { mutableStateOf(false) }
    var showingEvents by remember { mutableStateOf(false) }
    var showingEquipment by remember { mutableStateOf(false) }
    var showingEquipmentRequests by remember { mutableStateOf(false) }
    var showingBudget by remember { mutableStateOf(false) }
    var showingAnalytics by remember { mutableStateOf(false) }
    var showingGateLog by remember { mutableStateOf(false) }
    var showingLabConfig by remember { mutableStateOf(false) }

    val dismissSubScreen: () -> Unit = {
        showingProfile = false
        showingProjects = false
        showingTasks = false
        showingAchievements = false
        showingNotices = false
        showingEvents = false
        showingEquipment = false
        showingEquipmentRequests = false
        showingBudget = false
        showingAnalytics = false
        showingGateLog = false
        showingLabConfig = false
        onSubScreenCleared()
    }

    val isAnySubScreenActive = showingProfile || showingProjects || showingTasks ||
            showingAchievements || showingNotices || showingEvents || showingEquipment ||
            showingEquipmentRequests || showingBudget || showingAnalytics

    BackHandler(enabled = isAnySubScreenActive) {
        if (showingEquipmentRequests && showingEquipment) {
            showingEquipmentRequests = false
        } else {
            dismissSubScreen()
        }
    }

    LaunchedEffect(initialSubScreen) {
        when (initialSubScreen) {
            "projects" -> showingProjects = true
            "equipment" -> showingEquipment = true
            "tasks" -> showingTasks = true
            "achievements" -> showingAchievements = true
            "notices" -> showingNotices = true
            "events" -> showingEvents = true
            "budget" -> showingBudget = true
            "analytics" -> showingAnalytics = true
            "equipment_requests" -> showingEquipmentRequests = true
            "profile" -> showingProfile = true
        }
    }

    if (showingProfile) {
        Column(modifier = modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = dismissSubScreen) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Settings",
                        tint = if (isDark) CyberCyan else ElectricBlue
                    )
                }
                Text(
                    text = "Back to Admin Options",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = if (isDark) CyberCyan else ElectricBlue
                )
            }
            AdminProfileScreen(
                adminProfile = adminProfile,
                onUpdateProfile = onUpdateProfile,
                onUpdatePhoto = onUpdatePhoto,
                onLogout = onLogout
            )
        }
        return
    }

    if (showingAnalytics) {
        Column(modifier = modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = dismissSubScreen) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Settings",
                        tint = if (isDark) CyberCyan else ElectricBlue
                    )
                }
                Text(
                    text = "Back to Admin Options",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = if (isDark) CyberCyan else ElectricBlue
                )
            }
            AdminAnalyticsScreen()
        }
        return
    }

    if (showingBudget) {
        Column(modifier = modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = dismissSubScreen) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Settings",
                        tint = if (isDark) CyberCyan else ElectricBlue
                    )
                }
                Text(
                    text = "Back to Admin Options",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = if (isDark) CyberCyan else ElectricBlue
                )
            }
            AdminBudgetScreen()
        }
        return
    }

    if (showingEquipmentRequests) {
        AdminEquipmentRequestsScreen(
            onNavigateBack = {
                showingEquipmentRequests = false
                if (!showingEquipment) {
                    onSubScreenCleared()
                }
            },
            modifier = modifier
        )
        return
    }

    if (showingEquipment) {
        Column(modifier = modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = dismissSubScreen) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Settings",
                        tint = if (isDark) CyberCyan else ElectricBlue
                    )
                }
                Text(
                    text = "Back to Admin Options",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = if (isDark) CyberCyan else ElectricBlue
                )
            }
            AdminEquipmentScreen(
                onNavigateToRequests = { showingEquipmentRequests = true }
            )
        }
        return
    }

    if (showingNotices) {
        Column(modifier = modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = dismissSubScreen) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Settings",
                        tint = if (isDark) CyberCyan else ElectricBlue
                    )
                }
                Text(
                    text = "Back to Admin Options",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = if (isDark) CyberCyan else ElectricBlue
                )
            }
            AdminNoticesScreen()
        }
        return
    }

    if (showingEvents) {
        Column(modifier = modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = dismissSubScreen) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Settings",
                        tint = if (isDark) CyberCyan else ElectricBlue
                    )
                }
                Text(
                    text = "Back to Admin Options",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = if (isDark) CyberCyan else ElectricBlue
                )
            }
            AdminEventsScreen()
        }
        return
    }

    if (showingProjects) {
        Column(modifier = modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = dismissSubScreen) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Settings",
                        tint = if (isDark) CyberCyan else ElectricBlue
                    )
                }
                Text(
                    text = "Back to Admin Options",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = if (isDark) CyberCyan else ElectricBlue
                )
            }
            AdminProjectsScreen()
        }
        return
    }

    if (showingTasks) {
        Column(modifier = modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = dismissSubScreen) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Settings",
                        tint = if (isDark) CyberCyan else ElectricBlue
                    )
                }
                Text(
                    text = "Back to Admin Options",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = if (isDark) CyberCyan else ElectricBlue
                )
            }
            AdminTasksScreen()
        }
        return
    }

    if (showingAchievements) {
        Column(modifier = modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = dismissSubScreen) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Settings",
                        tint = if (isDark) CyberCyan else ElectricBlue
                    )
                }
                Text(
                    text = "Back to Admin Options",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = if (isDark) CyberCyan else ElectricBlue
                )
            }
            AdminAchievementsScreen()
        }
        return
    }

    val adminPhotoUrl = adminProfile?.photoUrl.orEmpty()
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        // Section Header
        Text(
            text = "Admin Control Center",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.3).sp
            ),
            color = if (isDark) TextPrimaryDark else TextPrimaryLight
        )
        Text(
            text = "Facility management, approvals, inventory & system controls",
            style = MaterialTheme.typography.bodySmall,
            color = if (isDark) TextSecondaryDark else TextSecondaryLight
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 👑 HERO: ADMIN PROFILE & ID DOSSIER CARD (Compact & Sleek)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showingProfile = true }
                .border(
                    width = 1.dp,
                    color = if (isDark) CyberCyan.copy(alpha = 0.45f) else ElectricBlue.copy(alpha = 0.35f),
                    shape = RoundedCornerShape(18.dp)
                ),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDark) DarkSurface else LightSurface
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(
                            if (isDark) CyberCyan.copy(alpha = 0.25f) else ElectricBlue.copy(alpha = 0.18f)
                        )
                        .border(
                            width = 2.dp,
                            brush = Brush.linearGradient(
                                colors = if (isDark) listOf(CyberCyan, CyberCyanGlow, ElectricBlue)
                                else listOf(ElectricBlue, CyberCyan, ElectricBlue)
                            ),
                            shape = RoundedCornerShape(15.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (adminPhotoBitmap != null) {
                        Image(
                            bitmap = adminPhotoBitmap.asImageBitmap(),
                            contentDescription = "Admin Profile Photo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else if (adminPhotoUrl.startsWith("http")) {
                        AsyncImage(
                            model = adminPhotoUrl,
                            contentDescription = "Admin Profile Photo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Admin Profile",
                            tint = if (isDark) CyberCyan else ElectricBlue,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = adminProfile?.fullName ?: "Admin In-Charge",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (isDark) TextPrimaryDark else TextPrimaryLight
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    if (isDark) CyberCyan.copy(alpha = 0.2f) else ElectricBlue.copy(alpha = 0.15f)
                                )
                                .padding(horizontal = 5.dp, vertical = 1.5.dp)
                        ) {
                            Text(
                                text = "ADMIN",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold
                                ),
                                color = if (isDark) CyberCyan else ElectricBlue
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "ID: ${adminProfile?.displayAdminId ?: "RWH-ADM-2026-001"} • Dossier & Permissions",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = if (isDark) TextSecondaryDark else TextSecondaryLight
                    )
                }

                Icon(
                    imageVector = Icons.Filled.Tune,
                    contentDescription = "Open Profile",
                    tint = if (isDark) CyberCyan else ElectricBlue,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Quick Grid Title
        Text(
            text = "MANAGEMENT MODULES",
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = if (isDark) CyberCyan else ElectricBlue
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 2-COLUMN COMPACT GRID
        // Row 1: Achievements & Tasks
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AdminGridTile(
                modifier = Modifier.weight(1f),
                title = "Achievements",
                subtitle = "Approve certs & awards",
                icon = Icons.Filled.EmojiEvents,
                badge = "Certificates",
                isDark = isDark,
                onClick = { showingAchievements = true }
            )
            AdminGridTile(
                modifier = Modifier.weight(1f),
                title = "Weekly Tasks",
                subtitle = "Milestones & reviews",
                icon = Icons.Filled.Assignment,
                badge = "Assignments",
                isDark = isDark,
                onClick = { showingTasks = true }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Row 2: Projects & Inventory
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AdminGridTile(
                modifier = Modifier.weight(1f),
                title = "Projects Hub",
                subtitle = "Registry & mentors",
                icon = Icons.Filled.PrecisionManufacturing,
                badge = "Robotics Lab",
                isDark = isDark,
                onClick = { showingProjects = true }
            )
            AdminGridTile(
                modifier = Modifier.weight(1f),
                title = "Inventory",
                subtitle = "Sensors & stock alerts",
                icon = Icons.Filled.Inventory2,
                badge = "Equipment",
                isDark = isDark,
                onClick = { showingEquipment = true }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Row 3: Notices & Events
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AdminGridTile(
                modifier = Modifier.weight(1f),
                title = "Notice Board",
                subtitle = "Broadcast lab alerts",
                icon = Icons.Filled.Campaign,
                badge = "Circulars",
                isDark = isDark,
                onClick = { showingNotices = true }
            )
            AdminGridTile(
                modifier = Modifier.weight(1f),
                title = "Events & Meets",
                subtitle = "Hackathons & workshops",
                icon = Icons.Filled.Event,
                badge = "Schedules",
                isDark = isDark,
                onClick = { showingEvents = true }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Row 4: Budget & Analytics
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AdminGridTile(
                modifier = Modifier.weight(1f),
                title = "Budget & Grants",
                subtitle = "Expense audits & claims",
                icon = Icons.Filled.Inventory2,
                badge = "Finances",
                isDark = isDark,
                onClick = { showingBudget = true }
            )
            AdminGridTile(
                modifier = Modifier.weight(1f),
                title = "Hub Analytics",
                subtitle = "Trends & export reports",
                icon = Icons.Filled.PrecisionManufacturing,
                badge = "Reports",
                isDark = isDark,
                onClick = { showingAnalytics = true }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Row 5: Security & Preferences
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AdminGridTile(
                modifier = Modifier.weight(1f),
                title = "Gate Access",
                subtitle = "Biometric & RFID log",
                icon = Icons.Filled.Security,
                badge = "Security",
                isDark = isDark,
                onClick = { showingGateLog = true }
            )
            AdminGridTile(
                modifier = Modifier.weight(1f),
                title = "Lab Config",
                subtitle = "Operating hours & bays",
                icon = Icons.Filled.Tune,
                badge = "Settings",
                isDark = isDark,
                onClick = { showingLabConfig = true }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Row 6: Community & Discussion
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AdminGridTile(
                modifier = Modifier.weight(1f),
                title = "Community Chat",
                subtitle = "Real-time lab discussion",
                icon = Icons.AutoMirrored.Filled.Chat,
                badge = if (unreadChatCount > 0) "$unreadChatCount NEW" else "Live Room",
                isDark = isDark,
                onClick = onNavigateToChat
            )
            Spacer(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(24.dp))

        RoboticsOutlinedButton(
            text = "Log Out of Admin Hub",
            onClick = onLogout,
            leadingIcon = Icons.Filled.Logout
        )

        Spacer(modifier = Modifier.height(36.dp))
    }

    // Gate Access & RFID Biometric Dialog
    if (showingGateLog) {
        AlertDialog(
            onDismissRequest = { showingGateLog = false },
            title = {
                Text(
                    text = "🔒 RFID Gate & Security Logs",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (isDark) CyberCyan else ElectricBlue
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Real-time RFID barrier status: ONLINE",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = if (isDark) TextPrimaryDark else TextPrimaryLight
                    )
                    Text(
                        text = "• Main Bay Turnstile: 🟢 Normal Access Enabled\n• Hardware Lab Door #1: 🟢 RFID Active\n• Robotics Testing Arena: 🟢 Monitored\n• Last biometric pulse: 12 seconds ago",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isDark) TextSecondaryDark else TextSecondaryLight
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showingGateLog = false }) {
                    Text(
                        text = "Close",
                        color = if (isDark) CyberCyan else ElectricBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            containerColor = if (isDark) DarkSurface else LightSurface
        )
    }

    // Lab Preferences & Hours Config Dialog
    if (showingLabConfig) {
        AlertDialog(
            onDismissRequest = { showingLabConfig = false },
            title = {
                Text(
                    text = "⚙️ Robotics Lab Preferences",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (isDark) CyberCyan else ElectricBlue
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Facility Operating Parameters:",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = if (isDark) TextPrimaryDark else TextPrimaryLight
                    )
                    Text(
                        text = "• Station Operating Hours: 08:00 AM - 08:00 PM\n• Max Slot Booking Duration: 4 Hours\n• Machine Quota: 2 Workbenches / Student\n• 3D Printing Auto-Shutdown: 09:00 PM\n• Auto-Attendance Grace Period: 15 Mins",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isDark) TextSecondaryDark else TextSecondaryLight
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showingLabConfig = false }) {
                    Text(
                        text = "Done",
                        color = if (isDark) CyberCyan else ElectricBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            containerColor = if (isDark) DarkSurface else LightSurface
        )
    }
}

@Composable
private fun AdminGridTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    badge: String,
    isDark: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .border(
                width = 1.dp,
                color = if (isDark) DarkSurfaceBorder else LightSurfaceBorder,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) DarkSurface else LightSurface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isDark) CyberCyan.copy(alpha = 0.22f) else ElectricBlue.copy(alpha = 0.16f)
                        )
                        .border(
                            width = 1.5.dp,
                            color = if (isDark) CyberCyan.copy(alpha = 0.85f) else ElectricBlue.copy(alpha = 0.7f),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = if (isDark) CyberCyan else ElectricBlue,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (isDark) DarkSurfaceBorder.copy(alpha = 0.6f) else LightSurfaceBorder.copy(alpha = 0.8f)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = if (isDark) TextSecondaryDark else TextSecondaryLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp
                ),
                color = if (isDark) TextPrimaryDark else TextPrimaryLight,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 10.5.sp,
                    lineHeight = 13.sp
                ),
                color = if (isDark) TextSecondaryDark else TextSecondaryLight,
                maxLines = 1
            )
        }
    }
}
