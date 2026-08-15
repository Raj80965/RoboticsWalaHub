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
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.roboticswala.hub.data.models.Project
import com.roboticswala.hub.data.models.ProjectExpense
import com.roboticswala.hub.ui.screens.admin.budget.AdminBudgetViewModel
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
fun AdminBudgetScreen(
    modifier: Modifier = Modifier,
    viewModel: AdminBudgetViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isDark = isSystemInDarkTheme()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(if (isDark) DarkSurface else LightSurface)
    ) {
        // Header
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
            Text(
                text = "Budget & Expense Management",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = if (isDark) TextPrimaryDark else TextPrimaryLight
            )
            Text(
                text = "Project budget allocations, expense approvals & financial audit",
                style = MaterialTheme.typography.labelSmall,
                color = if (isDark) TextSecondaryDark else TextSecondaryLight
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Financial Summary KPI Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = if (isDark) CyberCyan.copy(alpha = 0.4f) else ElectricBlue.copy(alpha = 0.3f),
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
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Total Approved Budget", fontSize = 11.sp, color = if (isDark) TextSecondaryDark else TextSecondaryLight)
                            Text("₹${uiState.totalApprovedBudget.toInt()}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = if (isDark) CyberCyan else ElectricBlue)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Total Expenses Claimed", fontSize = 11.sp, color = if (isDark) TextSecondaryDark else TextSecondaryLight)
                            Text("₹${uiState.totalApprovedExpenses.toInt()}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = CircuitSuccess)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Remaining Budget: ₹${uiState.totalRemainingBudget.toInt()}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) TextPrimaryDark else TextPrimaryLight
                        )
                        Text(
                            text = "Pending: ${uiState.pendingApprovalsCount} (₹${uiState.totalPendingExpenses.toInt()})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CircuitWarning
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Search by student, project, title, or vendor...", fontSize = 13.sp) },
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

            Spacer(modifier = Modifier.height(6.dp))

            // Status Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(bottom = 4.dp)
            ) {
                val statuses = listOf("All") + ProjectExpense.ALL_STATUSES
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

        // List
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = if (isDark) CyberCyan else ElectricBlue)
            }
        } else if (uiState.filteredExpenses.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No expense claims found", color = if (isDark) TextSecondaryDark else TextSecondaryLight)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(uiState.filteredExpenses, key = { it.expenseId }) { item ->
                    AdminExpenseCard(
                        expense = item,
                        isDark = isDark,
                        onApprove = { viewModel.promptApproveExpense(item) },
                        onReject = { viewModel.openRejectDialog(item) }
                    )
                }
            }
        }
    }

    // ── Reject Dialog ─────────────────────────────────────────────────────────
    if (uiState.isRejecting && uiState.selectedExpenseForAction != null) {
        var reason by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { viewModel.closeActionDialog() },
            title = { Text("Reject Expense Claim", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Enter rejection reason for student:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = reason,
                        onValueChange = { reason = it },
                        placeholder = { Text("e.g. GST invoice not attached / unauthorized item") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.confirmReject(reason) },
                    enabled = reason.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = CircuitError)
                ) {
                    Text("Reject Claim", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.closeActionDialog() }) {
                    Text("Cancel")
                }
            }
        )
    }

    // ── Over Budget Warning Modal ─────────────────────────────────────────────
    if (uiState.isOverBudgetWarning && uiState.selectedExpenseForAction != null) {
        val exp = uiState.selectedExpenseForAction!!
        var exceptionReason by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { viewModel.closeActionDialog() },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Warning, contentDescription = null, tint = CircuitError)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Over-Budget Warning", fontWeight = FontWeight.Bold, color = CircuitError)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Approving ₹${exp.amount} for \"${exp.title}\" exceeds the project's approved budget!", fontSize = 13.sp)
                    Text(text = "To approve as an authorized financial exception, enter approval justification:", fontSize = 12.sp)
                    OutlinedTextField(
                        value = exceptionReason,
                        onValueChange = { exceptionReason = it },
                        placeholder = { Text("e.g. Authorized by HOD for National Competition") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.confirmOverBudgetApproval(exceptionReason) },
                    enabled = exceptionReason.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = CircuitError)
                ) {
                    Text("Approve Over-Budget Exception", color = Color.White, fontWeight = FontWeight.Bold)
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
private fun AdminExpenseCard(
    expense: ProjectExpense,
    isDark: Boolean,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
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
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = expense.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (isDark) TextPrimaryDark else TextPrimaryLight
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            when (expense.status.lowercase()) {
                                "approved" -> CircuitSuccess.copy(alpha = 0.15f)
                                "rejected" -> CircuitError.copy(alpha = 0.15f)
                                else -> CircuitWarning.copy(alpha = 0.15f)
                            }
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = expense.status.uppercase(),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (expense.status.lowercase()) {
                            "approved" -> CircuitSuccess
                            "rejected" -> CircuitError
                            else -> CircuitWarning
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "👤 ${expense.submittedByName} (${expense.submittedByStudentId}) • 📁 ${expense.projectName}",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isDark) CyberCyan else ElectricBlue
            )

            Text(
                text = "Category: ${expense.category} • Vendor: ${expense.vendorName.ifBlank { "N/A" }} • Date: ${expense.purchaseDate}",
                fontSize = 11.sp,
                color = if (isDark) TextSecondaryDark else TextSecondaryLight
            )

            if (expense.notes.isNotBlank()) {
                Text(
                    text = "Notes: ${expense.notes}",
                    fontSize = 11.sp,
                    color = if (isDark) TextSecondaryDark else TextSecondaryLight
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "₹${expense.amount}",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) CyberCyan else ElectricBlue
                )

                if (expense.isPending) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = onApprove,
                            colors = ButtonDefaults.buttonColors(containerColor = CircuitSuccess, contentColor = Color.Black),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text("Approve", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = onReject,
                            colors = ButtonDefaults.buttonColors(containerColor = CircuitError.copy(alpha = 0.15f), contentColor = CircuitError),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text("Reject", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
