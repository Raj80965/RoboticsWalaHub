package com.roboticswala.hub.ui.screens.student.expenses

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.roboticswala.hub.data.models.Project
import com.roboticswala.hub.data.models.ProjectBudgetSummary
import com.roboticswala.hub.data.models.ProjectExpense
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
import com.roboticswala.hub.utils.BookingTimeUtils

@Composable
fun StudentExpensesScreen(
    userProfile: UserProfile,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StudentExpensesViewModel = viewModel(
        factory = StudentExpensesViewModelFactory(userProfile.uid)
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val isDark = isSystemInDarkTheme()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(if (isDark) DarkSurface else LightSurface)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // ── Top Bar ───────────────────────────────────────────────────────
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
                        text = "Project Budget & Expenses",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = if (isDark) TextPrimaryDark else TextPrimaryLight
                    )
                    Text(
                        text = "Submit bills, track allocations & expense claims",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isDark) TextSecondaryDark else TextSecondaryLight
                    )
                }
            }

            // ── Project Budget Summary Card ───────────────────────────────────
            uiState.budgetSummary?.let { summary ->
                BudgetSummaryCard(summary = summary, isDark = isDark, modifier = Modifier.padding(horizontal = 16.dp))
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ── Project Selector & Filter Row ─────────────────────────────────
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                if (uiState.myProjects.isNotEmpty()) {
                    Text("Select Project:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        contentPadding = PaddingValues(top = 4.dp, bottom = 4.dp)
                    ) {
                        item {
                            FilterChip(
                                selected = uiState.selectedProjectId == "All",
                                onClick = { viewModel.setSelectedProject("All") },
                                label = { Text("All My Projects", fontSize = 11.sp) }
                            )
                        }
                        items(uiState.myProjects) { proj ->
                            FilterChip(
                                selected = uiState.selectedProjectId == proj.projectId,
                                onClick = { viewModel.setSelectedProject(proj.projectId) },
                                label = { Text(proj.title, fontSize = 11.sp) }
                            )
                        }
                    }
                }

                // Search Bar
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("Search expense, vendor, receipt...", fontSize = 13.sp) },
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

                // Status Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(bottom = 6.dp)
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

            // ── Expenses List ─────────────────────────────────────────────────
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = if (isDark) CyberCyan else ElectricBlue)
                }
            } else if (uiState.filteredExpenses.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Filled.Description,
                            contentDescription = null,
                            tint = if (isDark) TextSecondaryDark else TextSecondaryLight,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No expenses submitted yet",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (isDark) TextPrimaryDark else TextPrimaryLight
                        )
                        Text(
                            text = "Tap + below to submit component purchase bills",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isDark) TextSecondaryDark else TextSecondaryLight
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(uiState.filteredExpenses, key = { it.expenseId }) { item ->
                        StudentExpenseCard(
                            expense = item,
                            isDark = isDark,
                            onClick = { viewModel.openDetails(item) },
                            onEdit = { viewModel.openEditDialog(item) },
                            onDelete = { viewModel.promptDelete(item) }
                        )
                    }
                }
            }
        }

        // ── FAB Add Expense ───────────────────────────────────────────────────
        FloatingActionButton(
            onClick = { viewModel.openAddDialog() },
            containerColor = if (isDark) CyberCyan else ElectricBlue,
            contentColor = if (isDark) Color.Black else Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
        ) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = "Add Expense")
        }
    }

    // ── Add / Edit Expense Dialog ─────────────────────────────────────────────
    if (uiState.showAddDialog) {
        AddExpenseDialog(
            existing = uiState.expenseToEdit,
            projects = uiState.myProjects,
            isDark = isDark,
            isLoading = uiState.isActionLoading,
            errorMessage = uiState.errorMessage,
            onDismiss = { viewModel.closeAddDialog() },
            onSubmit = { projId, title, cat, desc, amt, pDate, vendor, notes, rBytes, rName ->
                viewModel.submitExpense(userProfile, projId, title, cat, desc, amt, pDate, vendor, notes, rBytes, rName)
            }
        )
    }

    // ── Expense Details Modal ─────────────────────────────────────────────────
    uiState.selectedExpenseDetails?.let { exp ->
        AlertDialog(
            onDismissRequest = { viewModel.closeDetails() },
            title = {
                Text(
                    text = exp.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = "Amount: ₹${exp.amount} • ${exp.category}", fontWeight = FontWeight.Bold, color = if (isDark) CyberCyan else ElectricBlue)
                    Text(text = "Project: ${exp.projectName}", fontSize = 12.sp)
                    Text(text = "Vendor / Shop: ${exp.vendorName.ifBlank { "Unspecified" }}", fontSize = 12.sp)
                    Text(text = "Purchase Date: ${exp.purchaseDate}", fontSize = 12.sp)
                    Text(text = "Status: ${exp.status}", fontWeight = FontWeight.Bold, color = if (exp.isApproved) CircuitSuccess else if (exp.isRejected) CircuitError else CircuitWarning)

                    if (exp.receiptFileName.isNotBlank()) {
                        Text(text = "📄 Receipt: ${exp.receiptFileName}", fontSize = 12.sp, color = if (isDark) CyberCyan else ElectricBlue)
                    }

                    if (exp.isRejected && exp.rejectionReason.isNotBlank()) {
                        Text(text = "Rejection Reason: ${exp.rejectionReason}", fontSize = 12.sp, color = CircuitError)
                    }

                    if (exp.notes.isNotBlank()) {
                        Text(text = "Notes: ${exp.notes}", fontSize = 12.sp, color = if (isDark) TextSecondaryDark else TextSecondaryLight)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.closeDetails() }) {
                    Text("Close")
                }
            }
        )
    }

    // ── Delete Confirmation Modal ─────────────────────────────────────────────
    uiState.expenseToDelete?.let { exp ->
        AlertDialog(
            onDismissRequest = { viewModel.dismissDelete() },
            title = { Text("Delete Expense", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete pending expense \"${exp.title}\" (₹${exp.amount})?") },
            confirmButton = {
                Button(
                    onClick = { viewModel.confirmDelete() },
                    colors = ButtonDefaults.buttonColors(containerColor = CircuitError)
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissDelete() }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun BudgetSummaryCard(
    summary: ProjectBudgetSummary,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val effectiveBudget = if (summary.approvedBudget > 0) summary.approvedBudget else summary.estimatedBudget

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = if (summary.healthStatus == "Over Budget") CircuitError.copy(alpha = 0.6f)
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
                    text = summary.projectTitle,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (isDark) TextPrimaryDark else TextPrimaryLight,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            when (summary.healthStatus) {
                                "Over Budget" -> CircuitError.copy(alpha = 0.15f)
                                "Near Limit", "Warning" -> CircuitWarning.copy(alpha = 0.15f)
                                else -> CircuitSuccess.copy(alpha = 0.15f)
                            }
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = summary.healthStatus.uppercase(),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (summary.healthStatus) {
                            "Over Budget" -> CircuitError
                            "Near Limit", "Warning" -> CircuitWarning
                            else -> CircuitSuccess
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Budget Allocated", fontSize = 10.sp, color = if (isDark) TextSecondaryDark else TextSecondaryLight)
                    Text("₹${effectiveBudget.toInt()}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (isDark) CyberCyan else ElectricBlue)
                }
                Column {
                    Text("Total Approved", fontSize = 10.sp, color = if (isDark) TextSecondaryDark else TextSecondaryLight)
                    Text("₹${summary.totalApprovedExpenses.toInt()}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = CircuitSuccess)
                }
                Column {
                    Text("Remaining", fontSize = 10.sp, color = if (isDark) TextSecondaryDark else TextSecondaryLight)
                    Text("₹${summary.remainingBudget.toInt()}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (summary.remainingBudget <= 0) CircuitError else if (isDark) TextPrimaryDark else TextPrimaryLight)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Utilization progress bar
            val progress = (summary.utilizationPercentage / 100f).toFloat().coerceIn(0f, 1f)
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (summary.healthStatus == "Over Budget") CircuitError else if (summary.healthStatus == "Warning") CircuitWarning else CircuitSuccess,
                trackColor = if (isDark) DarkSurfaceBorder else LightSurfaceBorder
            )

            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Utilization: ${String.format("%.1f", summary.utilizationPercentage)}%", fontSize = 10.sp, color = if (isDark) TextSecondaryDark else TextSecondaryLight)
                if (summary.totalPendingExpenses > 0) {
                    Text(text = "Pending: ₹${summary.totalPendingExpenses.toInt()}", fontSize = 10.sp, color = CircuitWarning, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun StudentExpenseCard(
    expense: ProjectExpense,
    isDark: Boolean,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isDark) CyberCyan.copy(alpha = 0.15f) else ElectricBlue.copy(alpha = 0.12f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(text = expense.category, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isDark) CyberCyan else ElectricBlue)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = expense.purchaseDate, fontSize = 11.sp, color = if (isDark) TextSecondaryDark else TextSecondaryLight)
                }

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

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = expense.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = if (isDark) TextPrimaryDark else TextPrimaryLight
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "📁 ${expense.projectName} • Vendor: ${expense.vendorName.ifBlank { "N/A" }}",
                fontSize = 11.sp,
                color = if (isDark) TextSecondaryDark else TextSecondaryLight
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "₹${expense.amount}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) CyberCyan else ElectricBlue
                )

                if (expense.isPending) {
                    Row {
                        IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Filled.Edit, contentDescription = "Edit", tint = if (isDark) CyberCyan else ElectricBlue, modifier = Modifier.size(16.dp))
                        }
                        IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = CircuitError, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AddExpenseDialog(
    existing: ProjectExpense?,
    projects: List<Project>,
    isDark: Boolean,
    isLoading: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onSubmit: (String, String, String, String, Double, String, String, String, ByteArray?, String?) -> Unit
) {
    var selectedProjId by remember { mutableStateOf(existing?.projectId ?: (projects.firstOrNull()?.projectId ?: "")) }
    var title by remember { mutableStateOf(existing?.title ?: "") }
    var category by remember { mutableStateOf(existing?.category ?: ProjectExpense.CATEGORY_COMPONENTS) }
    var description by remember { mutableStateOf(existing?.description ?: "") }
    var amountText by remember { mutableStateOf(existing?.amount?.takeIf { it > 0 }?.toString() ?: "") }
    var purchaseDate by remember { mutableStateOf(existing?.purchaseDate ?: BookingTimeUtils.getTodayDateString()) }
    var vendor by remember { mutableStateOf(existing?.vendorName ?: "") }
    var notes by remember { mutableStateOf(existing?.notes ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (existing != null) "Edit Expense" else "Submit Project Expense",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (errorMessage != null) {
                    Text(text = errorMessage, color = CircuitError, fontSize = 12.sp)
                }

                if (projects.isNotEmpty() && existing == null) {
                    Text("Select Project *", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(projects) { p ->
                            FilterChip(
                                selected = selectedProjId == p.projectId,
                                onClick = { selectedProjId = p.projectId },
                                label = { Text(p.title, fontSize = 10.sp) }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Expense Title *") },
                    placeholder = { Text("e.g. 4x MG996R Metal Gear Servos") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Category selector
                Text("Category *", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(ProjectExpense.ALL_CATEGORIES) { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, fontSize = 10.sp) }
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { amountText = it },
                        label = { Text("Amount (₹) *") },
                        placeholder = { Text("1250.0") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = purchaseDate,
                        onValueChange = { purchaseDate = it },
                        label = { Text("Purchase Date *") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = vendor,
                    onValueChange = { vendor = it },
                    label = { Text("Vendor / Shop Name (Optional)") },
                    placeholder = { Text("Robu.in / Local Electronics") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Bill / Receipt & Notes") },
                    placeholder = { Text("Invoice #RB-29384, GST Paid") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountText.toDoubleOrNull() ?: 0.0
                    onSubmit(selectedProjId, title, category, description, amt, purchaseDate, vendor, notes, null, null)
                },
                enabled = !isLoading && selectedProjId.isNotBlank() && title.isNotBlank() && amountText.toDoubleOrNull() != null && (amountText.toDoubleOrNull() ?: 0.0) > 0.0,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDark) CyberCyan else ElectricBlue,
                    contentColor = if (isDark) Color.Black else Color.White
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                } else {
                    Text("Submit Expense", fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
