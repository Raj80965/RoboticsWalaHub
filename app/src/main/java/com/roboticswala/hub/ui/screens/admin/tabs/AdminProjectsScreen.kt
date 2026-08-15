package com.roboticswala.hub.ui.screens.admin.tabs

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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.roboticswala.hub.data.models.Project
import com.roboticswala.hub.ui.components.RoboticsOutlinedButton
import com.roboticswala.hub.ui.components.RoboticsPrimaryButton
import com.roboticswala.hub.ui.components.RoboticsTextField
import com.roboticswala.hub.ui.screens.admin.AdminProjectsViewModel
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

@Composable
fun AdminProjectsScreen(
    viewModel: AdminProjectsViewModel = viewModel(),
    onNavigateToDetails: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val surfaceColor = if (isDark) DarkSurface else LightSurface
    val elevatedColor = if (isDark) DarkSurfaceElevated else LightSurfaceElevated
    val borderColor = if (isDark) DarkSurfaceBorder else LightSurfaceBorder
    val textColor = if (isDark) TextPrimaryDark else TextPrimaryLight
    val textSecColor = if (isDark) TextSecondaryDark else TextSecondaryLight

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. HEADER
        item {
            Column {
                Text(
                    text = "Project Registry & Monitoring",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = textColor
                )
                Text(
                    text = "Review all lab robotics projects, assign mentors, and audit budgets",
                    style = MaterialTheme.typography.bodySmall,
                    color = textSecColor
                )
            }
        }

        // 2. SEARCH
        item {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::setSearchQuery,
                placeholder = { Text("Search by Project Title, Owner, ID, or Mentor...", fontSize = 13.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = textSecColor,
                        modifier = Modifier.size(18.dp)
                    )
                },
                trailingIcon = {
                    if (uiState.searchQuery.isNotBlank()) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = textSecColor,
                            modifier = Modifier
                                .size(18.dp)
                                .clickable { viewModel.setSearchQuery("") }
                        )
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

        // 3. CATEGORY FILTER
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val categories = listOf("All") + Project.ALL_CATEGORIES
                items(categories) { cat ->
                    val isSelected = uiState.categoryFilter == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setCategoryFilter(cat) },
                        label = { Text(cat, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = if (isDark) CyberCyan else ElectricBlue,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // 4. STATUS FILTER
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val statuses = listOf("All") + Project.ALL_STATUSES
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

        // 5. PROJECTS COUNT
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Projects (${uiState.filteredProjects.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = textColor
                )
            }
        }

        // 6. LIST
        if (uiState.filteredProjects.isEmpty()) {
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
                        Icon(imageVector = Icons.Default.FolderOpen, contentDescription = null, tint = textSecColor, modifier = Modifier.size(44.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "No Matching Projects", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = textColor)
                        Text(text = "Try adjusting your search query or status filter.", style = MaterialTheme.typography.bodySmall, color = textSecColor)
                    }
                }
            }
        } else {
            items(uiState.filteredProjects, key = { it.projectId }) { project ->
                AdminProjectCard(
                    project = project,
                    isDark = isDark,
                    onEditMentor = { viewModel.promptEditMentor(project) },
                    onClick = { onNavigateToDetails(project.projectId) }
                )
            }
        }

        item { Spacer(modifier = Modifier.height(40.dp)) }
    }

    // EDIT MENTOR & STATUS MODAL
    uiState.projectToEditMentor?.let { project ->
        AdminEditProjectDialog(
            project = project,
            isUpdating = uiState.isUpdating,
            onDismiss = viewModel::dismissEditMentor,
            onConfirm = { mentor, status ->
                viewModel.updateMentorAndStatus(project.projectId, mentor, status)
            }
        )
    }
}

@Composable
fun AdminProjectCard(
    project: Project,
    isDark: Boolean,
    onEditMentor: () -> Unit,
    onClick: () -> Unit
) {
    val surfaceColor = if (isDark) DarkSurface else LightSurface
    val elevatedColor = if (isDark) DarkSurfaceElevated else LightSurfaceElevated
    val borderColor = if (isDark) DarkSurfaceBorder else LightSurfaceBorder
    val textColor = if (isDark) TextPrimaryDark else TextPrimaryLight
    val textSecColor = if (isDark) TextSecondaryDark else TextSecondaryLight

    val statusColor = when (project.status) {
        Project.STATUS_COMPLETED -> CircuitSuccess
        Project.STATUS_IN_PROGRESS -> if (isDark) CyberCyan else ElectricBlue
        Project.STATUS_ON_HOLD -> CircuitWarning
        else -> textSecColor
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = project.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = textColor
                    )
                    Text(
                        text = "Owner: ${project.ownerName} (${project.ownerStudentId})",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isDark) CyberCyan else ElectricBlue
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(statusColor.copy(alpha = 0.15f))
                        .border(1.dp, statusColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
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

            // Mentor & Category
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Category: ${project.category} • ${project.type}",
                    style = MaterialTheme.typography.labelSmall,
                    color = textSecColor
                )

                Text(
                    text = if (project.mentorName.isNotBlank()) "Mentor: ${project.mentorName}" else "No Mentor Assigned",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = if (project.mentorName.isNotBlank()) textColor else CircuitWarning
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Progress: ${project.progressPercentage}%", style = MaterialTheme.typography.labelSmall, color = textSecColor)
                Text(text = "Budget: ₹${project.estimatedBudget.toInt()} (Exp: ₹${project.actualExpense.toInt()})", style = MaterialTheme.typography.labelSmall, color = textSecColor)
            }

            Spacer(modifier = Modifier.height(4.dp))

            LinearProgressIndicator(
                progress = { (project.progressPercentage / 100f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = if (project.progressPercentage >= 100) CircuitSuccess else if (isDark) CyberCyan else ElectricBlue,
                trackColor = if (isDark) DarkSurfaceBorder else LightSurfaceBorder,
                strokeCap = StrokeCap.Round
            )

            Spacer(modifier = Modifier.height(12.dp))

            RoboticsOutlinedButton(
                text = "⚙️ Manage Mentor & Status",
                onClick = onEditMentor,
                modifier = Modifier.fillMaxWidth(),
                height = 36.dp
            )
        }
    }
}

@Composable
fun AdminEditProjectDialog(
    project: Project,
    isUpdating: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var mentor by remember { mutableStateOf(project.mentorName) }
    var status by remember { mutableStateOf(project.status) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Manage Project Admin Controls", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = "Project: ${project.title}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)

                RoboticsTextField(
                    value = mentor,
                    onValueChange = { mentor = it },
                    label = "Faculty Mentor Name",
                    placeholder = "e.g. Dr. Rajesh Kumar"
                )

                Text(text = "Update Status:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Project.ALL_STATUSES.forEach { stat ->
                        val isSelected = status == stat
                        FilterChip(
                            selected = isSelected,
                            onClick = { status = stat },
                            label = { Text(stat, fontSize = 11.sp) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            RoboticsPrimaryButton(
                text = if (isUpdating) "Saving..." else "Save Changes",
                onClick = { onConfirm(mentor, status) },
                isLoading = isUpdating
            )
        },
        dismissButton = {
            RoboticsOutlinedButton(text = "Cancel", onClick = onDismiss)
        }
    )
}
