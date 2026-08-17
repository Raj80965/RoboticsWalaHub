package com.roboticswala.hub.ui.screens.student.booking

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.roboticswala.hub.data.models.UserProfile
import com.roboticswala.hub.ui.components.RoboticsOutlinedButton
import com.roboticswala.hub.ui.components.RoboticsPrimaryButton
import com.roboticswala.hub.ui.components.RoboticsTextField
import com.roboticswala.hub.ui.theme.CircuitError
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateBookingDialog(
    studentProfile: UserProfile,
    isSubmitting: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onSubmit: (
        date: String,
        startTime: String,
        endTime: String,
        projectName: String,
        workDescription: String,
        requiredEquipment: String,
        teamMembers: String
    ) -> Unit
) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()
    val scrollState = rememberScrollState()

    val surfaceColor = if (isDark) DarkSurface else LightSurface
    val elevatedColor = if (isDark) DarkSurfaceElevated else LightSurfaceElevated
    val borderColor = if (isDark) DarkSurfaceBorder else LightSurfaceBorder
    val textColor = if (isDark) TextPrimaryDark else TextPrimaryLight
    val textSecColor = if (isDark) TextSecondaryDark else TextSecondaryLight

    val cal = remember { Calendar.getInstance() }

    var selectedDate by remember { mutableStateOf(BookingTimeUtils.getTodayDateString()) }
    var selectedStartTime by remember { mutableStateOf("10:00 AM") }
    var selectedEndTime by remember { mutableStateOf("12:00 PM") }

    var projectName by remember { mutableStateOf("") }
    var workDescription by remember { mutableStateOf("") }
    var requiredEquipment by remember { mutableStateOf("TurtleBot4, ROS2 Workstation") }
    var teamMembers by remember { mutableStateOf("") }

    val popularEquipment = listOf(
        "TurtleBot4 Mobile Robot",
        "3D Printer (Prusa MK4)",
        "SLAM & LiDAR Bench",
        "Laser Cutter",
        "Digital Oscilloscope",
        "Soldering Station"
    )

    // Date Picker Dialog Launcher
    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val formatted = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth)
                selectedDate = formatted
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = System.currentTimeMillis() - 1000
        }
    }

    // Start Time Picker
    val startTimePickerDialog = remember {
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                selectedStartTime = BookingTimeUtils.formatTime12Hour(hourOfDay, minute)
            },
            10, 0, false
        )
    }

    // End Time Picker
    val endTimePickerDialog = remember {
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                selectedEndTime = BookingTimeUtils.formatTime12Hour(hourOfDay, minute)
            },
            12, 0, false
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(22.dp)),
            color = surfaceColor,
            border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(scrollState)
                    .imePadding()
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Reserve Lab Station Slot",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = textColor
                        )
                        Text(
                            text = "Request bench time & hardware access",
                            style = MaterialTheme.typography.bodySmall,
                            color = textSecColor
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = textSecColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Error Message if any
                if (!errorMessage.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(CircuitError.copy(alpha = 0.15f))
                            .border(1.dp, CircuitError.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "⚠️ $errorMessage",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            color = CircuitError
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // 1. DATE SELECTOR
                Text(
                    text = "Booking Date *",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = textColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(elevatedColor)
                        .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                        .clickable { datePickerDialog.show() }
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = selectedDate,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = textColor
                            )
                            Text(
                                text = BookingTimeUtils.formatReadableDate(selectedDate),
                                style = MaterialTheme.typography.labelSmall,
                                color = textSecColor
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Pick Date",
                            tint = if (isDark) CyberCyan else ElectricBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 2. TIME RANGE SELECTORS
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Start Time
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Start Time *",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = textColor
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(elevatedColor)
                                .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                                .clickable { startTimePickerDialog.show() }
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = selectedStartTime,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = textColor
                                )
                                Icon(
                                    imageVector = Icons.Default.AccessTime,
                                    contentDescription = null,
                                    tint = if (isDark) CyberCyan else ElectricBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    // End Time
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "End Time *",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = textColor
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(elevatedColor)
                                .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                                .clickable { endTimePickerDialog.show() }
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = selectedEndTime,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = textColor
                                )
                                Icon(
                                    imageVector = Icons.Default.AccessTime,
                                    contentDescription = null,
                                    tint = if (isDark) CyberCyan else ElectricBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 3. PROJECT NAME
                RoboticsTextField(
                    value = projectName,
                    onValueChange = { projectName = it },
                    label = "Project Name *",
                    placeholder = "e.g. Autonomous Maze Solving Rover",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 4. WORK DESCRIPTION
                RoboticsTextField(
                    value = workDescription,
                    onValueChange = { workDescription = it },
                    label = "Work Description *",
                    placeholder = "e.g. Calibrating RPLIDAR A1 & testing SLAM navigation stack",
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 5. REQUIRED EQUIPMENT
                RoboticsTextField(
                    value = requiredEquipment,
                    onValueChange = { requiredEquipment = it },
                    label = "Required Equipment",
                    placeholder = "e.g. TurtleBot4, 3D Printer, Soldering Iron",
                    leadingIcon = Icons.Default.PrecisionManufacturing,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Equipment Quick Pick Chips
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    popularEquipment.forEach { item ->
                        val isSelected = requiredEquipment.contains(item)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                requiredEquipment = if (isSelected) {
                                    requiredEquipment.replace(item, "").replace(", ,", ",").trim(',', ' ')
                                } else {
                                    if (requiredEquipment.isBlank()) item else "$requiredEquipment, $item"
                                }
                            },
                            label = { Text(item, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = if (isDark) CyberCyan.copy(alpha = 0.25f) else ElectricBlue.copy(alpha = 0.2f),
                                selectedLabelColor = if (isDark) CyberCyan else ElectricBlue
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 6. TEAM MEMBERS
                RoboticsTextField(
                    value = teamMembers,
                    onValueChange = { teamMembers = it },
                    label = "Team Members (Optional)",
                    placeholder = "e.g. Aarav Sharma, Rohan Verma",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    RoboticsOutlinedButton(
                        text = "Cancel",
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    )

                    RoboticsPrimaryButton(
                        text = if (isSubmitting) "Submitting..." else "Submit Request",
                        onClick = {
                            onSubmit(
                                selectedDate,
                                selectedStartTime,
                                selectedEndTime,
                                projectName,
                                workDescription,
                                requiredEquipment,
                                teamMembers
                            )
                        },
                        isLoading = isSubmitting,
                        enabled = projectName.isNotBlank() && workDescription.isNotBlank(),
                        modifier = Modifier.weight(1.5f)
                    )
                }
            }
        }
    }
}
