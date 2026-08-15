package com.roboticswala.hub.ui.screens.admin.tabs

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PrecisionManufacturing
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.roboticswala.hub.data.models.LabBooking
import com.roboticswala.hub.ui.components.BookingDetailDialog
import com.roboticswala.hub.ui.components.RoboticsOutlinedButton
import com.roboticswala.hub.ui.components.RoboticsPrimaryButton
import com.roboticswala.hub.ui.components.RoboticsTextField
import com.roboticswala.hub.ui.screens.admin.AdminBookingViewModel
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun AdminBookingsScreen(
    viewModel: AdminBookingViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val surfaceColor = if (isDark) DarkSurface else LightSurface
    val elevatedColor = if (isDark) DarkSurfaceElevated else LightSurfaceElevated
    val borderColor = if (isDark) DarkSurfaceBorder else LightSurfaceBorder
    val textColor = if (isDark) TextPrimaryDark else TextPrimaryLight
    val textSecColor = if (isDark) TextSecondaryDark else TextSecondaryLight

    var rejectReasonInput by remember { mutableStateOf("") }

    // Date Filters
    val dateFilters = remember {
        val list = mutableListOf("All")
        val cal = Calendar.getInstance()
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        for (i in 0..6) {
            list.add(format.format(cal.time))
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }
        list
    }

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
                    text = "Lab Station & Slot Bookings",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = textColor
                )
                Text(
                    text = "Review and approve student reservations for testing bays & equipment",
                    style = MaterialTheme.typography.bodySmall,
                    color = textSecColor
                )
            }
        }

        // 2. SEARCH BAR
        item {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::setSearchQuery,
                placeholder = { Text("Search by Student Name, ID, or Project...", fontSize = 13.sp) },
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

        // 3. STATUS FILTER CHIPS
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val statuses = listOf("All", "Pending", "Approved", "Rejected", "Cancelled")
                items(statuses) { status ->
                    val isSelected = uiState.statusFilter == status
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setStatusFilter(status) },
                        label = { Text(status, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = if (isDark) CyberCyan else ElectricBlue,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // 4. DATE FILTER
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(dateFilters) { date ->
                    val isSelected = uiState.dateFilter == date
                    val isToday = date == BookingTimeUtils.getTodayDateString()
                    val label = when {
                        date == "All" -> "All Dates"
                        isToday -> "Today (${date.takeLast(5)})"
                        else -> date.takeLast(5)
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isSelected) {
                                    if (isDark) CyberCyan.copy(alpha = 0.25f) else ElectricBlue.copy(alpha = 0.2f)
                                } else {
                                    elevatedColor
                                }
                            )
                            .border(
                                1.dp,
                                if (isSelected) (if (isDark) CyberCyan else ElectricBlue) else borderColor,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { viewModel.setDateFilter(date) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) (if (isDark) CyberCyan else ElectricBlue) else textColor
                        )
                    }
                }
            }
        }

        // 5. BOOKINGS COUNT
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Requests (${uiState.filteredBookings.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = textColor
                )

                if (uiState.isProcessing) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                }
            }
        }

        // 6. BOOKINGS LIST
        if (uiState.filteredBookings.isEmpty()) {
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
                        Icon(
                            imageVector = Icons.Default.BookmarkBorder,
                            contentDescription = null,
                            tint = textSecColor,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No Booking Requests Found",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = textColor
                        )
                        Text(
                            text = "Matching filters: Status = ${uiState.statusFilter}, Date = ${uiState.dateFilter}",
                            style = MaterialTheme.typography.bodySmall,
                            color = textSecColor,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        } else {
            items(uiState.filteredBookings, key = { it.bookingId }) { booking ->
                AdminBookingCard(
                    booking = booking,
                    isDark = isDark,
                    onApprove = { viewModel.approveBooking(booking) },
                    onReject = { viewModel.promptRejectBooking(booking) },
                    onClick = { viewModel.openDetails(booking) }
                )
            }
        }
    }

    // DETAIL MODAL
    uiState.selectedBookingForDetails?.let { booking ->
        BookingDetailDialog(
            booking = booking,
            isAdmin = true,
            onDismiss = viewModel::closeDetails,
            onApproveBooking = { viewModel.approveBooking(booking) },
            onRejectBooking = { viewModel.promptRejectBooking(booking) }
        )
    }

    // REJECT REASON PROMPT DIALOG
    uiState.bookingToReject?.let { booking ->
        AlertDialog(
            onDismissRequest = viewModel::dismissRejectDialog,
            icon = {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = CircuitError,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "Reject Booking Request",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column {
                    Text(
                        text = "Provide a reason for rejecting '${booking.projectName}' by ${booking.studentName}:",
                        style = MaterialTheme.typography.bodySmall,
                        color = textSecColor
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    RoboticsTextField(
                        value = rejectReasonInput,
                        onValueChange = { rejectReasonInput = it },
                        label = "Rejection Reason",
                        placeholder = "e.g. Station undergoing maintenance / Time conflict"
                    )
                }
            },
            confirmButton = {
                RoboticsPrimaryButton(
                    text = "Confirm Reject",
                    onClick = {
                        viewModel.confirmRejectBooking(rejectReasonInput)
                        rejectReasonInput = ""
                    }
                )
            },
            dismissButton = {
                RoboticsOutlinedButton(
                    text = "Cancel",
                    onClick = viewModel::dismissRejectDialog
                )
            }
        )
    }

    // CONFLICT ERROR ALERT DIALOG
    uiState.conflictError?.let { conflictMsg ->
        AlertDialog(
            onDismissRequest = viewModel::clearConflictError,
            icon = {
                Icon(
                    imageVector = Icons.Default.ErrorOutline,
                    contentDescription = null,
                    tint = CircuitError,
                    modifier = Modifier.size(44.dp)
                )
            },
            title = {
                Text(
                    text = "Slot Overlap Conflict! ⚠️",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = CircuitError
                )
            },
            text = {
                Text(
                    text = conflictMsg,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor
                )
            },
            confirmButton = {
                RoboticsPrimaryButton(
                    text = "Understood",
                    onClick = viewModel::clearConflictError
                )
            }
        )
    }
}

@Composable
fun AdminBookingCard(
    booking: LabBooking,
    isDark: Boolean,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onClick: () -> Unit
) {
    val surfaceColor = if (isDark) DarkSurface else LightSurface
    val elevatedColor = if (isDark) DarkSurfaceElevated else LightSurfaceElevated
    val borderColor = if (isDark) DarkSurfaceBorder else LightSurfaceBorder
    val textColor = if (isDark) TextPrimaryDark else TextPrimaryLight
    val textSecColor = if (isDark) TextSecondaryDark else TextSecondaryLight

    val statusColor = when (booking.status) {
        LabBooking.STATUS_APPROVED -> CircuitSuccess
        LabBooking.STATUS_PENDING -> CircuitWarning
        LabBooking.STATUS_REJECTED -> CircuitError
        else -> textSecColor
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
            // Header: Student Name + Status Chip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = booking.studentName,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = textColor
                    )
                    Text(
                        text = "ID: ${booking.studentId} • Project: ${booking.projectName}",
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
                        text = booking.status.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Date & Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = if (isDark) CyberCyan else ElectricBlue,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = booking.bookingDate,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    color = textColor
                )

                Spacer(modifier = Modifier.width(12.dp))

                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = if (isDark) CyberCyan else ElectricBlue,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${booking.timeRangeDisplay} (${booking.durationHoursMinutes})",
                    style = MaterialTheme.typography.bodySmall,
                    color = textColor
                )
            }

            if (booking.requiredEquipment.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Equipment: ${booking.requiredEquipment}",
                    style = MaterialTheme.typography.labelSmall,
                    color = textSecColor,
                    maxLines = 1
                )
            }

            // Quick Approve / Reject for Pending Bookings
            if (booking.isPending) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RoboticsOutlinedButton(
                        text = "Reject",
                        onClick = onReject,
                        modifier = Modifier.weight(1f),
                        height = 40.dp
                    )

                    RoboticsPrimaryButton(
                        text = "✓ Approve",
                        onClick = onApprove,
                        modifier = Modifier.weight(1f),
                        height = 40.dp
                    )
                }
            }
        }
    }
}
