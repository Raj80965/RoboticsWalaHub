package com.roboticswala.hub.ui.screens.student.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.roboticswala.hub.data.models.StudentDashboardData
import com.roboticswala.hub.ui.components.MetricCard
import com.roboticswala.hub.ui.components.RoboticsLogo
import com.roboticswala.hub.ui.components.StatusChip
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

@Composable
fun StudentHomeScreen(
    data: StudentDashboardData,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Top Welcome & Student ID Header Card
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
                verticalAlignment = Alignment.CenterVertY,
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
                        text = data.studentName,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        ),
                        color = if (isDark) TextPrimaryDark else TextPrimaryLight
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(verticalAlignment = Alignment.CenterVertY) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isDark) DarkSurfaceElevated else LightSurfaceElevated
                                )
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "ID: ${data.studentId}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp
                                ),
                                color = if (isDark) TextSecondaryDark else TextSecondaryLight
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        StatusChip(status = "Active")
                    }
                }

                RoboticsLogo(
                    size = 56.dp,
                    animate = false
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Quick Stats Row (Attendance & Working Hours)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            MetricCard(
                title = "Lab Attendance",
                value = "${data.attendancePercentage}%",
                subtitle = "Target: >85% required",
                icon = Icons.Filled.Assessment,
                modifier = Modifier.weight(1f),
                accentColor = CircuitSuccess
            )

            MetricCard(
                title = "Total Work Hours",
                value = "${data.totalWorkingHours} hrs",
                subtitle = "Active this semester",
                icon = Icons.Filled.AccessTime,
                modifier = Modifier.weight(1f),
                accentColor = CyberCyan
            )
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Today's Lab Slot Card
        Text(
            text = "Today's Lab Slot",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = if (isDark) TextPrimaryDark else TextPrimaryLight
        )

        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = if (isDark) CyberCyan.copy(alpha = 0.3f) else ElectricBlue.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(18.dp)
                ),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDark) DarkSurfaceElevated else LightSurfaceElevated
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertY,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertY) {
                        Icon(
                            imageVector = Icons.Filled.Schedule,
                            contentDescription = null,
                            tint = if (isDark) CyberCyan else ElectricBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = data.todaySlot.time,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (isDark) TextPrimaryDark else TextPrimaryLight
                        )
                    }

                    StatusChip(status = data.todaySlot.status)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "📍 ${data.todaySlot.station}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = if (isDark) TextPrimaryDark else TextPrimaryLight
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Topic: ${data.todaySlot.topic}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isDark) TextSecondaryDark else TextSecondaryLight
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Current Project & Progress
        Text(
            text = "Current Project",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = if (isDark) TextPrimaryDark else TextPrimaryLight
        )

        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = if (isDark) DarkSurfaceBorder else LightSurfaceBorder,
                    shape = RoundedCornerShape(18.dp)
                ),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDark) DarkSurface else LightSurface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertY,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = data.currentProject.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (isDark) TextPrimaryDark else TextPrimaryLight,
                        modifier = Modifier.weight(1f)
                    )
                    StatusChip(status = data.currentProject.status)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = data.currentProject.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isDark) CyberCyan else ElectricBlue
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Progress Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertY
                ) {
                    Text(
                        text = "Project Completion",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isDark) TextSecondaryDark else TextSecondaryLight
                    )
                    Text(
                        text = "${data.currentProject.progress}%",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (isDark) CyberCyan else ElectricBlue
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { data.currentProject.progress / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = CyberCyanGlow,
                    trackColor = if (isDark) DarkSurfaceBorder else LightSurfaceBorder,
                    strokeCap = StrokeCap.Round
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Next: ${data.currentProject.nextMilestone}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = if (isDark) TextSecondaryDark else TextSecondaryLight
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Latest Notice Card
        Text(
            text = "Latest Notice",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = if (isDark) TextPrimaryDark else TextPrimaryLight
        )

        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = if (isDark) DarkSurfaceBorder else LightSurfaceBorder,
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
                    .padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(ElectricBlue.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Campaign,
                        contentDescription = null,
                        tint = if (isDark) CyberCyan else ElectricBlue,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertY
                    ) {
                        Text(
                            text = data.latestNotice.title,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (isDark) TextPrimaryDark else TextPrimaryLight
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = data.latestNotice.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isDark) TextSecondaryDark else TextSecondaryLight
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = data.latestNotice.date,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = if (isDark) CyberCyan else ElectricBlue
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Upcoming Event Card
        Text(
            text = "Upcoming Event",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = if (isDark) TextPrimaryDark else TextPrimaryLight
        )

        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = if (isDark) DarkSurfaceBorder else LightSurfaceBorder,
                    shape = RoundedCornerShape(18.dp)
                ),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDark) DarkSurfaceElevated else LightSurfaceElevated
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
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
                        text = data.upcomingEvent.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (isDark) TextPrimaryDark else TextPrimaryLight
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "📅 ${data.upcomingEvent.dateTime}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = if (isDark) CyberCyanGlow else ElectricBlue
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "📍 ${data.upcomingEvent.venue}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isDark) TextSecondaryDark else TextSecondaryLight
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "Speaker: ${data.upcomingEvent.speaker}",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isDark) TextSecondaryDark else TextSecondaryLight
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
