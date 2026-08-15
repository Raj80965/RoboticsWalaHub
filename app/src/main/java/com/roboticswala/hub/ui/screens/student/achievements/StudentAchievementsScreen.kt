package com.roboticswala.hub.ui.screens.student.achievements

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.remember
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
import com.roboticswala.hub.data.models.Achievement
import com.roboticswala.hub.data.models.UserProfile
import com.roboticswala.hub.ui.components.RoboticsOutlinedButton
import com.roboticswala.hub.ui.components.RoboticsPrimaryButton
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
fun StudentAchievementsScreen(
    userProfile: UserProfile,
    onNavigateBack: () -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateToEdit: (Achievement) -> Unit = {},
    viewModel: StudentAchievementsViewModel = viewModel(factory = StudentAchievementsViewModelFactory(userProfile.uid))
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
                            text = "My Achievements & Certs",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = textColor
                        )
                        Text(
                            text = "Verified awards, hackathons & research certifications",
                            style = MaterialTheme.typography.labelSmall,
                            color = textSecColor
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = textColor)
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::toggleSortOrder) {
                        Icon(imageVector = Icons.Default.Sort, contentDescription = "Sort", tint = if (isDark) CyberCyan else ElectricBlue)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = surfaceColor)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Search bar
                item {
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = viewModel::setSearchQuery,
                        placeholder = { Text("Search by title, organization...", fontSize = 13.sp) },
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
                        val statuses = listOf("All") + Achievement.ALL_STATUSES
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

                // Category Filter Chips
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                        val categories = listOf("All") + Achievement.ALL_CATEGORIES
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

                // Count Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Achievements (${uiState.filteredAchievements.size})",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = textColor
                        )
                        if (uiState.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        }
                    }
                }

                // Achievements List
                if (uiState.filteredAchievements.isEmpty() && !uiState.isLoading) {
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
                                Icon(imageVector = Icons.Default.EmojiEvents, contentDescription = null, tint = textSecColor, modifier = Modifier.size(44.dp))
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(text = "No Achievements Found", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = textColor)
                                Text(text = "Tap '+ Add Achievement' to record competitions and certificates.", style = MaterialTheme.typography.bodySmall, color = textSecColor)
                            }
                        }
                    }
                } else {
                    items(uiState.filteredAchievements, key = { it.achievementId }) { item ->
                        StudentAchievementCard(
                            achievement = item,
                            isDark = isDark,
                            onClick = { viewModel.openDetails(item) },
                            onEdit = { onNavigateToEdit(item) },
                            onDelete = { viewModel.promptDelete(item) }
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }

            // FAB "+ Add"
            FloatingActionButton(
                onClick = onNavigateToCreate,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp),
                containerColor = if (isDark) CyberCyan else ElectricBlue,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Add Achievement", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                }
            }
        }
    }

    // DETAILS DIALOG
    uiState.selectedAchievementForDetails?.let { item ->
        AchievementDetailDialog(
            achievement = item,
            onDismiss = viewModel::closeDetails,
            onEdit = {
                viewModel.closeDetails()
                onNavigateToEdit(item)
            }
        )
    }

    // DELETE CONFIRMATION
    uiState.achievementToDelete?.let { item ->
        AlertDialog(
            onDismissRequest = viewModel::dismissDelete,
            icon = { Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = CircuitError) },
            title = { Text(text = "Delete Achievement?", fontWeight = FontWeight.Bold) },
            text = { Text(text = "Are you sure you want to delete '${item.title}'?") },
            confirmButton = {
                RoboticsPrimaryButton(text = "Confirm Delete", onClick = viewModel::confirmDelete)
            },
            dismissButton = {
                RoboticsOutlinedButton(text = "Cancel", onClick = viewModel::dismissDelete)
            }
        )
    }
}

@Composable
fun StudentAchievementCard(
    achievement: Achievement,
    isDark: Boolean,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val surfaceColor = if (isDark) DarkSurface else LightSurface
    val elevatedColor = if (isDark) DarkSurfaceElevated else LightSurfaceElevated
    val borderColor = if (isDark) DarkSurfaceBorder else LightSurfaceBorder
    val textColor = if (isDark) TextPrimaryDark else TextPrimaryLight
    val textSecColor = if (isDark) TextSecondaryDark else TextSecondaryLight

    val statusColor = when (achievement.status) {
        Achievement.STATUS_APPROVED -> CircuitSuccess
        Achievement.STATUS_REJECTED -> CircuitError
        Achievement.STATUS_NEEDS_CORRECTION -> if (isDark) CyberCyan else ElectricBlue
        else -> CircuitWarning
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceColor),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Badges row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isDark) CyberCyan.copy(alpha = 0.15f) else ElectricBlue.copy(alpha = 0.12f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(text = achievement.category.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isDark) CyberCyan else ElectricBlue)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(elevatedColor)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(text = achievement.achievementLevel, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = textSecColor)
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(statusColor.copy(alpha = 0.15f))
                        .border(1.dp, statusColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(text = achievement.status.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = statusColor)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(text = achievement.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = textColor)
            Text(text = "🏛️ ${achievement.organizationName} • 📅 ${achievement.achievementDate}", style = MaterialTheme.typography.labelSmall, color = textSecColor, modifier = Modifier.padding(top = 2.dp))

            if (achievement.description.isNotBlank()) {
                Text(text = achievement.description, style = MaterialTheme.typography.bodySmall, color = textSecColor, maxLines = 2, modifier = Modifier.padding(top = 4.dp))
            }

            // Correction Message Banner
            if (achievement.needsCorrection && achievement.correctionMessage.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isDark) CyberCyan.copy(alpha = 0.12f) else ElectricBlue.copy(alpha = 0.1f))
                        .padding(8.dp)
                ) {
                    Text(text = "✏️ Admin Note: ${achievement.correctionMessage}", style = MaterialTheme.typography.bodySmall, color = if (isDark) CyberCyan else ElectricBlue)
                }
            }

            // Rejection Banner
            if (achievement.isRejected && achievement.rejectionReason.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(CircuitError.copy(alpha = 0.12f))
                        .padding(8.dp)
                ) {
                    Text(text = "❌ Rejection Reason: ${achievement.rejectionReason}", style = MaterialTheme.typography.bodySmall, color = CircuitError)
                }
            }

            // Action Buttons
            if (achievement.canStudentEdit || achievement.canStudentDelete) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (achievement.canStudentEdit) {
                        RoboticsOutlinedButton(
                            text = if (achievement.needsCorrection) "✏️ Fix & Resubmit" else "Edit",
                            onClick = onEdit,
                            modifier = Modifier.weight(1f),
                            height = 32.dp
                        )
                    }
                    if (achievement.canStudentDelete) {
                        RoboticsOutlinedButton(
                            text = "Delete",
                            onClick = onDelete,
                            modifier = Modifier.weight(1f),
                            height = 32.dp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AchievementDetailDialog(
    achievement: Achievement,
    onDismiss: () -> Unit,
    onEdit: () -> Unit
) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()
    val textColor = if (isDark) TextPrimaryDark else TextPrimaryLight
    val textSecColor = if (isDark) TextSecondaryDark else TextSecondaryLight

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = achievement.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Category: ${achievement.category} • Level: ${achievement.achievementLevel}", style = MaterialTheme.typography.labelSmall, color = textSecColor)
                Text(text = "Issuing Org: ${achievement.organizationName}", style = MaterialTheme.typography.labelSmall, color = textSecColor)
                Text(text = "Date: ${achievement.achievementDate}", style = MaterialTheme.typography.labelSmall, color = textSecColor)

                if (achievement.description.isNotBlank()) {
                    Text(text = achievement.description, style = MaterialTheme.typography.bodyMedium, color = textColor)
                }

                if (achievement.verificationLink.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isDark) CyberCyan.copy(alpha = 0.12f) else ElectricBlue.copy(alpha = 0.1f))
                            .clickable {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(achievement.verificationLink))
                                    context.startActivity(intent)
                                } catch (_: Exception) {}
                            }
                            .padding(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Open Verification Link", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                if (achievement.certificateFileName.isNotBlank()) {
                    Text(text = "Attached: 📄 ${achievement.certificateFileName}", style = MaterialTheme.typography.labelSmall, color = textSecColor)
                }
            }
        },
        confirmButton = {
            RoboticsPrimaryButton(text = "Close", onClick = onDismiss)
        },
        dismissButton = {
            if (achievement.canStudentEdit) {
                RoboticsOutlinedButton(text = "Edit", onClick = onEdit)
            }
        }
    )
}
