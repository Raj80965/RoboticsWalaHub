package com.roboticswala.hub.ui.screens.admin.tabs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.roboticswala.hub.data.models.AttendanceRecord
import com.roboticswala.hub.ui.components.RoboticsPrimaryButton
import com.roboticswala.hub.ui.components.RoboticsOutlinedButton
import com.roboticswala.hub.ui.components.RoboticsTextField
import com.roboticswala.hub.ui.screens.admin.AdminAttendanceViewModel
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAttendanceScreen(
    viewModel: AdminAttendanceViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isDark = isSystemInDarkTheme()

    val surfaceColor = if (isDark) DarkSurface else LightSurface
    val borderColor = if (isDark) DarkSurfaceBorder else LightSurfaceBorder
    val textColor = if (isDark) TextPrimaryDark else TextPrimaryLight
    val textSecColor = if (isDark) TextSecondaryDark else TextSecondaryLight

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header Card
        item {
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
                        Column {
                            Text(
                                text = "Lab Attendance Control",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                ),
                                color = textColor
                            )
                            Text(
                                text = "Day 6 • Real-Time QR Session System",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isDark) CyberCyan else ElectricBlue
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isDark) DarkSurfaceElevated else LightSurfaceElevated),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCode,
                                contentDescription = "QR",
                                tint = if (isDark) CyberCyan else ElectricBlue
                            )
                        }
                    }
                }
            }
        }

        // 2. Active Session Card OR Start Session Form
        item {
            if (uiState.activeSession != null) {
                // ACTIVE QR SESSION CARD
                val session = uiState.activeSession!!
                val minutesLeft = uiState.remainingSeconds / 60
                val secondsLeft = uiState.remainingSeconds % 60
                val countdownFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutesLeft, secondsLeft)

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = (if (isDark) DarkSurfaceElevated else LightSurfaceElevated)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.5.dp,
                        CircuitSuccess.copy(alpha = 0.6f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(CircuitSuccess)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "SESSION ACTIVE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    ),
                                    color = CircuitSuccess
                                )
                            }

                            // Countdown Badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (uiState.remainingSeconds < 60) CircuitError.copy(alpha = 0.15f)
                                        else CircuitWarning.copy(alpha = 0.15f)
                                    )
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.HourglassBottom,
                                        contentDescription = "Timer",
                                        modifier = Modifier.size(14.dp),
                                        tint = if (uiState.remainingSeconds < 60) CircuitError else CircuitWarning
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "$countdownFormatted left",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = if (uiState.remainingSeconds < 60) CircuitError else CircuitWarning
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // QR Code Image
                        uiState.qrBitmap?.let { bitmap ->
                            Box(
                                modifier = Modifier
                                    .size(230.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.White)
                                    .border(3.dp, CyberCyan.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    bitmap = bitmap.asImageBitmap(),
                                    contentDescription = "Attendance QR",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = session.labName,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = textColor
                        )

                        Text(
                            text = "Token: ${session.sessionToken} • Students can scan to Check-In & Check-Out",
                            style = MaterialTheme.typography.bodySmall,
                            color = textSecColor,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Stop Session Button
                        RoboticsOutlinedButton(
                            text = if (uiState.isStoppingSession) "Stopping Session..." else "⏹ Stop Attendance Session",
                            onClick = viewModel::stopSession,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            } else {
                // START SESSION CARD
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = surfaceColor),
                    border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Start New Attendance Session",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = textColor
                        )
                        Text(
                            text = "Generate an expiring QR code for student check-ins",
                            style = MaterialTheme.typography.bodySmall,
                            color = textSecColor
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        RoboticsTextField(
                            value = uiState.labNameInput,
                            onValueChange = viewModel::setLabNameInput,
                            label = "Lab Station / Room Name",
                            placeholder = "e.g. Robotics Lab Bay 1",
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "QR Expiration Duration",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = textColor
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(5, 10, 15, 30).forEach { mins ->
                                val selected = uiState.selectedDurationMinutes == mins
                                FilterChip(
                                    selected = selected,
                                    onClick = { viewModel.setDuration(mins) },
                                    label = { Text("$mins mins") },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = if (isDark) CyberCyan else ElectricBlue,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        RoboticsPrimaryButton(
                            text = if (uiState.isCreatingSession) "Starting..." else "🚀 Start Session & Show QR",
                            onClick = {
                                viewModel.startSession(
                                    labName = uiState.labNameInput,
                                    durationMinutes = uiState.selectedDurationMinutes
                                )
                            },
                            isLoading = uiState.isCreatingSession,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        // 3. Today's Attendance Records Section
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
                        Column {
                            Text(
                                text = "Today's Attendance Logs",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = textColor
                            )
                            Text(
                                text = "${uiState.records.size} total entries",
                                style = MaterialTheme.typography.bodySmall,
                                color = textSecColor
                            )
                        }

                        // Checked-In / Completed Badges
                        val checkedInCount = uiState.records.count { it.status == AttendanceRecord.STATUS_CHECKED_IN }
                        val completedCount = uiState.records.count { it.status == AttendanceRecord.STATUS_COMPLETED }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(CircuitWarning.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "$checkedInCount Active",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = CircuitWarning
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(CircuitSuccess.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "$completedCount Done",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = CircuitSuccess
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Search Input
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = viewModel::setSearchQuery,
                        placeholder = { Text("Search by name or student ID...", style = MaterialTheme.typography.bodySmall) },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Search, contentDescription = "Search", modifier = Modifier.size(18.dp))
                        },
                        trailingIcon = {
                            if (uiState.searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (isDark) CyberCyan else ElectricBlue,
                            unfocusedBorderColor = borderColor
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Status Filter Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("All", "Checked In", "Completed").forEach { status ->
                            val selected = uiState.statusFilter == status
                            FilterChip(
                                selected = selected,
                                onClick = { viewModel.setStatusFilter(status) },
                                label = { Text(status, style = MaterialTheme.typography.labelSmall) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = if (isDark) CyberCyan else ElectricBlue,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }
        }

        // 4. Records List
        if (uiState.filteredRecords.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "No Records",
                            modifier = Modifier.size(44.dp),
                            tint = textSecColor.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (uiState.records.isEmpty()) "No attendance records recorded today yet." else "No records match your filter.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = textSecColor
                        )
                    }
                }
            }
        } else {
            items(uiState.filteredRecords, key = { it.recordId }) { record ->
                AttendanceRecordItem(record = record, isDark = isDark)
            }
        }
    }
}

@Composable
fun AttendanceRecordItem(
    record: AttendanceRecord,
    isDark: Boolean
) {
    val surfaceColor = if (isDark) DarkSurfaceElevated else LightSurfaceElevated
    val borderColor = if (isDark) DarkSurfaceBorder else LightSurfaceBorder
    val textColor = if (isDark) TextPrimaryDark else TextPrimaryLight
    val textSecColor = if (isDark) TextSecondaryDark else TextSecondaryLight
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    val isCompleted = record.status == AttendanceRecord.STATUS_COMPLETED

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceColor),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = record.fullName,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = textColor
                )
                Text(
                    text = "${record.studentId} • ${record.labName}",
                    style = MaterialTheme.typography.labelSmall,
                    color = textSecColor
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "In: ${timeFormat.format(Date(record.checkInTime))}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = textColor
                    )

                    if (record.checkOutTime != null) {
                        Text(
                            text = "Out: ${timeFormat.format(Date(record.checkOutTime))}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = textColor
                        )
                        Text(
                            text = "⏱ ${record.formattedDuration}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) CyberCyan else ElectricBlue
                            )
                        )
                    }
                }
            }

            // Status Chip
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (isCompleted) CircuitSuccess.copy(alpha = 0.15f)
                        else CircuitWarning.copy(alpha = 0.15f)
                    )
                    .border(
                        1.dp,
                        if (isCompleted) CircuitSuccess.copy(alpha = 0.4f) else CircuitWarning.copy(alpha = 0.4f),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = record.status,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    ),
                    color = if (isCompleted) CircuitSuccess else CircuitWarning
                )
            }
        }
    }
}
