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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.roboticswala.hub.ui.components.RoboticsOutlinedButton
import com.roboticswala.hub.ui.theme.CyberCyan
import com.roboticswala.hub.ui.theme.DarkSurface
import com.roboticswala.hub.ui.theme.DarkSurfaceBorder
import com.roboticswala.hub.ui.theme.ElectricBlue
import com.roboticswala.hub.ui.theme.LightSurface
import com.roboticswala.hub.ui.theme.LightSurfaceBorder
import com.roboticswala.hub.ui.theme.TextPrimaryDark
import com.roboticswala.hub.ui.theme.TextPrimaryLight
import com.roboticswala.hub.ui.theme.TextSecondaryDark
import com.roboticswala.hub.ui.theme.TextSecondaryLight

@Composable
fun AdminMoreScreen(
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val scrollState = rememberScrollState()
    var showingProjects by remember { mutableStateOf(false) }
    var showingTasks by remember { mutableStateOf(false) }
    var showingAchievements by remember { mutableStateOf(false) }
    var showingNotices by remember { mutableStateOf(false) }
    var showingEvents by remember { mutableStateOf(false) }
    var showingEquipment by remember { mutableStateOf(false) }
    var showingEquipmentRequests by remember { mutableStateOf(false) }
    var showingBudget by remember { mutableStateOf(false) }

    if (showingBudget) {
        Column(modifier = modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { showingBudget = false }) {
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
            onNavigateBack = { showingEquipmentRequests = false },
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
                IconButton(onClick = { showingEquipment = false }) {
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
                IconButton(onClick = { showingNotices = false }) {
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
                IconButton(onClick = { showingEvents = false }) {
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
                IconButton(onClick = { showingProjects = false }) {
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
                IconButton(onClick = { showingTasks = false }) {
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
                IconButton(onClick = { showingAchievements = false }) {
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Admin Settings & Management",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = if (isDark) TextPrimaryDark else TextPrimaryLight
        )
        Text(
            text = "Facility controls, achievement approvals, and task management",
            style = MaterialTheme.typography.bodySmall,
            color = if (isDark) TextSecondaryDark else TextSecondaryLight
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Day 10 Highlight Card: Achievements Approvals
        AdminOptionCard(
            title = "🏆 Achievement & Certificate Approvals",
            subtitle = "Verify certificates, approve hackathon awards, and publish to profile",
            icon = Icons.Filled.EmojiEvents,
            isDark = isDark,
            onClick = { showingAchievements = true }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Day 9 Highlight Card: Tasks Management
        AdminOptionCard(
            title = "📋 Weekly Tasks & Milestones",
            subtitle = "Assign tasks to approved students & review submitted deliverables",
            icon = Icons.Filled.Assignment,
            isDark = isDark,
            onClick = { showingTasks = true }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Day 8 Highlight Card: Projects Registry
        AdminOptionCard(
            title = "⚡ Projects Registry & Mentorship",
            subtitle = "Review all lab robotics projects, assign mentors, and audit budgets",
            icon = Icons.Filled.PrecisionManufacturing,
            isDark = isDark,
            onClick = { showingProjects = true }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Day 12 Highlight Card: Inventory & Equipment Stock
        AdminOptionCard(
            title = "📦 Inventory & Equipment Stock",
            subtitle = "Manage microcontrollers, sensors, stock alerts & issue requests",
            icon = Icons.Filled.Inventory2,
            isDark = isDark,
            onClick = { showingEquipment = true }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Day 11 Highlight Card: Notice Board Management
        AdminOptionCard(
            title = "📢 Notice Board & Broadcasts",
            subtitle = "Publish facility alerts, safety guidelines & announcements",
            icon = Icons.Filled.Campaign,
            isDark = isDark,
            onClick = { showingNotices = true }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Day 11 Highlight Card: Event & Workshop Management
        AdminOptionCard(
            title = "📅 Event & Workshop Scheduler",
            subtitle = "Create hackathons, seminars, and track student registrations",
            icon = Icons.Filled.Event,
            isDark = isDark,
            onClick = { showingEvents = true }
        )

        // Day 13 Highlight Card: Budget & Expense Management
        AdminOptionCard(
            title = "💰 Budget & Expense Management",
            subtitle = "Manage project grants, approve component receipts & audit finances",
            icon = Icons.Filled.Inventory2,
            isDark = isDark,
            onClick = { showingBudget = true }
        )

        Spacer(modifier = Modifier.height(12.dp))

        AdminOptionCard(
            title = "Security & RFID Gate Log",
            subtitle = "Live biometric access records and bay telemetry streams",
            icon = Icons.Filled.Security,
            isDark = isDark
        )

        Spacer(modifier = Modifier.height(12.dp))

        AdminOptionCard(
            title = "Robotics Lab Preferences",
            subtitle = "Configure station operating hours and machine quotas",
            icon = Icons.Filled.Tune,
            isDark = isDark
        )

        Spacer(modifier = Modifier.height(28.dp))

        RoboticsOutlinedButton(
            text = "Log Out of Admin Hub",
            onClick = onLogout,
            leadingIcon = Icons.Filled.Logout
        )

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun AdminOptionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isDark: Boolean,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .border(
                        width = 1.dp,
                        color = if (isDark) CyberCyan.copy(alpha = 0.4f) else ElectricBlue.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isDark) CyberCyan else ElectricBlue,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (isDark) TextPrimaryDark else TextPrimaryLight
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isDark) TextSecondaryDark else TextSecondaryLight
                )
            }
        }
    }
}
