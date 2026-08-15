package com.roboticswala.hub.ui.screens.admin.tabs

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.roboticswala.hub.data.models.Equipment
import com.roboticswala.hub.data.models.EquipmentRequest
import com.roboticswala.hub.ui.screens.admin.equipment.AdminEquipmentRequestsViewModel
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
fun AdminEquipmentRequestsScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AdminEquipmentRequestsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isDark = isSystemInDarkTheme()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(if (isDark) DarkSurface else LightSurface)
    ) {
        // Top Bar
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
                    text = "Equipment Requests & Issues",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = if (isDark) TextPrimaryDark else TextPrimaryLight
                )
                Text(
                    text = "Approve requests, issue hardware, and process returns",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isDark) TextSecondaryDark else TextSecondaryLight
                )
            }
        }

        // Search & Filter
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Search by student name, ID, component...", fontSize = 13.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search",
                        tint = if (isDark) CyberCyan else ElectricBlue
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (isDark) CyberCyan else ElectricBlue,
                    unfocusedBorderColor = if (isDark) DarkSurfaceBorder else LightSurfaceBorder,
                    focusedContainerColor = if (isDark) DarkSurfaceElevated else LightSurfaceElevated,
                    unfocusedContainerColor = if (isDark) DarkSurfaceElevated else LightSurfaceElevated
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Status Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(bottom = 4.dp)
            ) {
                val statuses = listOf("All") + EquipmentRequest.ALL_STATUSES
                items(statuses) { st ->
                    val isSelected = uiState.statusFilter.equals(st, ignoreCase = true)
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setStatusFilter(st) },
                        label = { Text(st, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = if (isDark) CyberCyan.copy(alpha = 0.2f) else ElectricBlue.copy(alpha = 0.15f),
                            selectedLabelColor = if (isDark) CyberCyan else ElectricBlue
                        )
                    )
                }
            }
        }

        // Requests List
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = if (isDark) CyberCyan else ElectricBlue)
            }
        } else if (uiState.filteredRequests.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No equipment requests found", color = if (isDark) TextSecondaryDark else TextSecondaryLight)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.filteredRequests, key = { it.requestId }) { req ->
                    AdminRequestCard(
                        request = req,
                        isDark = isDark,
                        onApprove = { viewModel.approveRequest(req.requestId) },
                        onReject = { viewModel.openRejectDialog(req) },
                        onIssue = { viewModel.openIssueDialog(req) },
                        onReturn = { viewModel.openReturnDialog(req) }
                    )
                }
            }
        }
    }

    // ── Reject Dialog ─────────────────────────────────────────────────────────
    if (uiState.isRejecting && uiState.selectedRequestForAction != null) {
        var reason by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { viewModel.closeActionDialog() },
            title = { Text("Reject Request", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Enter rejection reason for student:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = reason,
                        onValueChange = { reason = it },
                        placeholder = { Text("e.g. Component currently reserved for competition team") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.rejectRequest(uiState.selectedRequestForAction!!.requestId, reason) },
                    enabled = reason.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = CircuitError)
                ) {
                    Text("Reject Request", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.closeActionDialog() }) {
                    Text("Cancel")
                }
            }
        )
    }

    // ── Issue Equipment Dialog ────────────────────────────────────────────────
    if (uiState.isIssuing && uiState.selectedRequestForAction != null) {
        val req = uiState.selectedRequestForAction!!
        var qty by remember { mutableIntStateOf(req.requestedQuantity) }
        AlertDialog(
            onDismissRequest = { viewModel.closeActionDialog() },
            title = { Text("Issue Equipment", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (uiState.errorMessage != null) {
                        Text(text = uiState.errorMessage!!, color = CircuitError, fontSize = 12.sp)
                    }
                    Text(text = "Component: ${req.equipmentName}", fontWeight = FontWeight.Bold)
                    Text(text = "Student: ${req.studentName} (${req.studentId})")
                    Text(text = "Requested: ${req.requestedQuantity} unit(s) • Return: ${req.expectedReturnDate}")

                    OutlinedTextField(
                        value = qty.toString(),
                        onValueChange = { qty = it.toIntOrNull() ?: 1 },
                        label = { Text("Actual Quantity to Issue") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.confirmIssue(qty) },
                    enabled = !uiState.isActionLoading && qty > 0,
                    colors = ButtonDefaults.buttonColors(containerColor = CircuitSuccess, contentColor = Color.Black)
                ) {
                    if (uiState.isActionLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.Black)
                    } else {
                        Text("Confirm Issue (Reduce Stock)", fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.closeActionDialog() }) {
                    Text("Cancel")
                }
            }
        )
    }

    // ── Return Equipment Dialog ───────────────────────────────────────────────
    if (uiState.isReturning && uiState.selectedRequestForAction != null) {
        val req = uiState.selectedRequestForAction!!
        var condition by remember { mutableStateOf(Equipment.CONDITION_GOOD) }
        var notes by remember { mutableStateOf("") }
        var restoreStock by remember { mutableStateOf(true) }

        AlertDialog(
            onDismissRequest = { viewModel.closeActionDialog() },
            title = { Text("Process Equipment Return", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (uiState.errorMessage != null) {
                        Text(text = uiState.errorMessage!!, color = CircuitError, fontSize = 12.sp)
                    }
                    Text(text = "${req.equipmentName} (${req.actualIssuedQuantity.takeIf { it > 0 } ?: req.requestedQuantity} units)", fontWeight = FontWeight.Bold)
                    Text(text = "Returned by: ${req.studentName} (${req.studentId})")

                    Text("Return Condition:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(Equipment.ALL_CONDITIONS) { cond ->
                            FilterChip(
                                selected = condition == cond,
                                onClick = {
                                    condition = cond
                                    restoreStock = (cond != Equipment.CONDITION_DAMAGED && cond != Equipment.CONDITION_NEEDS_REPAIR)
                                },
                                label = { Text(cond, fontSize = 10.sp) }
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = restoreStock, onCheckedChange = { restoreStock = it })
                        Text("Restore quantity to available inventory", fontSize = 12.sp)
                    }

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Return Notes / Inspection Remarks") },
                        placeholder = { Text("e.g. Pin header intact, tested working") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.confirmReturn(condition, notes, restoreStock) },
                    enabled = !uiState.isActionLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = if (isDark) CyberCyan else ElectricBlue, contentColor = Color.Black)
                ) {
                    if (uiState.isActionLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.Black)
                    } else {
                        Text("Confirm Return", fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.closeActionDialog() }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun AdminRequestCard(
    request: EquipmentRequest,
    isDark: Boolean,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onIssue: () -> Unit,
    onReturn: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = if (request.isOverdue) CircuitError.copy(alpha = 0.6f)
                else if (isDark) DarkSurfaceBorder else LightSurfaceBorder,
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
                Text(
                    text = request.equipmentName,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (isDark) TextPrimaryDark else TextPrimaryLight
                )

                // Status Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            when (request.status.lowercase()) {
                                "issued" -> CircuitSuccess.copy(alpha = 0.15f)
                                "approved" -> Color(0xFF0066FF).copy(alpha = 0.15f)
                                "returned" -> Color(0xFF64748B).copy(alpha = 0.2f)
                                "overdue" -> CircuitError.copy(alpha = 0.2f)
                                "rejected" -> CircuitError.copy(alpha = 0.15f)
                                else -> CircuitWarning.copy(alpha = 0.15f)
                            }
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = request.status.uppercase(),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (request.status.lowercase()) {
                            "issued" -> CircuitSuccess
                            "approved" -> Color(0xFF3385FF)
                            "returned" -> Color(0xFF94A3B8)
                            "overdue" -> CircuitError
                            "rejected" -> CircuitError
                            else -> CircuitWarning
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "👤 ${request.studentName} (${request.studentId}) • Qty: ${request.requestedQuantity}",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isDark) CyberCyan else ElectricBlue
            )

            Text(
                text = "Purpose: ${request.purpose} • Return Date: 📅 ${request.expectedReturnDate}",
                fontSize = 11.sp,
                color = if (request.isOverdue) CircuitError else if (isDark) TextSecondaryDark else TextSecondaryLight
            )

            if (request.isOverdue) {
                Text(
                    text = "⚠️ OVERDUE: Equipment has not been returned by expected date!",
                    fontSize = 11.sp,
                    color = CircuitError,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (request.isPending) {
                    Button(
                        onClick = onApprove,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0066FF)),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp).weight(1f)
                    ) {
                        Text("Approve", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = onReject,
                        colors = ButtonDefaults.buttonColors(containerColor = CircuitError.copy(alpha = 0.15f), contentColor = CircuitError),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp).weight(1f)
                    ) {
                        Text("Reject", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                } else if (request.isApproved) {
                    Button(
                        onClick = onIssue,
                        colors = ButtonDefaults.buttonColors(containerColor = CircuitSuccess, contentColor = Color.Black),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp).fillMaxWidth()
                    ) {
                        Text("Issue Equipment (Handover)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                } else if (request.isIssued || request.isOverdue) {
                    Button(
                        onClick = onReturn,
                        colors = ButtonDefaults.buttonColors(containerColor = if (isDark) CyberCyan else ElectricBlue, contentColor = Color.Black),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp).fillMaxWidth()
                    ) {
                        Text("Process Return (Inspect & Stock In)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
