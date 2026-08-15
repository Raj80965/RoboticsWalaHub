package com.roboticswala.hub.ui.screens.student.projects

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.roboticswala.hub.data.models.Project
import com.roboticswala.hub.data.models.ProjectTeamMember
import com.roboticswala.hub.data.models.ProjectUpdate
import com.roboticswala.hub.data.models.UserProfile
import com.roboticswala.hub.ui.components.RoboticsOutlinedButton
import com.roboticswala.hub.ui.components.RoboticsPrimaryButton
import com.roboticswala.hub.ui.components.RoboticsTextField
import com.roboticswala.hub.ui.theme.CircuitError
import com.roboticswala.hub.ui.theme.CircuitSuccess
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailsScreen(
    projectId: String,
    currentUser: UserProfile,
    onNavigateBack: () -> Unit,
    viewModel: ProjectDetailsViewModel = viewModel(factory = ProjectDetailsViewModelFactory(projectId))
) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val bgColor = if (isDark) DarkBackground else LightBackground
    val surfaceColor = if (isDark) DarkSurface else LightSurface
    val elevatedColor = if (isDark) DarkSurfaceElevated else LightSurfaceElevated
    val borderColor = if (isDark) DarkSurfaceBorder else LightSurfaceBorder
    val textColor = if (isDark) TextPrimaryDark else TextPrimaryLight
    val textSecColor = if (isDark) TextSecondaryDark else TextSecondaryLight

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearMessage()
        }
    }

    LaunchedEffect(uiState.isDeleted) {
        if (uiState.isDeleted) {
            onNavigateBack()
        }
    }

    val project = uiState.project
    val isOwner = project?.isOwner(currentUser.uid) == true
    val isAuthorized = project?.isUserAuthorized(currentUser.uid) == true

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = bgColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = project?.title ?: "Project Details",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = textColor,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = textColor
                        )
                    }
                },
                actions = {
                    if (isOwner && project?.isCompleted == false) {
                        IconButton(onClick = viewModel::openDeleteConfirmDialog) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Project",
                                tint = CircuitError
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = surfaceColor)
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading || project == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = if (isDark) CyberCyan else ElectricBlue)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. HERO HEADER CARD
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isDark) DarkSurfaceElevated else LightSurfaceElevated
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (isDark) CyberCyan.copy(alpha = 0.2f) else ElectricBlue.copy(alpha = 0.15f))
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = project.category.uppercase(),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isDark) CyberCyan else ElectricBlue
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(surfaceColor)
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = project.type,
                                            fontSize = 10.sp,
                                            color = textSecColor
                                        )
                                    }
                                }

                                val statusColor = when (project.status) {
                                    Project.STATUS_COMPLETED -> CircuitSuccess
                                    Project.STATUS_IN_PROGRESS -> if (isDark) CyberCyan else ElectricBlue
                                    Project.STATUS_ON_HOLD -> CircuitWarning
                                    else -> textSecColor
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(statusColor.copy(alpha = 0.15f))
                                        .border(1.dp, statusColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = project.status.uppercase(),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = statusColor
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = project.title,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = textColor
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = project.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = textSecColor,
                                lineHeight = 20.sp
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Owner & Mentor row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(text = "PROJECT OWNER", style = MaterialTheme.typography.labelSmall, color = textSecColor)
                                    Text(text = project.ownerName, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = textColor)
                                    Text(text = "ID: ${project.ownerStudentId}", style = MaterialTheme.typography.labelSmall, color = textSecColor)
                                }

                                if (project.mentorName.isNotBlank()) {
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(text = "FACULTY MENTOR", style = MaterialTheme.typography.labelSmall, color = textSecColor)
                                        Text(text = project.mentorName, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = textColor)
                                    }
                                }
                            }
                        }
                    }
                }

                // 2. PROGRESS GAUGE CARD
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = surfaceColor),
                        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.TrendingUp,
                                        contentDescription = null,
                                        tint = if (isDark) CyberCyan else ElectricBlue,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Project Progress",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = textColor
                                    )
                                }

                                Text(
                                    text = "${project.progressPercentage}%",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = if (project.progressPercentage >= 100) CircuitSuccess else if (isDark) CyberCyan else ElectricBlue
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            LinearProgressIndicator(
                                progress = { (project.progressPercentage / 100f).coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp),
                                color = if (project.progressPercentage >= 100) CircuitSuccess else if (isDark) CyberCyan else ElectricBlue,
                                trackColor = if (isDark) DarkSurfaceBorder else LightSurfaceBorder,
                                strokeCap = StrokeCap.Round
                            )

                            if (isAuthorized) {
                                Spacer(modifier = Modifier.height(14.dp))
                                RoboticsPrimaryButton(
                                    text = "➕ Log Progress Update",
                                    onClick = viewModel::openAddUpdateDialog,
                                    modifier = Modifier.fillMaxWidth(),
                                    height = 42.dp
                                )
                            }
                        }
                    }
                }

                // 3. META DETAILS GRID
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Timeline Card
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = surfaceColor),
                            border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null, tint = textSecColor, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "TIMELINE", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = textSecColor)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Start: ${project.startDate.ifBlank { "N/A" }}", style = MaterialTheme.typography.bodySmall, color = textColor)
                                Text(text = "End: ${project.expectedCompletionDate.ifBlank { "TBD" }}", style = MaterialTheme.typography.bodySmall, color = textColor)
                            }
                        }

                        // Budget Card
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = surfaceColor),
                            border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "₹", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = textSecColor)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "BUDGET & COST", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = textSecColor)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Est: ₹${project.estimatedBudget.toInt()}", style = MaterialTheme.typography.bodySmall, color = textColor)
                                Text(text = "Actual: ₹${project.actualExpense.toInt()}", style = MaterialTheme.typography.bodySmall, color = textSecColor)
                            }
                        }
                    }
                }

                // GitHub Link Button
                if (project.githubLink.isNotBlank()) {
                    item {
                        RoboticsOutlinedButton(
                            text = "🔗 View GitHub Repository",
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(project.githubLink))
                                context.startActivity(intent)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            height = 42.dp
                        )
                    }
                }

                // 4. TEAM MEMBERS
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = surfaceColor),
                        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Group, contentDescription = null, tint = if (isDark) CyberCyan else ElectricBlue, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Team Members (${project.teamMembers.size})", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = textColor)
                            }
                            Spacer(modifier = Modifier.height(10.dp))

                            project.teamMembers.forEach { member ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(elevatedColor)
                                        .padding(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(text = member.studentName, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = textColor)
                                            Text(text = "ID: ${member.studentId}", style = MaterialTheme.typography.labelSmall, color = textSecColor)
                                        }
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(if (member.role == ProjectTeamMember.ROLE_OWNER) CyberCyan.copy(alpha = 0.2f) else surfaceColor)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(text = member.role, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (member.role == ProjectTeamMember.ROLE_OWNER) CyberCyan else textSecColor)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 5. PROGRESS UPDATES TIMELINE
                item {
                    Text(
                        text = "Progress Updates Timeline (${uiState.updates.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = textColor
                    )
                }

                if (uiState.updates.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = surfaceColor),
                            border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(imageVector = Icons.Default.Schedule, contentDescription = null, tint = textSecColor, modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = "No Progress Updates Yet", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = textColor)
                                Text(text = "Team members can log daily sprint updates and blockers.", style = MaterialTheme.typography.bodySmall, color = textSecColor)
                            }
                        }
                    }
                } else {
                    items(uiState.updates, key = { it.updateId }) { update ->
                        ProjectUpdateTimelineCard(update = update, isDark = isDark)
                    }
                }

                item { Spacer(modifier = Modifier.height(40.dp)) }
            }
        }
    }

    // ADD UPDATE DIALOG
    if (uiState.showAddUpdateDialog && project != null) {
        AddProjectUpdateDialog(
            currentProgress = project.progressPercentage,
            isPosting = uiState.isPostingUpdate,
            errorMessage = uiState.errorMessage,
            onDismiss = viewModel::closeAddUpdateDialog,
            onSubmit = { title, desc, progress, problems, nextSteps ->
                viewModel.postProgressUpdate(
                    title = title,
                    workDescription = desc,
                    progressPercentage = progress,
                    problemsFaced = problems,
                    nextSteps = nextSteps,
                    imageBytes = null,
                    userProfile = currentUser
                )
            }
        )
    }

    // DELETE CONFIRMATION DIALOG
    if (uiState.showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = viewModel::closeDeleteConfirmDialog,
            icon = {
                Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = CircuitError, modifier = Modifier.size(36.dp))
            },
            title = {
                Text(text = "Delete Project?", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            },
            text = {
                Text(text = "Are you sure you want to delete '${project?.title}'? This action cannot be undone and all team updates will be removed.")
            },
            confirmButton = {
                RoboticsPrimaryButton(
                    text = "Confirm Delete",
                    onClick = viewModel::deleteProject,
                    isLoading = uiState.isDeleting
                )
            },
            dismissButton = {
                RoboticsOutlinedButton(text = "Cancel", onClick = viewModel::closeDeleteConfirmDialog)
            }
        )
    }
}

@Composable
fun ProjectUpdateTimelineCard(update: ProjectUpdate, isDark: Boolean) {
    val surfaceColor = if (isDark) DarkSurface else LightSurface
    val elevatedColor = if (isDark) DarkSurfaceElevated else LightSurfaceElevated
    val borderColor = if (isDark) DarkSurfaceBorder else LightSurfaceBorder
    val textColor = if (isDark) TextPrimaryDark else TextPrimaryLight
    val textSecColor = if (isDark) TextSecondaryDark else TextSecondaryLight

    val dateFormat = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceColor),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = update.title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = textColor)
                    Text(text = "By ${update.createdByName} • ${dateFormat.format(Date(update.createdAt))}", style = MaterialTheme.typography.labelSmall, color = textSecColor)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isDark) CyberCyan.copy(alpha = 0.2f) else ElectricBlue.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(text = "${update.progressPercentage}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isDark) CyberCyan else ElectricBlue)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = update.workDescription, style = MaterialTheme.typography.bodyMedium, color = textColor)

            if (update.problemsFaced.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(CircuitError.copy(alpha = 0.1f))
                        .padding(8.dp)
                ) {
                    Text(text = "⚠️ Blockers: ${update.problemsFaced}", style = MaterialTheme.typography.bodySmall, color = CircuitError)
                }
            }

            if (update.nextSteps.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(elevatedColor)
                        .padding(8.dp)
                ) {
                    Text(text = "➔ Next Steps: ${update.nextSteps}", style = MaterialTheme.typography.bodySmall, color = if (isDark) CyberCyan else ElectricBlue)
                }
            }
        }
    }
}

@Composable
fun AddProjectUpdateDialog(
    currentProgress: Int,
    isPosting: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onSubmit: (title: String, desc: String, progress: Int, problems: String, nextSteps: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var progress by remember { mutableStateOf(currentProgress) }
    var problems by remember { mutableStateOf("") }
    var nextSteps by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Log Progress Update", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (!errorMessage.isNullOrBlank()) {
                    Text(text = "⚠️ $errorMessage", color = CircuitError, fontSize = 12.sp)
                }

                RoboticsTextField(value = title, onValueChange = { title = it }, label = "Update Title *", placeholder = "e.g. Completed SLAM LiDAR Integration")
                RoboticsTextField(value = description, onValueChange = { description = it }, label = "Work Description *", placeholder = "What was implemented or tested today?", singleLine = false)

                Text(text = "Updated Progress: $progress%", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                Slider(
                    value = progress.toFloat(),
                    onValueChange = { progress = it.toInt() },
                    valueRange = 0f..100f
                )

                RoboticsTextField(value = problems, onValueChange = { problems = it }, label = "Problems / Blockers (Optional)", placeholder = "e.g. Motor driver overheating")
                RoboticsTextField(value = nextSteps, onValueChange = { nextSteps = it }, label = "Next Steps (Optional)", placeholder = "e.g. Tune PID parameters in ROS2")
            }
        },
        confirmButton = {
            RoboticsPrimaryButton(
                text = if (isPosting) "Posting..." else "Publish Update",
                onClick = { onSubmit(title, description, progress, problems, nextSteps) },
                isLoading = isPosting,
                enabled = title.isNotBlank() && description.isNotBlank()
            )
        },
        dismissButton = {
            RoboticsOutlinedButton(text = "Cancel", onClick = onDismiss)
        }
    )
}
