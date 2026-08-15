package com.roboticswala.hub.ui.screens.student.tabs

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RssFeed
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.roboticswala.hub.data.models.ActivityLogItem
import com.roboticswala.hub.data.models.DailyWorkUpdate
import com.roboticswala.hub.data.models.UserProfile
import com.roboticswala.hub.ui.components.RoboticsOutlinedButton
import com.roboticswala.hub.ui.components.RoboticsPrimaryButton
import com.roboticswala.hub.ui.screens.student.work.CreateWorkUpdateDialog
import com.roboticswala.hub.ui.screens.student.work.DailyWorkDetailDialog
import com.roboticswala.hub.ui.screens.student.work.DailyWorkViewModel
import com.roboticswala.hub.ui.screens.student.work.DailyWorkViewModelFactory
import com.roboticswala.hub.ui.theme.CircuitError
import com.roboticswala.hub.ui.theme.CircuitSuccess
import com.roboticswala.hub.ui.theme.CyberCyan
import com.roboticswala.hub.ui.theme.DarkSurface
import com.roboticswala.hub.ui.theme.DarkSurfaceBorder
import com.roboticswala.hub.ui.theme.DarkSurfaceElevated
import com.roboticswala.hub.ui.theme.ElectricBlue
import com.roboticswala.hub.ui.theme.LightSurface
import com.roboticswala.hub.ui.theme.LightSurfaceBorder
import com.roboticswala.hub.ui.theme.LightSurfaceElevated
import com.roboticswala.hub.ui.theme.TextPrimaryDark
import com.roboticswala.hub.ui.theme.TextPrimaryLight
import com.roboticswala.hub.ui.theme.TextSecondaryDark
import com.roboticswala.hub.ui.theme.TextSecondaryLight

@Composable
fun StudentActivityScreen(
    activities: List<ActivityLogItem> = emptyList(),
    currentUid: String = FirebaseAuth.getInstance().currentUser?.uid ?: "",
    viewModel: DailyWorkViewModel = viewModel(factory = DailyWorkViewModelFactory(currentUid)),
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val surfaceColor = if (isDark) DarkSurface else LightSurface
    val elevatedColor = if (isDark) DarkSurfaceElevated else LightSurfaceElevated
    val borderColor = if (isDark) DarkSurfaceBorder else LightSurfaceBorder
    val textColor = if (isDark) TextPrimaryDark else TextPrimaryLight
    val textSecColor = if (isDark) TextSecondaryDark else TextSecondaryLight

    val currentUser = FirebaseAuth.getInstance().currentUser
    val userProfile = UserProfile(
        uid = currentUid,
        fullName = currentUser?.displayName ?: "Aarav Sharma",
        studentId = "RW-STU-042",
        status = "Approved"
    )

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Daily Work Progress",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = textColor
                )
                Text(
                    text = "Log daily hardware fabrication, AI testing, and ROS2 sprint updates",
                    style = MaterialTheme.typography.bodySmall,
                    color = textSecColor
                )
            }

            // Dual Tab Selector (Feed / My History)
            TabRow(
                selectedTabIndex = uiState.selectedTab,
                containerColor = surfaceColor,
                contentColor = if (isDark) CyberCyan else ElectricBlue,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[uiState.selectedTab]),
                        color = if (isDark) CyberCyan else ElectricBlue
                    )
                }
            ) {
                Tab(
                    selected = uiState.selectedTab == 0,
                    onClick = { viewModel.selectTab(0) },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.RssFeed, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Lab Work Feed", fontWeight = FontWeight.Bold)
                        }
                    }
                )
                Tab(
                    selected = uiState.selectedTab == 1,
                    onClick = { viewModel.selectTab(1) },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.History, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("My Work Logs (${uiState.workHistory.size})", fontWeight = FontWeight.Bold)
                        }
                    }
                )
            }

            val listToShow = if (uiState.selectedTab == 0) uiState.labFeed else uiState.workHistory

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (listToShow.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = surfaceColor),
                            border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Engineering,
                                    contentDescription = null,
                                    tint = textSecColor,
                                    modifier = Modifier.size(44.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = if (uiState.selectedTab == 0) "No Lab Updates Posted Yet" else "No Work Logged Yet",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = textColor
                                )
                                Text(
                                    text = "Tap the + button below to log your daily engineering progress.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = textSecColor,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                } else {
                    items(listToShow, key = { it.updateId }) { update ->
                        DailyWorkCard(
                            update = update,
                            isOwn = update.studentUid == currentUid,
                            isDark = isDark,
                            onClick = { viewModel.openDetailModal(update) },
                            onDelete = { viewModel.promptDelete(update) }
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }

        // FAB "+ Log Daily Work"
        FloatingActionButton(
            onClick = viewModel::openCreateDialog,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp),
            containerColor = if (isDark) CyberCyan else ElectricBlue,
            contentColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Log Work")
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Log Daily Work",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }

    // CREATE WORK UPDATE DIALOG
    if (uiState.showCreateDialog) {
        CreateWorkUpdateDialog(
            isLogging = uiState.isLogging,
            errorMessage = uiState.errorMessage,
            onDismiss = viewModel::closeCreateDialog,
            onSubmit = { title, desc, proj, hours, problems, next, date ->
                viewModel.createWorkUpdate(
                    title = title,
                    description = desc,
                    projectName = proj,
                    hoursWorked = hours,
                    problemsFaced = problems,
                    nextSteps = next,
                    workDate = date,
                    imageBytes = null,
                    userProfile = userProfile
                )
            }
        )
    }

    // DETAIL MODAL
    uiState.selectedUpdateForDetail?.let { update ->
        DailyWorkDetailDialog(
            update = update,
            isOwner = update.studentUid == currentUid,
            onDismiss = viewModel::closeDetailModal,
            onDelete = {
                viewModel.closeDetailModal()
                viewModel.promptDelete(update)
            }
        )
    }

    // DELETE CONFIRMATION
    if (uiState.updateToDelete != null) {
        AlertDialog(
            onDismissRequest = viewModel::dismissDelete,
            icon = { Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = CircuitError) },
            title = { Text(text = "Delete Work Log?", fontWeight = FontWeight.Bold) },
            text = { Text(text = "Are you sure you want to delete this work log entry? This action cannot be undone.") },
            confirmButton = {
                RoboticsPrimaryButton(text = "Confirm Delete", onClick = viewModel::confirmDeleteWorkUpdate)
            },
            dismissButton = {
                RoboticsOutlinedButton(text = "Cancel", onClick = viewModel::dismissDelete)
            }
        )
    }
}

@Composable
fun DailyWorkCard(
    update: DailyWorkUpdate,
    isOwn: Boolean,
    isDark: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val surfaceColor = if (isDark) DarkSurface else LightSurface
    val elevatedColor = if (isDark) DarkSurfaceElevated else LightSurfaceElevated
    val borderColor = if (isDark) DarkSurfaceBorder else LightSurfaceBorder
    val textColor = if (isDark) TextPrimaryDark else TextPrimaryLight
    val textSecColor = if (isDark) TextSecondaryDark else TextSecondaryLight

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceColor),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Student info + Hours badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(if (isDark) CyberCyan.copy(alpha = 0.2f) else ElectricBlue.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = update.studentName.take(2).uppercase(),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) CyberCyan else ElectricBlue
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = if (isOwn) "${update.studentName} (You)" else update.studentName,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = textColor
                        )
                        Text(
                            text = "📅 ${update.workDate} • ${update.studentId}",
                            style = MaterialTheme.typography.labelSmall,
                            color = textSecColor
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isDark) CyberCyan.copy(alpha = 0.15f) else ElectricBlue.copy(alpha = 0.12f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${update.hoursWorked} hrs",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) CyberCyan else ElectricBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = update.title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = textColor
            )

            Text(
                text = update.description,
                style = MaterialTheme.typography.bodySmall,
                color = textSecColor,
                maxLines = 2,
                modifier = Modifier.padding(top = 3.dp)
            )

            if (update.relatedProjectName.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(elevatedColor)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Project: ${update.relatedProjectName}",
                        fontSize = 10.sp,
                        color = if (isDark) CyberCyan else ElectricBlue,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
