package com.roboticswala.hub.ui.screens.student.tasks

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.roboticswala.hub.data.models.LabTask
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentTasksScreen(
    studentProfile: UserProfile,
    onNavigateBack: () -> Unit,
    viewModel: StudentTasksViewModel = viewModel(factory = StudentTasksViewModelFactory(studentProfile.uid))
) {
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

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = bgColor,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "My Assigned Tasks",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = textColor
                        )
                        Text(
                            text = "Weekly milestones & deliverables assigned by lab mentor",
                            style = MaterialTheme.typography.labelSmall,
                            color = textSecColor
                        )
                    }
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = surfaceColor)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Search
            item {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = viewModel::setSearchQuery,
                    placeholder = { Text("Search task title, project...", fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = textSecColor, modifier = Modifier.size(18.dp))
                    },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotBlank()) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", tint = textSecColor, modifier = Modifier.size(18.dp).clickable { viewModel.setSearchQuery("") })
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (isDark) CyberCyan else ElectricBlue,
                        unfocusedBorderColor = borderColor,
                        focusedContainerColor = elevatedColor,
                        unfocusedContainerColor = elevatedColor
                    ),
                    singleLine = true
                )
            }

            // Status Filter Chips
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                    val statuses = listOf("All") + LabTask.ALL_STATUSES
                    items(statuses) { stat ->
                        val isSelected = uiState.statusFilter == stat
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setStatusFilter(stat) },
                            label = { Text(stat, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = if (isDark) CyberCyan.copy(alpha = 0.25f) else ElectricBlue.copy(alpha = 0.2f),
                                selectedLabelColor = if (isDark) CyberCyan else ElectricBlue
                            )
                        )
                    }
                }
            }

            // Count
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Tasks (${uiState.filteredTasks.size})", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = textColor)
                    if (uiState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    }
                }
            }

            // Tasks List
            if (uiState.filteredTasks.isEmpty() && !uiState.isLoading) {
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
                            Icon(imageVector = Icons.Default.Assignment, contentDescription = null, tint = textSecColor, modifier = Modifier.size(44.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(text = "No Tasks Found", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = textColor)
                            Text(text = "You're all caught up with your lab milestones.", style = MaterialTheme.typography.bodySmall, color = textSecColor)
                        }
                    }
                }
            } else {
                items(uiState.filteredTasks, key = { it.taskId }) { task ->
                    StudentTaskCard(
                        task = task,
                        isDark = isDark,
                        onStart = { viewModel.startTask(task.taskId) },
                        onSubmit = { viewModel.openSubmissionModal(task) }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(40.dp)) }
        }
    }

    // SUBMIT TASK MODAL
    uiState.selectedTaskForSubmission?.let { task ->
        SubmitTaskDialog(
            task = task,
            isSubmitting = uiState.isSubmitting,
            onDismiss = viewModel::closeSubmissionModal,
            onSubmit = { note ->
                viewModel.submitTaskWork(task.taskId, note, null)
            }
        )
    }
}

@Composable
fun StudentTaskCard(
    task: LabTask,
    isDark: Boolean,
    onStart: () -> Unit,
    onSubmit: () -> Unit
) {
    val surfaceColor = if (isDark) DarkSurface else LightSurface
    val elevatedColor = if (isDark) DarkSurfaceElevated else LightSurfaceElevated
    val borderColor = if (isDark) DarkSurfaceBorder else LightSurfaceBorder
    val textColor = if (isDark) TextPrimaryDark else TextPrimaryLight
    val textSecColor = if (isDark) TextSecondaryDark else TextSecondaryLight

    val priorityColor = when (task.priority) {
        LabTask.PRIORITY_URGENT -> CircuitError
        LabTask.PRIORITY_HIGH -> CircuitWarning
        LabTask.PRIORITY_MEDIUM -> if (isDark) CyberCyan else ElectricBlue
        else -> textSecColor
    }

    val statusColor = when (task.status) {
        LabTask.STATUS_COMPLETED -> CircuitSuccess
        LabTask.STATUS_SUBMITTED -> if (isDark) CyberCyan else ElectricBlue
        LabTask.STATUS_IN_PROGRESS -> CircuitWarning
        else -> textSecColor
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceColor),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Priority & Status header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(priorityColor.copy(alpha = 0.15f))
                        .border(1.dp, priorityColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(text = "${task.priority} Priority".uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = priorityColor)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(statusColor.copy(alpha = 0.15f))
                        .border(1.dp, statusColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(text = task.status.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = statusColor)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(text = task.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = textColor)
            Text(text = task.description, style = MaterialTheme.typography.bodySmall, color = textSecColor, modifier = Modifier.padding(top = 4.dp))

            Spacer(modifier = Modifier.height(10.dp))

            // Deadline & Project
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "📅 Deadline: ${task.deadline}", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = if (task.priority == LabTask.PRIORITY_URGENT) CircuitError else textColor)
                if (task.relatedProjectName.isNotBlank()) {
                    Text(text = "Project: ${task.relatedProjectName}", style = MaterialTheme.typography.labelSmall, color = textSecColor)
                }
            }

            if (task.submissionNote.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(elevatedColor)
                        .padding(8.dp)
                ) {
                    Text(text = "📝 Submission Note: ${task.submissionNote}", style = MaterialTheme.typography.bodySmall, color = textColor)
                }
            }

            // Action Buttons
            Spacer(modifier = Modifier.height(12.dp))
            when (task.status) {
                LabTask.STATUS_PENDING -> {
                    RoboticsPrimaryButton(
                        text = "▶️ Start Task (In Progress)",
                        onClick = onStart,
                        modifier = Modifier.fillMaxWidth(),
                        height = 38.dp
                    )
                }
                LabTask.STATUS_IN_PROGRESS -> {
                    RoboticsPrimaryButton(
                        text = "📤 Submit Completed Work",
                        onClick = onSubmit,
                        modifier = Modifier.fillMaxWidth(),
                        height = 38.dp
                    )
                }
                LabTask.STATUS_SUBMITTED -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isDark) CyberCyan.copy(alpha = 0.15f) else ElectricBlue.copy(alpha = 0.1f))
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "⏳ Submitted for Admin Review", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = if (isDark) CyberCyan else ElectricBlue)
                    }
                }
                LabTask.STATUS_COMPLETED -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(CircuitSuccess.copy(alpha = 0.15f))
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "✓ Completed & Verified", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = CircuitSuccess)
                    }
                }
            }
        }
    }
}

@Composable
fun SubmitTaskDialog(
    task: LabTask,
    isSubmitting: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    var note by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Submit Completed Task Work", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Task: ${task.title}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)

                RoboticsTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = "Submission Notes / Test Results *",
                    placeholder = "Describe what was implemented & link any test logs...",
                    singleLine = false
                )
            }
        },
        confirmButton = {
            RoboticsPrimaryButton(
                text = if (isSubmitting) "Submitting..." else "Submit for Review",
                onClick = { onSubmit(note) },
                isLoading = isSubmitting,
                enabled = note.isNotBlank()
            )
        },
        dismissButton = {
            RoboticsOutlinedButton(text = "Cancel", onClick = onDismiss)
        }
    )
}
