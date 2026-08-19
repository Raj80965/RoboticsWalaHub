package com.roboticswala.hub.ui.screens.student.tabs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.roboticswala.hub.data.models.AttendanceData
import com.roboticswala.hub.data.models.EventItem
import com.roboticswala.hub.data.models.LabBooking
import com.roboticswala.hub.data.models.NoticeItem
import com.roboticswala.hub.data.models.StudentProject
import com.roboticswala.hub.data.models.StudentTask
import com.roboticswala.hub.data.models.UserProfile
import com.roboticswala.hub.ui.components.StatusChip
import com.roboticswala.hub.ui.screens.student.StudentUiState
import com.roboticswala.hub.ui.theme.CircuitError
import com.roboticswala.hub.ui.theme.CircuitSuccess
import com.roboticswala.hub.ui.theme.CyberCyan
import com.roboticswala.hub.ui.theme.CyberCyanGlow
import com.roboticswala.hub.ui.theme.DarkSurface
import com.roboticswala.hub.ui.theme.DarkSurfaceBorder
import com.roboticswala.hub.ui.theme.DarkSurfaceElevated
import com.roboticswala.hub.ui.theme.ElectricBlue
import com.roboticswala.hub.ui.theme.ElectricBlueLight
import com.roboticswala.hub.ui.theme.LightSurface
import com.roboticswala.hub.ui.theme.LightSurfaceBorder
import com.roboticswala.hub.ui.theme.LightSurfaceElevated
import com.roboticswala.hub.ui.theme.TextPrimaryDark
import com.roboticswala.hub.ui.theme.TextPrimaryLight
import com.roboticswala.hub.ui.theme.TextSecondaryDark
import com.roboticswala.hub.ui.theme.TextSecondaryLight
import com.roboticswala.hub.ui.theme.CircuitWarning

// ─────────────────────────────────────────────────────────────────────────────
// StudentHomeScreen — Day 5: Real Firestore Data
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentHomeScreen(
    uiState: StudentUiState,
    onRefresh: () -> Unit,
    onNavigateToScanner: () -> Unit = {},
    onNavigateToAttendanceHistory: () -> Unit = {},
    onNavigateToBookings: () -> Unit = {},
    onNavigateToProjects: () -> Unit = {},
    onNavigateToProjectDetails: (String) -> Unit = {},
    onNavigateToTasks: () -> Unit = {},
    onNavigateToAchievements: () -> Unit = {},
    onNavigateToNotices: () -> Unit = {},
    onNavigateToEvents: () -> Unit = {},
    onNavigateToEquipment: () -> Unit = {},
    onNavigateToExpenses: () -> Unit = {},
    onNavigateToLeaderboard: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    var showAdminDirectoryDialog by remember { mutableStateOf(false) }

    PullToRefreshBox(
        isRefreshing = uiState.isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier.fillMaxSize()
    ) {
        // Loading overlay
        AnimatedVisibility(
            visible = uiState.isLoading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(
                        color = if (isDark) CyberCyan else ElectricBlue,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Loading dashboard...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isDark) TextSecondaryDark else TextSecondaryLight
                    )
                }
            }
        }

        // Main dashboard content
        AnimatedVisibility(
            visible = !uiState.isLoading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                // Error card
                if (uiState.error != null) {
                    DashboardErrorCard(
                        message = uiState.error,
                        onRetry = onRefresh,
                        isDark = isDark
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // ── 1. Profile Header Card ───────────────────────────────
                StudentHeaderCard(
                    profile = uiState.userProfile,
                    isDark = isDark
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ── 1.1 Quick Action: Scan Attendance QR Button ───────────
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp)),
                    onClick = onNavigateToScanner,
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDark) ElectricBlue.copy(alpha = 0.15f) else ElectricBlue.copy(alpha = 0.08f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.5.dp,
                        if (isDark) CyberCyan.copy(alpha = 0.6f) else ElectricBlue.copy(alpha = 0.4f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isDark) CyberCyan.copy(alpha = 0.2f) else ElectricBlue.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.QrCodeScanner,
                                    contentDescription = "Scan QR",
                                    tint = if (isDark) CyberCyan else ElectricBlue,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Scan Attendance QR",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (isDark) TextPrimaryDark else TextPrimaryLight
                                )
                                Text(
                                    text = "Tap to Check-In or Check-Out for Lab",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isDark) CyberCyan else ElectricBlue
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Open Scanner",
                            tint = if (isDark) CyberCyan else ElectricBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // ── 1B. Lab Administrators & Mentors Master Card ─────────
                val adminCount = uiState.adminProfiles.size.coerceAtLeast(1)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showAdminDirectoryDialog = true }
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
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isDark) CyberCyan.copy(alpha = 0.15f) else ElectricBlue.copy(alpha = 0.12f))
                                    .border(
                                        width = 1.dp,
                                        color = if (isDark) CyberCyan.copy(alpha = 0.5f) else ElectricBlue.copy(alpha = 0.4f),
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.SupervisorAccount,
                                    contentDescription = "Lab In-Charges",
                                    tint = if (isDark) CyberCyan else ElectricBlue,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Robotics Lab In-Charges",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = if (isDark) TextPrimaryDark else TextPrimaryLight
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(if (isDark) CyberCyan.copy(alpha = 0.2f) else ElectricBlue.copy(alpha = 0.15f))
                                            .padding(horizontal = 5.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = "$adminCount ADMIN${if (adminCount > 1) "S" else ""}",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 8.5.sp,
                                                fontWeight = FontWeight.ExtraBold
                                            ),
                                            color = if (isDark) CyberCyan else ElectricBlue
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Faculty In-Charge & Verified Lab Admins • Tap to View",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                    color = if (isDark) TextSecondaryDark else TextSecondaryLight
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "View Admins",
                            tint = if (isDark) CyberCyan else ElectricBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ── 2. Attendance Card ───────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SectionTitle(text = "Attendance", isDark = isDark)
                    Text(
                        text = "View History ➔",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (isDark) CyberCyan else ElectricBlue,
                        modifier = Modifier.padding(4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                AttendanceCard(
                    data = uiState.attendanceData,
                    isDark = isDark,
                    onClick = onNavigateToAttendanceHistory
                )

                Spacer(modifier = Modifier.height(22.dp))

                // ── 3. Today's Lab Slot ──────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SectionTitle(text = "Today's Lab Slot", isDark = isDark)
                    Text(
                        text = "+ Book Slot ➔",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (isDark) CyberCyan else ElectricBlue,
                        modifier = Modifier
                            .clickable(onClick = onNavigateToBookings)
                            .padding(4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                TodayLabSlotCard(
                    booking = uiState.todayLabBooking,
                    isDark = isDark,
                    onClick = onNavigateToBookings
                )

                Spacer(modifier = Modifier.height(22.dp))

                // ── 4. Current Projects ──────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SectionTitle(text = "Current Projects", isDark = isDark)
                    Text(
                        text = "View All (${uiState.projects.size}) ➔",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (isDark) CyberCyan else ElectricBlue,
                        modifier = Modifier
                            .clickable(onClick = onNavigateToProjects)
                            .padding(4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                CurrentProjectsCard(
                    projects = uiState.projects,
                    isDark = isDark,
                    onNavigateToDetails = onNavigateToProjectDetails
                )

                Spacer(modifier = Modifier.height(22.dp))

                // ── 5. Assigned Tasks ────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SectionTitle(text = "Assigned Tasks", isDark = isDark)
                    Text(
                        text = "View All (${uiState.tasks.size}) ➔",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (isDark) CyberCyan else ElectricBlue,
                        modifier = Modifier
                            .clickable(onClick = onNavigateToTasks)
                            .padding(4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                AssignedTasksCard(tasks = uiState.tasks, isDark = isDark)

                // ── 6. Approved Achievements (Day 10) ───────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SectionTitle(text = "Achievements & Certs", isDark = isDark)
                    Text(
                        text = "View All ➔",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (isDark) CyberCyan else ElectricBlue,
                        modifier = Modifier
                            .clickable(onClick = onNavigateToAchievements)
                            .padding(4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onNavigateToAchievements)
                        .border(
                            width = 1.dp,
                            color = if (isDark) DarkSurfaceBorder else LightSurfaceBorder,
                            shape = RoundedCornerShape(18.dp)
                        ),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDark) DarkSurface.copy(alpha = 0.95f) else LightSurface
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
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(if (isDark) CyberCyan.copy(alpha = 0.15f) else ElectricBlue.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Filled.EmojiEvents, contentDescription = null, tint = if (isDark) CyberCyan else ElectricBlue, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Verified Awards & Certifications",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (isDark) TextPrimaryDark else TextPrimaryLight
                            )
                            Text(
                                text = "Submit competition results & certificates for profile verification",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isDark) TextSecondaryDark else TextSecondaryLight
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // ── 7. Latest Notice (Day 11) ───────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SectionTitle(text = "Latest Notice", isDark = isDark)
                    Text(
                        text = "Notice Board ➔",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (isDark) CyberCyan else ElectricBlue,
                        modifier = Modifier
                            .clickable(onClick = onNavigateToNotices)
                            .padding(4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Box(modifier = Modifier.clickable(onClick = onNavigateToNotices)) {
                    LatestNoticeCard(notice = uiState.latestNotice, isDark = isDark)
                }

                Spacer(modifier = Modifier.height(22.dp))

                // ── 8. Upcoming Event (Day 11) ───────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SectionTitle(text = "Upcoming Event", isDark = isDark)
                    Text(
                        text = "All Events ➔",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (isDark) CyberCyan else ElectricBlue,
                        modifier = Modifier
                            .clickable(onClick = onNavigateToEvents)
                            .padding(4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Box(modifier = Modifier.clickable(onClick = onNavigateToEvents)) {
                    UpcomingEventCard(event = uiState.upcomingEvent, isDark = isDark)
                }

                Spacer(modifier = Modifier.height(22.dp))

                // ── 9. Lab Equipment & Components (Day 12) ───────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SectionTitle(text = "Lab Equipment & Components", isDark = isDark)
                    Text(
                        text = "Browse & Request ➔",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (isDark) CyberCyan else ElectricBlue,
                        modifier = Modifier
                            .clickable(onClick = onNavigateToEquipment)
                            .padding(4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onNavigateToEquipment)
                        .border(
                            width = 1.dp,
                            color = if (isDark) DarkSurfaceBorder else LightSurfaceBorder,
                            shape = RoundedCornerShape(18.dp)
                        ),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDark) DarkSurface.copy(alpha = 0.95f) else LightSurface
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
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(if (isDark) CyberCyan.copy(alpha = 0.15f) else ElectricBlue.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Filled.Inventory2, contentDescription = null, tint = if (isDark) CyberCyan else ElectricBlue, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Hardware & Sensors Inventory",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (isDark) TextPrimaryDark else TextPrimaryLight
                            )
                            Text(
                                text = "Request microcontrollers, motors, LiPo batteries & tools for your projects",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isDark) TextSecondaryDark else TextSecondaryLight
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // ── 10. Project Budget & Expenses (Day 13) ────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SectionTitle(text = "Project Budget & Grants", isDark = isDark)
                    Text(
                        text = "Claim Expenses ➔",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (isDark) CyberCyan else ElectricBlue,
                        modifier = Modifier
                            .clickable(onClick = onNavigateToExpenses)
                            .padding(4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onNavigateToExpenses)
                        .border(
                            width = 1.dp,
                            color = if (isDark) DarkSurfaceBorder else LightSurfaceBorder,
                            shape = RoundedCornerShape(18.dp)
                        ),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDark) DarkSurface.copy(alpha = 0.95f) else LightSurface
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
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(if (isDark) CyberCyan.copy(alpha = 0.15f) else ElectricBlue.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Filled.Assessment, contentDescription = null, tint = if (isDark) CyberCyan else ElectricBlue, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Budget Utilization & Bill Claims",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (isDark) TextPrimaryDark else TextPrimaryLight
                            )
                            Text(
                                text = "Submit component bills, upload receipts & track remaining project funds",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isDark) TextSecondaryDark else TextSecondaryLight
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // ── 11. Student Leaderboard & Ranks (Day 14) ────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SectionTitle(text = "Student Leaderboard & Ranks", isDark = isDark)
                    Text(
                        text = "View Top Rankers ➔",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (isDark) CyberCyan else ElectricBlue,
                        modifier = Modifier
                            .clickable(onClick = onNavigateToLeaderboard)
                            .padding(4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onNavigateToLeaderboard)
                        .border(
                            width = 1.dp,
                            color = if (isDark) CyberCyan.copy(alpha = 0.5f) else ElectricBlue.copy(alpha = 0.35f),
                            shape = RoundedCornerShape(18.dp)
                        ),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDark) DarkSurface.copy(alpha = 0.95f) else LightSurface
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
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(if (isDark) CyberCyan.copy(alpha = 0.15f) else ElectricBlue.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Filled.EmojiEvents, contentDescription = null, tint = if (isDark) CyberCyan else ElectricBlue, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Real-Time 100-Point Performance Rankings",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (isDark) TextPrimaryDark else TextPrimaryLight
                            )
                            Text(
                                text = "Transparent ranking based on attendance, projects, tasks & achievements",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isDark) TextSecondaryDark else TextSecondaryLight
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))
            }
        }

        if (showAdminDirectoryDialog) {
            AdminDirectoryDialog(
                adminProfiles = uiState.adminProfiles,
                isDark = isDark,
                onDismiss = { showAdminDirectoryDialog = false }
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Section Title
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SectionTitle(text: String, isDark: Boolean) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = if (isDark) TextPrimaryDark else TextPrimaryLight
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Error Card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun DashboardErrorCard(
    message: String,
    onRetry: () -> Unit,
    isDark: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CircuitError.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = CircuitError.copy(alpha = 0.08f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.ErrorOutline,
                contentDescription = null,
                tint = CircuitError,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Could not load dashboard data",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = CircuitError
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isDark) TextSecondaryDark else TextSecondaryLight
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = CircuitError),
                modifier = Modifier.height(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Retry", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 1. Student Header Card — Real Profile Data
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun StudentHeaderCard(
    profile: UserProfile?,
    isDark: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = if (isDark) DarkSurfaceBorder else LightSurfaceBorder,
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) DarkSurface.copy(alpha = 0.95f) else LightSurface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "WELCOME BACK,",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    ),
                    color = if (isDark) CyberCyan else ElectricBlue
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = profile?.fullName?.ifBlank { "Student" } ?: "Student",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    ),
                    color = if (isDark) TextPrimaryDark else TextPrimaryLight
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Student ID
                val displayId = profile?.displayStudentId ?: ""
                if (displayId.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isDark) DarkSurfaceElevated else LightSurfaceElevated
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "ID: $displayId",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            ),
                            color = if (isDark) CyberCyan else ElectricBlue
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }

                // College / Branch / Year
                val details = buildList {
                    if (!profile?.college.isNullOrBlank()) add(profile!!.college)
                    if (!profile?.branch.isNullOrBlank()) add(profile!!.branch)
                    if (!profile?.year.isNullOrBlank()) add(profile!!.year)
                }
                if (details.isNotEmpty()) {
                    Text(
                        text = details.joinToString(" • "),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isDark) TextSecondaryDark else TextSecondaryLight,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                StatusChip(status = "Approved")
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Profile Photo or Initials Avatar
            ProfileAvatar(
                photoUrl = profile?.photoUrl ?: "",
                initials = profile?.initials ?: "ST",
                isDark = isDark
            )
        }
    }
}

@Composable
private fun ProfileAvatar(
    photoUrl: String,
    initials: String,
    isDark: Boolean
) {
    val avatarBitmap = remember(photoUrl) {
        if (photoUrl.isNotBlank() && !photoUrl.startsWith("http")) {
            try {
                val clean = if (photoUrl.contains(",")) photoUrl.substringAfter(",") else photoUrl
                val bytes = Base64.decode(clean, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            } catch (e: Exception) {
                null
            }
        } else null
    }

    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(ElectricBlue, CyberCyan)
                )
            )
            .border(2.dp, if (isDark) CyberCyan else ElectricBlue, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (avatarBitmap != null) {
            Image(
                bitmap = avatarBitmap.asImageBitmap(),
                contentDescription = "Profile Photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
            )
        } else if (photoUrl.startsWith("http")) {
            AsyncImage(
                model = photoUrl,
                contentDescription = "Profile Photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
            )
        } else {
            Text(
                text = initials,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = Color.White
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 2. Attendance Card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun AttendanceCard(
    data: AttendanceData?,
    isDark: Boolean,
    onClick: () -> Unit = {}
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, if (isDark) DarkSurfaceBorder else LightSurfaceBorder, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) DarkSurfaceElevated else LightSurfaceElevated
        )
    ) {
        if (data == null) {
            // Empty state
            EmptyStateRow(
                icon = Icons.Filled.Assessment,
                message = "No attendance records yet",
                subMessage = "Your attendance will appear here once tracked",
                isDark = isDark
            )
        } else {
            Column(modifier = Modifier.fillMaxWidth().padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${data.attendancePercentage.toInt()}%",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 36.sp
                            ),
                            color = when {
                                data.attendancePercentage >= 85 -> CircuitSuccess
                                data.attendancePercentage >= 75 -> CircuitWarning
                                else -> CircuitError
                            }
                        )
                        Text(
                            text = "Attendance",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isDark) TextSecondaryDark else TextSecondaryLight
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        AttendanceStat(
                            label = "Present",
                            value = "${data.presentDays} days",
                            isDark = isDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        AttendanceStat(
                            label = "Total",
                            value = "${data.totalDays} days",
                            isDark = isDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        AttendanceStat(
                            label = "Work Hours",
                            value = "${data.totalWorkingHours} hrs",
                            isDark = isDark
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
                LinearProgressIndicator(
                    progress = { (data.attendancePercentage / 100f).toFloat().coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth().height(8.dp),
                    color = when {
                        data.attendancePercentage >= 85 -> CircuitSuccess
                        data.attendancePercentage >= 75 -> CircuitWarning
                        else -> CircuitError
                    },
                    trackColor = if (isDark) DarkSurfaceBorder else LightSurfaceBorder,
                    strokeCap = StrokeCap.Round
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Target: ≥85% required",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isDark) TextSecondaryDark else TextSecondaryLight
                )
            }
        }
    }
}

@Composable
private fun AttendanceStat(label: String, value: String, isDark: Boolean) {
    Column(horizontalAlignment = Alignment.End) {
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = if (isDark) TextPrimaryDark else TextPrimaryLight
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isDark) TextSecondaryDark else TextSecondaryLight
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 3. Today's Lab Slot Card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun TodayLabSlotCard(
    booking: LabBooking?,
    isDark: Boolean,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .border(
                1.dp,
                if (isDark) CyberCyan.copy(alpha = 0.3f) else ElectricBlue.copy(alpha = 0.3f),
                RoundedCornerShape(18.dp)
            ),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) DarkSurfaceElevated else LightSurfaceElevated
        )
    ) {
        if (booking == null) {
            EmptyStateRow(
                icon = Icons.Filled.Schedule,
                message = "No lab slot booked for today",
                subMessage = "Book a lab slot to see it here",
                isDark = isDark
            )
        } else {
            Column(modifier = Modifier.fillMaxWidth().padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Schedule,
                            contentDescription = null,
                            tint = if (isDark) CyberCyan else ElectricBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${booking.startTime} - ${booking.endTime}",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (isDark) TextPrimaryDark else TextPrimaryLight
                        )
                    }
                    StatusChip(status = booking.status)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.CalendarToday,
                        contentDescription = null,
                        tint = if (isDark) TextSecondaryDark else TextSecondaryLight,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = booking.date,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isDark) TextSecondaryDark else TextSecondaryLight
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.PrecisionManufacturing,
                        contentDescription = null,
                        tint = if (isDark) CyberCyan else ElectricBlue,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = booking.projectName,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = if (isDark) TextPrimaryDark else TextPrimaryLight
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 4. Current Projects Card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun CurrentProjectsCard(
    projects: List<StudentProject>,
    isDark: Boolean,
    onNavigateToDetails: (String) -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, if (isDark) DarkSurfaceBorder else LightSurfaceBorder, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) DarkSurface else LightSurface
        )
    ) {
        if (projects.isEmpty()) {
            EmptyStateRow(
                icon = Icons.Filled.FolderOpen,
                message = "No active projects yet",
                subMessage = "Your assigned projects will appear here",
                isDark = isDark
            )
        } else {
            Column(modifier = Modifier.fillMaxWidth().padding(18.dp)) {
                projects.forEach { project ->
                    ProjectRow(
                        project = project,
                        isDark = isDark,
                        onClick = { onNavigateToDetails(project.id) }
                    )
                    if (project != projects.last()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(if (isDark) DarkSurfaceBorder else LightSurfaceBorder)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ProjectRow(
    project: StudentProject,
    isDark: Boolean,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = project.title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = if (isDark) TextPrimaryDark else TextPrimaryLight,
                modifier = Modifier.weight(1f)
            )
            StatusChip(status = project.status)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Progress",
                style = MaterialTheme.typography.labelSmall,
                color = if (isDark) TextSecondaryDark else TextSecondaryLight
            )
            Text(
                text = "${project.progressPercent}%",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = if (isDark) CyberCyan else ElectricBlue
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { project.progressPercent / 100f },
            modifier = Modifier.fillMaxWidth().height(6.dp),
            color = CyberCyanGlow,
            trackColor = if (isDark) DarkSurfaceBorder else LightSurfaceBorder,
            strokeCap = StrokeCap.Round
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 5. Assigned Tasks Card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun AssignedTasksCard(
    tasks: List<StudentTask>,
    isDark: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, if (isDark) DarkSurfaceBorder else LightSurfaceBorder, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) DarkSurface else LightSurface
        )
    ) {
        if (tasks.isEmpty()) {
            EmptyStateRow(
                icon = Icons.Filled.Assignment,
                message = "No tasks assigned",
                subMessage = "Tasks assigned by your mentor will appear here",
                isDark = isDark
            )
        } else {
            Column(modifier = Modifier.fillMaxWidth().padding(18.dp)) {
                tasks.forEach { task ->
                    TaskRow(task = task, isDark = isDark)
                    if (task != tasks.last()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(if (isDark) DarkSurfaceBorder else LightSurfaceBorder)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskRow(task: StudentTask, isDark: Boolean) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        // Priority indicator dot
        Box(
            modifier = Modifier
                .padding(top = 5.dp)
                .size(8.dp)
                .clip(CircleShape)
                .background(
                    when (task.priority) {
                        "High" -> CircuitError
                        "Medium" -> CircuitWarning
                        else -> CircuitSuccess
                    }
                )
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = if (isDark) TextPrimaryDark else TextPrimaryLight
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Priority: ${task.priority}",
                    style = MaterialTheme.typography.labelSmall,
                    color = when (task.priority) {
                        "High" -> CircuitError
                        "Medium" -> CircuitWarning
                        else -> CircuitSuccess
                    }
                )
                if (task.deadline.isNotBlank()) {
                    Text(
                        text = "Due: ${task.deadline}",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isDark) TextSecondaryDark else TextSecondaryLight
                    )
                }
            }
        }
        StatusChip(status = task.status)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 6. Latest Notice Card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun LatestNoticeCard(
    notice: NoticeItem?,
    isDark: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, if (isDark) DarkSurfaceBorder else LightSurfaceBorder, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) DarkSurface else LightSurface
        )
    ) {
        if (notice == null) {
            EmptyStateRow(
                icon = Icons.Filled.Campaign,
                message = "No new notices",
                subMessage = "Admin notices will appear here",
                isDark = isDark
            )
        } else {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            if (notice.isUrgent) CircuitError.copy(alpha = 0.15f)
                            else ElectricBlue.copy(alpha = 0.15f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Campaign,
                        contentDescription = null,
                        tint = if (notice.isUrgent) CircuitError else if (isDark) CyberCyan else ElectricBlue,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = notice.title,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (isDark) TextPrimaryDark else TextPrimaryLight,
                            modifier = Modifier.weight(1f)
                        )
                        if (notice.isUrgent) {
                            Spacer(modifier = Modifier.width(8.dp))
                            StatusChip(status = "Urgent")
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = notice.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isDark) TextSecondaryDark else TextSecondaryLight
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = notice.date,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = if (isDark) CyberCyan else ElectricBlue
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 7. Upcoming Event Card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun UpcomingEventCard(
    event: EventItem?,
    isDark: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, if (isDark) DarkSurfaceBorder else LightSurfaceBorder, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) DarkSurfaceElevated else LightSurfaceElevated
        )
    ) {
        if (event == null) {
            EmptyStateRow(
                icon = Icons.Filled.Event,
                message = "No upcoming events",
                subMessage = "Events will appear here when scheduled",
                isDark = isDark
            )
        } else {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(CyberCyan.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Event,
                        contentDescription = null,
                        tint = if (isDark) CyberCyan else ElectricBlue,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (isDark) TextPrimaryDark else TextPrimaryLight
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.CalendarToday,
                            contentDescription = null,
                            tint = if (isDark) CyberCyan else ElectricBlue,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${event.date}${if (event.time.isNotBlank()) " • ${event.time}" else ""}",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = if (isDark) CyberCyanGlow else ElectricBlue
                        )
                    }
                    if (event.location.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.LocationOn,
                                contentDescription = null,
                                tint = if (isDark) TextSecondaryDark else TextSecondaryLight,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = event.location,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isDark) TextSecondaryDark else TextSecondaryLight
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Empty State Row (reusable)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun EmptyStateRow(
    icon: ImageVector,
    message: String,
    subMessage: String,
    isDark: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    if (isDark) DarkSurfaceElevated else LightSurfaceElevated
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isDark) TextSecondaryDark else TextSecondaryLight,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = if (isDark) TextSecondaryDark else TextSecondaryLight
            )
            Text(
                text = subMessage,
                style = MaterialTheme.typography.bodySmall,
                color = if (isDark) TextSecondaryDark.copy(alpha = 0.7f) else TextSecondaryLight.copy(alpha = 0.7f)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Lab Administrators & Mentors Directory Dialog
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun AdminDirectoryDialog(
    adminProfiles: List<UserProfile>,
    isDark: Boolean,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val list = if (adminProfiles.isNotEmpty()) adminProfiles else listOf(
        UserProfile(
            fullName = "Robotics Lab Admin In-Charge",
            email = "admin@roboticswala.com",
            studentId = "RWH-ADM-2026-001",
            role = "Admin",
            status = "Approved",
            branch = "Robotics & AI Research Bay"
        )
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isDark) CyberCyan.copy(alpha = 0.15f) else ElectricBlue.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.SupervisorAccount,
                        contentDescription = null,
                        tint = if (isDark) CyberCyan else ElectricBlue,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Lab In-Charges & Mentors",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (isDark) TextPrimaryDark else TextPrimaryLight
                    )
                    Text(
                        text = "${list.size} Verified Administrator${if (list.size > 1) "s" else ""}",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isDark) CyberCyan else ElectricBlue
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                list.forEach { admin ->
                    AdminSquareCard(
                        admin = admin,
                        isDark = isDark,
                        onContactClick = {
                            if (admin.phone.isNotBlank()) {
                                try {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${admin.phone}"))
                                    context.startActivity(intent)
                                } catch (_: Exception) {}
                            } else if (admin.email.isNotBlank()) {
                                try {
                                    val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${admin.email}"))
                                    context.startActivity(intent)
                                } catch (_: Exception) {}
                            }
                        },
                        onEmailClick = {
                            if (admin.email.isNotBlank()) {
                                try {
                                    val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${admin.email}"))
                                    context.startActivity(intent)
                                } catch (_: Exception) {}
                            }
                        }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Close",
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) CyberCyan else ElectricBlue
                )
            }
        },
        containerColor = if (isDark) DarkSurface else LightSurface,
        shape = RoundedCornerShape(24.dp)
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Admin Square / Compact Card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun AdminSquareCard(
    admin: UserProfile,
    isDark: Boolean,
    onContactClick: () -> Unit = {},
    onEmailClick: () -> Unit = {}
) {
    val photoBitmap = remember(admin.photoUrl) {
        if (admin.photoUrl.isNotBlank() && !admin.photoUrl.startsWith("http")) {
            try {
                val clean = if (admin.photoUrl.contains(",")) admin.photoUrl.substringAfter(",") else admin.photoUrl
                val bytes = Base64.decode(clean, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            } catch (e: Exception) {
                null
            }
        } else null
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = if (isDark) DarkSurfaceBorder else LightSurfaceBorder,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) DarkSurfaceElevated else LightSurfaceElevated
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isDark) CyberCyan.copy(alpha = 0.15f) else ElectricBlue.copy(alpha = 0.12f))
                        .border(
                            width = 1.5.dp,
                            color = if (isDark) CyberCyan.copy(alpha = 0.6f) else ElectricBlue.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(14.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (photoBitmap != null) {
                        Image(
                            bitmap = photoBitmap.asImageBitmap(),
                            contentDescription = "Admin Photo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else if (admin.photoUrl.startsWith("http")) {
                        AsyncImage(
                            model = admin.photoUrl,
                            contentDescription = "Admin Photo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Admin",
                            tint = if (isDark) CyberCyan else ElectricBlue,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = admin.fullName.ifBlank { "Administrator" },
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (isDark) TextPrimaryDark else TextPrimaryLight,
                            maxLines = 1
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (isDark) CyberCyan.copy(alpha = 0.2f) else ElectricBlue.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
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
                        text = "ID: ${admin.displayAdminId}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) CyberCyanGlow else ElectricBlue
                        )
                    )

                    Text(
                        text = if (admin.branch.isNotBlank()) admin.branch else "Robotics Lab In-Charge",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                        color = if (isDark) TextSecondaryDark else TextSecondaryLight,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action row: Contact buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (admin.phone.isNotBlank()) {
                    Button(
                        onClick = onContactClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDark) CyberCyan.copy(alpha = 0.2f) else ElectricBlue.copy(alpha = 0.15f),
                            contentColor = if (isDark) CyberCyan else ElectricBlue
                        ),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Call,
                            contentDescription = "Call",
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Call",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }

                if (admin.email.isNotBlank()) {
                    Button(
                        onClick = onEmailClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDark) DarkSurfaceBorder else LightSurfaceBorder,
                            contentColor = if (isDark) TextPrimaryDark else TextPrimaryLight
                        ),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Email,
                            contentDescription = "Email",
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Email Admin",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}
