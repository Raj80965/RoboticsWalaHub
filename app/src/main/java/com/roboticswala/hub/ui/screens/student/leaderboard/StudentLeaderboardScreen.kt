package com.roboticswala.hub.ui.screens.student.leaderboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.roboticswala.hub.data.models.LeaderboardEntry
import com.roboticswala.hub.data.models.UserProfile
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
fun StudentLeaderboardScreen(
    userProfile: UserProfile,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StudentLeaderboardViewModel = viewModel(
        factory = StudentLeaderboardViewModelFactory(userProfile.uid)
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val isDark = isSystemInDarkTheme()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(if (isDark) DarkSurface else LightSurface)
    ) {
        // ── Header Bar ────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = if (isDark) CyberCyan else ElectricBlue
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Student Leaderboard & Ranks",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = if (isDark) TextPrimaryDark else TextPrimaryLight
                )
                Text(
                    text = "Real-time performance scores out of 100 points",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isDark) TextSecondaryDark else TextSecondaryLight
                )
            }
        }

        // ── Personal Performance Card ─────────────────────────────────────────
        uiState.myPerformanceReport?.let { report ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .border(
                        width = 1.dp,
                        color = if (isDark) CyberCyan.copy(alpha = 0.5f) else ElectricBlue.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(16.dp)
                    ),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDark) DarkSurfaceElevated else LightSurfaceElevated
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Your Performance Score", fontSize = 11.sp, color = if (isDark) TextSecondaryDark else TextSecondaryLight)
                            Text("${report.performanceScore} / 100 Pts", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = if (isDark) CyberCyan else ElectricBlue)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isDark) CyberCyan.copy(alpha = 0.15f) else ElectricBlue.copy(alpha = 0.12f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("Rank #${report.leaderboardRank}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = if (isDark) CyberCyan else ElectricBlue)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Attendance", fontSize = 10.sp, color = if (isDark) TextSecondaryDark else TextSecondaryLight)
                            Text("${String.format("%.1f", report.attendancePercentage)}%", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("Projects", fontSize = 10.sp, color = if (isDark) TextSecondaryDark else TextSecondaryLight)
                            Text("${report.completedProjectsCount} Done (${report.activeProjectsCount} Active)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("Tasks Done", fontSize = 10.sp, color = if (isDark) TextSecondaryDark else TextSecondaryLight)
                            Text("${report.completedTasksCount} / ${report.assignedTasksCount}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("Achievements", fontSize = 10.sp, color = if (isDark) TextSecondaryDark else TextSecondaryLight)
                            Text("${report.approvedAchievementsCount} Verified", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // ── Filter Period Chips ───────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val periods = listOf(
                "ALL_TIME" to "🏆 All-Time",
                "THIS_MONTH" to "📅 This Month",
                "THIS_WEEK" to "⚡ This Week"
            )
            periods.forEach { (key, label) ->
                FilterChip(
                    selected = uiState.selectedPeriod == key,
                    onClick = { viewModel.loadLeaderboard(key) },
                    label = { Text(label, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = if (isDark) CyberCyan.copy(alpha = 0.2f) else ElectricBlue.copy(alpha = 0.15f),
                        selectedLabelColor = if (isDark) CyberCyan else ElectricBlue
                    )
                )
            }
        }

        // ── Leaderboard List ──────────────────────────────────────────────────
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = if (isDark) CyberCyan else ElectricBlue)
            }
        } else if (uiState.leaderboard.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No ranked students yet.", color = if (isDark) TextSecondaryDark else TextSecondaryLight)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(uiState.leaderboard, key = { it.studentUid }) { entry ->
                    LeaderboardCard(
                        entry = entry,
                        isCurrentUser = entry.studentUid == userProfile.uid,
                        isDark = isDark,
                        onClick = { viewModel.selectEntry(entry) }
                    )
                }
            }
        }
    }

    // ── Score Breakdown Dialog ────────────────────────────────────────────────
    uiState.selectedEntryDetails?.let { entry ->
        AlertDialog(
            onDismissRequest = { viewModel.closeDetails() },
            title = {
                Text(
                    text = "${entry.studentName} - Score Breakdown",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Total Score: ${entry.totalScore} / 100 Pts (${entry.badge})", fontWeight = FontWeight.Bold, color = if (isDark) CyberCyan else ElectricBlue)
                    Text("• Attendance: ${entry.attendanceScore} / 25 Pts", fontSize = 12.sp)
                    Text("• Completed Projects: ${entry.projectsScore} / 20 Pts", fontSize = 12.sp)
                    Text("• Project Progress: ${entry.progressScore} / 15 Pts", fontSize = 12.sp)
                    Text("• Completed Tasks: ${entry.tasksScore} / 15 Pts", fontSize = 12.sp)
                    Text("• Approved Achievements: ${entry.achievementsScore} / 15 Pts", fontSize = 12.sp)
                    Text("• Event Participation: ${entry.eventsScore} / 5 Pts", fontSize = 12.sp)
                    Text("• Daily Consistency: ${entry.consistencyScore} / 5 Pts", fontSize = 12.sp)
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.closeDetails() }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
private fun LeaderboardCard(
    entry: LeaderboardEntry,
    isCurrentUser: Boolean,
    isDark: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .border(
                width = if (isCurrentUser) 1.5.dp else 1.dp,
                color = if (isCurrentUser) (if (isDark) CyberCyan else ElectricBlue)
                else if (isDark) DarkSurfaceBorder else LightSurfaceBorder,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) DarkSurfaceElevated else LightSurfaceElevated
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank Badge / Number
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        when (entry.rank) {
                            1 -> Color(0xFFFFD700).copy(alpha = 0.2f)
                            2 -> Color(0xFFC0C0C0).copy(alpha = 0.2f)
                            3 -> Color(0xFFCD7F32).copy(alpha = 0.2f)
                            else -> if (isDark) DarkSurfaceBorder else LightSurfaceBorder
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (entry.rank) {
                        1 -> "🥇"
                        2 -> "🥈"
                        3 -> "🥉"
                        else -> "#${entry.rank}"
                    },
                    fontSize = if (entry.rank <= 3) 16.sp else 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = entry.studentName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (isDark) TextPrimaryDark else TextPrimaryLight
                    )
                    if (isCurrentUser) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (isDark) CyberCyan.copy(alpha = 0.2f) else ElectricBlue.copy(alpha = 0.15f))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text("YOU", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (isDark) CyberCyan else ElectricBlue)
                        }
                    }
                }

                Text(
                    text = "${entry.studentId} • ${entry.department}",
                    fontSize = 11.sp,
                    color = if (isDark) TextSecondaryDark else TextSecondaryLight
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Progress Indicator for Score out of 100
                val progress = (entry.totalScore / 100f).toFloat().coerceIn(0f, 1f)
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = if (entry.rank == 1) Color(0xFFFFD700) else if (isDark) CyberCyan else ElectricBlue,
                    trackColor = if (isDark) DarkSurfaceBorder else LightSurfaceBorder
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${entry.totalScore}",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) CyberCyan else ElectricBlue
                )
                Text(
                    text = "pts",
                    fontSize = 10.sp,
                    color = if (isDark) TextSecondaryDark else TextSecondaryLight
                )
            }
        }
    }
}
