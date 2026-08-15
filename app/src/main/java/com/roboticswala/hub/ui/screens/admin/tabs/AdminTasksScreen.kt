package com.roboticswala.hub.ui.screens.admin.tabs

import android.app.DatePickerDialog
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.roboticswala.hub.ui.screens.admin.tasks.AdminTasksViewModel
import com.roboticswala.hub.ui.theme.CircuitError
import com.roboticswala.hub.ui.theme.CircuitSuccess
import com.roboticswala.hub.ui.theme.CircuitWarning
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
import com.roboticswala.hub.utils.BookingTimeUtils
import java.util.Calendar
import java.util.Locale

@Composable
fun AdminTasksScreen(
    viewModel: AdminTasksViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val surfaceColor = if (isDark) DarkSurface else LightSurface
    val elevatedColor = if (isDark) DarkSurfaceElevated else LightSurfaceElevated
    val borderColor = if (isDark) DarkSurfaceBorder else LightSurfaceBorder
    val textColor = if (isDark) TextPrimaryDark else TextPrimaryLight
    val textSecColor = if (isDark) TextSecondaryDark else TextSecondaryLight

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            item {
                Column {
                    Text(
                        text = "Task & Milestone Management",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = textColor
                    )
                    Text(
                        text = "Assign weekly tasks to approved students & review completed deliverables",
                        style = MaterialTheme.typography.bodySmall,
                        color = textSecColor
                    )
                }
            }

            // Search
            item {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = viewModel::setSearchQuery,
                    placeholder = { Text("Search by Task Title, Student Name, Project...", fontSize = 13.sp) },
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

            // Priority Filter Chips
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                    val priorities = listOf("All") + LabTask.ALL_PRIORITIES
                    items(priorities) { prio ->
                        val isSelected = uiState.priorityFilter == prio
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setPriorityFilter(prio) },
                            label = { Text(prio, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = if (isDark) CyberCyan else ElectricBlue,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // Count
            item {
                Text(
                    text = "Tasks (${uiState.filteredTasks.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = textColor
                )
            }

            // Task List
            if (uiState.filteredTasks.isEmpty()) {
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
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "No Tasks Found", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = textColor)
                            Text(text = "Tap '+ Assign Task' to create milestone assignments.", style = MaterialTheme.typography.bodySmall, color = textSecColor)
                        }
                    }
                }
            } else {
                items(uiState.filteredTasks, key = { it.taskId }) { task ->
                    AdminTaskCard(
                        task = task,
                        isDark = isDark,
                        onReview = { viewModel.openReviewModal(task) },
                        onDelete = { viewModel.promptDeleteTask(task) }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }

        // FAB "+ Assign Task"
        FloatingActionButton(
            onClick = viewModel::openCreateTaskDialog,
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
                Icon(imageVector = Icons.Default.Add, contentDescription = "Assign Task")
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Assign Task",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }

    // CREATE TASK DIALOG
    if (uiState.showCreateTaskDialog) {
        AdminCreateTaskDialog(
            availableStudents = uiState.availableStudents,
            isLoading = uiState.isActionLoading,
            errorMessage = uiState.errorMessage,
            onDismiss = viewModel::closeCreateTaskDialog,
            onSubmit = { title, desc, student, proj, deadline, prio ->
                viewModel.createAndAssignTask(title, desc, student, proj, deadline, prio)
            }
        )
    }

    // REVIEW SUBMISSION DIALOG
    uiState.selectedTaskForReview?.let { task ->
        AdminReviewSubmissionDialog(
            task = task,
            isLoading = uiState.isActionLoading,
            onDismiss = viewModel::closeReviewModal,
            onAction = { markCompleted, feedback ->
                viewModel.reviewTask(task.taskId, markCompleted, feedback)
            }
        )
    }

    // DELETE TASK CONFIRMATION
    uiState.taskToDelete?.let { task ->
        AlertDialog(
            onDismissRequest = viewModel::dismissDeleteTask,
            icon = { Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = CircuitError) },
            title = { Text(text = "Delete Task?", fontWeight = FontWeight.Bold) },
            text = { Text(text = "Are you sure you want to delete task '${task.title}' assigned to ${task.assignedStudentName}?") },
            confirmButton = {
                RoboticsPrimaryButton(text = "Confirm Delete", onClick = viewModel::confirmDeleteTask)
            },
            dismissButton = {
                RoboticsOutlinedButton(text = "Cancel", onClick = viewModel::dismissDeleteTask)
            }
        )
    }
}

@Composable
fun AdminTaskCard(
    task: LabTask,
    isDark: Boolean,
    onReview: () -> Unit,
    onDelete: () -> Unit
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

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(statusColor.copy(alpha = 0.15f))
                            .border(1.dp, statusColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(text = task.status.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = statusColor)
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(24.dp).padding(start = 6.dp)) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = CircuitError, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(text = task.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = textColor)
            Text(text = "Assigned To: ${task.assignedStudentName} (${task.assignedStudentId})", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = if (isDark) CyberCyan else ElectricBlue)
            Text(text = task.description, style = MaterialTheme.typography.bodySmall, color = textSecColor, modifier = Modifier.padding(top = 4.dp))

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "📅 Deadline: ${task.deadline}", style = MaterialTheme.typography.labelSmall, color = textColor)
                if (task.relatedProjectName.isNotBlank()) {
                    Text(text = "Project: ${task.relatedProjectName}", style = MaterialTheme.typography.labelSmall, color = textSecColor)
                }
            }

            if (task.status == LabTask.STATUS_SUBMITTED) {
                Spacer(modifier = Modifier.height(12.dp))
                RoboticsPrimaryButton(
                    text = "🔍 Review Student Submission",
                    onClick = onReview,
                    modifier = Modifier.fillMaxWidth(),
                    height = 36.dp
                )
            }
        }
    }
}

@Composable
fun AdminCreateTaskDialog(
    availableStudents: List<UserProfile>,
    isLoading: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onSubmit: (title: String, desc: String, student: UserProfile, proj: String, deadline: String, prio: String) -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedStudent by remember { mutableStateOf(availableStudents.firstOrNull()) }
    var projectName by remember { mutableStateOf("") }
    var deadline by remember { mutableStateOf(BookingTimeUtils.getTodayDateString()) }
    var priority by remember { mutableStateOf(LabTask.PRIORITY_MEDIUM) }

    val cal = remember { Calendar.getInstance() }
    val datePicker = remember {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                val formatted = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, day)
                deadline = formatted
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Assign New Weekly Milestone Task", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (!errorMessage.isNullOrBlank()) {
                    Text(text = "⚠️ $errorMessage", color = CircuitError, fontSize = 12.sp)
                }

                RoboticsTextField(value = title, onValueChange = { title = it }, label = "Task Title *", placeholder = "e.g. Solder motor driver shield")
                RoboticsTextField(value = description, onValueChange = { description = it }, label = "Task Instructions *", placeholder = "Deliverable specifications & safety notes", singleLine = false)

                Text(text = "Assign to Student *:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                if (availableStudents.isEmpty()) {
                    Text(text = "No approved students available.", color = CircuitError, fontSize = 12.sp)
                } else {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(availableStudents) { student ->
                            val isSelected = selectedStudent?.uid == student.uid
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedStudent = student },
                                label = { Text(student.fullName, fontSize = 11.sp) }
                            )
                        }
                    }
                }

                Text(text = "Task Priority:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    LabTask.ALL_PRIORITIES.forEach { p ->
                        FilterChip(
                            selected = priority == p,
                            onClick = { priority = p },
                            label = { Text(p, fontSize = 11.sp) }
                        )
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Deadline: $deadline", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    RoboticsOutlinedButton(text = "📅 Pick Date", onClick = { datePicker.show() }, height = 32.dp)
                }

                RoboticsTextField(value = projectName, onValueChange = { projectName = it }, label = "Related Project (Optional)", placeholder = "e.g. SLAM Rover")
            }
        },
        confirmButton = {
            RoboticsPrimaryButton(
                text = if (isLoading) "Assigning..." else "Assign Task",
                onClick = {
                    selectedStudent?.let { onSubmit(title, description, it, projectName, deadline, priority) }
                },
                isLoading = isLoading,
                enabled = title.isNotBlank() && description.isNotBlank() && selectedStudent != null
            )
        },
        dismissButton = {
            RoboticsOutlinedButton(text = "Cancel", onClick = onDismiss)
        }
    )
}

@Composable
fun AdminReviewSubmissionDialog(
    task: LabTask,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onAction: (markCompleted: Boolean, feedback: String?) -> Unit
) {
    var feedbackNote by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Review Student Submission", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Task: ${task.title}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                Text(text = "Student: ${task.assignedStudentName} (${task.assignedStudentId})", style = MaterialTheme.typography.labelSmall)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Gray.copy(alpha = 0.15f))
                        .padding(8.dp)
                ) {
                    Text(text = "📝 Student Notes:\n${task.submissionNote.ifBlank { "No notes provided." }}", style = MaterialTheme.typography.bodySmall)
                }

                RoboticsTextField(
                    value = feedbackNote,
                    onValueChange = { feedbackNote = it },
                    label = "Admin Review Feedback / Changes Required",
                    placeholder = "Feedback for student..."
                )
            }
        },
        confirmButton = {
            RoboticsPrimaryButton(
                text = "✓ Mark Completed",
                onClick = { onAction(true, feedbackNote) },
                isLoading = isLoading
            )
        },
        dismissButton = {
            RoboticsOutlinedButton(
                text = "↩ Return for Changes",
                onClick = { onAction(false, feedbackNote) }
            )
        }
    )
}
