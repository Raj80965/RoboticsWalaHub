package com.roboticswala.hub.ui.screens.student.work

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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
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
import com.roboticswala.hub.data.models.DailyWorkUpdate
import com.roboticswala.hub.ui.components.RoboticsOutlinedButton
import com.roboticswala.hub.ui.components.RoboticsPrimaryButton
import com.roboticswala.hub.ui.components.RoboticsTextField
import com.roboticswala.hub.ui.theme.CircuitError
import com.roboticswala.hub.ui.theme.CircuitSuccess
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
fun CreateWorkUpdateDialog(
    isLogging: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onSubmit: (
        title: String,
        description: String,
        projectName: String,
        hoursWorked: Double,
        problemsFaced: String,
        nextSteps: String,
        workDate: String
    ) -> Unit
) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()
    val textColor = if (isDark) TextPrimaryDark else TextPrimaryLight
    val textSecColor = if (isDark) TextSecondaryDark else TextSecondaryLight
    val elevatedColor = if (isDark) DarkSurfaceElevated else LightSurfaceElevated

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var projectName by remember { mutableStateOf("") }
    var hoursWorked by remember { mutableDoubleStateOf(2.5) }
    var problemsFaced by remember { mutableStateOf("") }
    var nextSteps by remember { mutableStateOf("") }
    var workDate by remember { mutableStateOf(BookingTimeUtils.getTodayDateString()) }

    val cal = remember { Calendar.getInstance() }
    val datePicker = remember {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                val formatted = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, day)
                workDate = formatted
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.maxDate = System.currentTimeMillis() // No future dates allowed
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Log Daily Lab Work Progress",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .imePadding(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!errorMessage.isNullOrBlank()) {
                    Text(text = "⚠️ $errorMessage", color = CircuitError, fontSize = 12.sp)
                }

                RoboticsTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = "Work Title *",
                    placeholder = "e.g. SLAM LiDAR sensor calibration & filter tuning"
                )

                RoboticsTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = "Work Description *",
                    placeholder = "What hardware/software tasks were completed?",
                    singleLine = false
                )

                RoboticsTextField(
                    value = projectName,
                    onValueChange = { projectName = it },
                    label = "Related Project (Optional)",
                    placeholder = "e.g. Autonomous SLAM Maze Rover"
                )

                // Date Picker row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Work Date", style = MaterialTheme.typography.labelSmall, color = textSecColor)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(elevatedColor)
                                .clickable { datePicker.show() }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(text = "📅 $workDate", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = textColor)
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "Hours Worked: ${String.format(Locale.getDefault(), "%.1f", hoursWorked)} hrs", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = textColor)
                        Slider(
                            value = hoursWorked.toFloat(),
                            onValueChange = { hoursWorked = (it * 2).toInt() / 2.0 },
                            valueRange = 0.5f..12.0f,
                            modifier = Modifier.width(160.dp),
                            colors = SliderDefaults.colors(
                                thumbColor = if (isDark) CyberCyan else ElectricBlue,
                                activeTrackColor = if (isDark) CyberCyan else ElectricBlue
                            )
                        )
                    }
                }

                RoboticsTextField(
                    value = problemsFaced,
                    onValueChange = { problemsFaced = it },
                    label = "Problems / Blockers (Optional)",
                    placeholder = "e.g. UART baud rate mismatch with motor driver"
                )

                RoboticsTextField(
                    value = nextSteps,
                    onValueChange = { nextSteps = it },
                    label = "Next Steps (Optional)",
                    placeholder = "e.g. Test waypoint navigation in obstacle field"
                )
            }
        },
        confirmButton = {
            RoboticsPrimaryButton(
                text = if (isLogging) "Saving..." else "Submit Daily Work",
                onClick = {
                    onSubmit(title, description, projectName, hoursWorked, problemsFaced, nextSteps, workDate)
                },
                isLoading = isLogging,
                enabled = title.isNotBlank() && description.isNotBlank() && hoursWorked > 0.0
            )
        },
        dismissButton = {
            RoboticsOutlinedButton(text = "Cancel", onClick = onDismiss)
        }
    )
}

@Composable
fun DailyWorkDetailDialog(
    update: DailyWorkUpdate,
    isOwner: Boolean,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val textColor = if (isDark) TextPrimaryDark else TextPrimaryLight
    val textSecColor = if (isDark) TextSecondaryDark else TextSecondaryLight
    val elevatedColor = if (isDark) DarkSurfaceElevated else LightSurfaceElevated

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = update.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isDark) CyberCyan.copy(alpha = 0.2f) else ElectricBlue.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${update.hoursWorked} hrs",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) CyberCyan else ElectricBlue
                    )
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Logged by ${update.studentName} (${update.studentId}) • 📅 ${update.workDate}",
                    style = MaterialTheme.typography.labelSmall,
                    color = textSecColor
                )

                if (update.relatedProjectName.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(elevatedColor)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(text = "Project: ${update.relatedProjectName}", fontSize = 11.sp, color = textColor)
                    }
                }

                Text(
                    text = update.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor,
                    lineHeight = 20.sp
                )

                if (update.problemsFaced.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(CircuitError.copy(alpha = 0.1f))
                            .padding(8.dp)
                    ) {
                        Text(text = "⚠️ Blockers: ${update.problemsFaced}", style = MaterialTheme.typography.bodySmall, color = CircuitError)
                    }
                }

                if (update.nextSteps.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(elevatedColor)
                            .padding(8.dp)
                    ) {
                        Text(text = "➔ Next Steps: ${update.nextSteps}", style = MaterialTheme.typography.bodySmall, color = if (isDark) CyberCyan else ElectricBlue)
                    }
                }
            }
        },
        confirmButton = {
            RoboticsPrimaryButton(text = "Close", onClick = onDismiss)
        },
        dismissButton = {
            if (isOwner) {
                RoboticsOutlinedButton(
                    text = "Delete Entry",
                    onClick = onDelete
                )
            }
        }
    )
}
