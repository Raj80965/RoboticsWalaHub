package com.roboticswala.hub.ui.screens.admin.tabs

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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
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
import com.roboticswala.hub.data.models.Achievement
import com.roboticswala.hub.ui.components.RoboticsOutlinedButton
import com.roboticswala.hub.ui.components.RoboticsPrimaryButton
import com.roboticswala.hub.ui.components.RoboticsTextField
import com.roboticswala.hub.ui.screens.admin.achievements.AdminAchievementsViewModel
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
fun AdminAchievementsScreen(
    viewModel: AdminAchievementsViewModel = viewModel(),
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
            item {
                Column {
                    Text(
                        text = "Achievement & Certificate Approvals",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = textColor
                    )
                    Text(
                        text = "Review, verify certificates, approve and publish to student profiles",
                        style = MaterialTheme.typography.bodySmall,
                        color = textSecColor
                    )
                }
            }

            // Search bar
            item {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = viewModel::setSearchQuery,
                    placeholder = { Text("Search student name, ID, title, organization...", fontSize = 13.sp) },
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
                Text(
                    text = "Submissions (${uiState.filteredAchievements.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = textColor
                )
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
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "No Submissions Found", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = textColor)
                            Text(text = "All student achievements are currently processed.", style = MaterialTheme.typography.bodySmall, color = textSecColor)
                        }
                    }
                }
            } else {
                items(uiState.filteredAchievements, key = { it.achievementId }) { item ->
                    AdminAchievementCard(
                        achievement = item,
                        isDark = isDark,
                        onReview = { viewModel.openReviewModal(item) }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(40.dp)) }
        }
    }

    // REVIEW MODAL
    uiState.selectedAchievementForReview?.let { item ->
        AdminAchievementReviewDialog(
            achievement = item,
            isLoading = uiState.isActionLoading,
            onDismiss = viewModel::closeReviewModal,
            onApprove = { viewModel.approveAchievement(item.achievementId) },
            onReject = { reason -> viewModel.rejectAchievement(item.achievementId, reason) },
            onReturnCorrection = { msg -> viewModel.returnForCorrection(item.achievementId, msg) }
        )
    }
}

@Composable
fun AdminAchievementCard(
    achievement: Achievement,
    isDark: Boolean,
    onReview: () -> Unit
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
                        .background(if (isDark) CyberCyan.copy(alpha = 0.15f) else ElectricBlue.copy(alpha = 0.12f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(text = "${achievement.category} • ${achievement.achievementLevel}".uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isDark) CyberCyan else ElectricBlue)
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
            Text(
                text = "Student: ${achievement.studentName} (${achievement.studentId})",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = if (isDark) CyberCyan else ElectricBlue
            )
            Text(text = "Issuing Org: ${achievement.organizationName} • 📅 ${achievement.achievementDate}", style = MaterialTheme.typography.labelSmall, color = textSecColor, modifier = Modifier.padding(top = 2.dp))

            Spacer(modifier = Modifier.height(12.dp))

            RoboticsPrimaryButton(
                text = "🔍 Review & Verify Certificate",
                onClick = onReview,
                modifier = Modifier.fillMaxWidth(),
                height = 36.dp
            )
        }
    }
}

@Composable
fun AdminAchievementReviewDialog(
    achievement: Achievement,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onApprove: () -> Unit,
    onReject: (String) -> Unit,
    onReturnCorrection: (String) -> Unit
) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()
    val textColor = if (isDark) TextPrimaryDark else TextPrimaryLight
    val textSecColor = if (isDark) TextSecondaryDark else TextSecondaryLight

    var feedbackInput by remember { mutableStateOf("") }
    var actionType by remember { mutableStateOf("none") } // "reject", "correction"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Review Achievement Submission", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = achievement.title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                Text(text = "Student: ${achievement.studentName} (${achievement.studentId})", style = MaterialTheme.typography.labelSmall)
                Text(text = "Category: ${achievement.category} • Level: ${achievement.achievementLevel} • Org: ${achievement.organizationName}", style = MaterialTheme.typography.labelSmall, color = textSecColor)

                if (achievement.description.isNotBlank()) {
                    Text(text = achievement.description, style = MaterialTheme.typography.bodySmall, color = textColor)
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
                            Text(text = "Verify Online Link", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                if (achievement.certificateFileName.isNotBlank()) {
                    Text(text = "Attached File: 📄 ${achievement.certificateFileName}", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }

                if (actionType == "reject") {
                    RoboticsTextField(
                        value = feedbackInput,
                        onValueChange = { feedbackInput = it },
                        label = "Rejection Reason *",
                        placeholder = "State why verification failed..."
                    )
                } else if (actionType == "correction") {
                    RoboticsTextField(
                        value = feedbackInput,
                        onValueChange = { feedbackInput = it },
                        label = "Correction Message *",
                        placeholder = "Specify what certificate or info to update..."
                    )
                }
            }
        },
        confirmButton = {
            if (actionType == "none") {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    RoboticsPrimaryButton(
                        text = "✓ Approve",
                        onClick = onApprove,
                        isLoading = isLoading
                    )
                }
            } else if (actionType == "reject") {
                RoboticsPrimaryButton(
                    text = "Confirm Reject",
                    onClick = { onReject(feedbackInput) },
                    isLoading = isLoading,
                    enabled = feedbackInput.isNotBlank()
                )
            } else if (actionType == "correction") {
                RoboticsPrimaryButton(
                    text = "Send for Correction",
                    onClick = { onReturnCorrection(feedbackInput) },
                    isLoading = isLoading,
                    enabled = feedbackInput.isNotBlank()
                )
            }
        },
        dismissButton = {
            if (actionType == "none") {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    RoboticsOutlinedButton(
                        text = "↩ Return",
                        onClick = { actionType = "correction" }
                    )
                    RoboticsOutlinedButton(
                        text = "✕ Reject",
                        onClick = { actionType = "reject" }
                    )
                }
            } else {
                RoboticsOutlinedButton(text = "Back", onClick = { actionType = "none" })
            }
        }
    )
}
