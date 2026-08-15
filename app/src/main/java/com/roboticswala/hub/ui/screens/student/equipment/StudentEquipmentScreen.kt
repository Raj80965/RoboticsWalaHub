package com.roboticswala.hub.ui.screens.student.equipment

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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Remove
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.roboticswala.hub.data.models.Equipment
import com.roboticswala.hub.data.models.EquipmentRequest
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
fun StudentEquipmentScreen(
    userProfile: UserProfile,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StudentEquipmentViewModel = viewModel(
        factory = StudentEquipmentViewModelFactory(userProfile.uid)
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val isDark = isSystemInDarkTheme()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(if (isDark) DarkSurface else LightSurface)
    ) {
        // ── Top Bar ───────────────────────────────────────────────────────────
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
                    text = "Lab Equipment & Components",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = if (isDark) TextPrimaryDark else TextPrimaryLight
                )
                Text(
                    text = "Browse hardware inventory & request components",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isDark) TextSecondaryDark else TextSecondaryLight
                )
            }
        }

        // ── Tabs (Catalog vs My Requests) ─────────────────────────────────────
        TabRow(
            selectedTabIndex = uiState.selectedTab,
            containerColor = if (isDark) DarkSurfaceElevated else LightSurfaceElevated,
            contentColor = if (isDark) CyberCyan else ElectricBlue
        ) {
            Tab(
                selected = uiState.selectedTab == 0,
                onClick = { viewModel.selectTab(0) },
                text = { Text("Hardware Catalog (${uiState.equipmentList.size})", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = uiState.selectedTab == 1,
                onClick = { viewModel.selectTab(1) },
                text = { Text("My Requests (${uiState.myRequests.size})", fontWeight = FontWeight.Bold) }
            )
        }

        if (uiState.selectedTab == 0) {
            // ── Tab 0: Equipment Catalog ──────────────────────────────────────
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("Search sensors, motors, controllers...", fontSize = 13.sp) },
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

                // Category Filter Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(bottom = 4.dp)
                ) {
                    val cats = listOf("All") + Equipment.ALL_CATEGORIES
                    items(cats) { cat ->
                        val isSelected = uiState.categoryFilter.equals(cat, ignoreCase = true)
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setCategoryFilter(cat) },
                            label = { Text(cat, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = if (isDark) CyberCyan.copy(alpha = 0.2f) else ElectricBlue.copy(alpha = 0.15f),
                                selectedLabelColor = if (isDark) CyberCyan else ElectricBlue
                            )
                        )
                    }
                }
            }

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = if (isDark) CyberCyan else ElectricBlue)
                }
            } else if (uiState.filteredEquipment.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "No equipment found matching criteria", color = if (isDark) TextSecondaryDark else TextSecondaryLight)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(uiState.filteredEquipment, key = { it.equipmentId }) { item ->
                        StudentEquipmentCard(
                            equipment = item,
                            isDark = isDark,
                            onRequestClick = { viewModel.openRequestDialog(item) },
                            onDetailsClick = { viewModel.openDetails(item) }
                        )
                    }
                }
            }
        } else {
            // ── Tab 1: My Requests ────────────────────────────────────────────
            if (uiState.myRequests.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Filled.Inventory2,
                            contentDescription = null,
                            tint = if (isDark) TextSecondaryDark else TextSecondaryLight,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No equipment requests yet",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (isDark) TextPrimaryDark else TextPrimaryLight
                        )
                        Text(
                            text = "Browse the catalog to request lab components",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isDark) TextSecondaryDark else TextSecondaryLight
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(uiState.myRequests, key = { it.requestId }) { req ->
                        StudentRequestCard(
                            request = req,
                            isDark = isDark,
                            onCancelClick = { viewModel.cancelRequest(req.requestId) }
                        )
                    }
                }
            }
        }
    }

    // ── Request Equipment Modal ───────────────────────────────────────────────
    uiState.selectedEquipmentForRequest?.let { eq ->
        RequestEquipmentDialog(
            equipment = eq,
            isDark = isDark,
            isLoading = uiState.isActionLoading,
            errorMessage = uiState.errorMessage,
            onDismiss = { viewModel.closeRequestDialog() },
            onSubmit = { qty, purpose, retDate, proj ->
                viewModel.submitRequest(userProfile, qty, purpose, retDate, proj)
            }
        )
    }

    // ── Equipment Details Modal ───────────────────────────────────────────────
    uiState.selectedEquipmentForDetails?.let { eq ->
        AlertDialog(
            onDismissRequest = { viewModel.closeDetails() },
            title = {
                Text(
                    text = eq.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (isDark) TextPrimaryDark else TextPrimaryLight
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = "Category: ${eq.category} • Condition: ${eq.condition}", fontSize = 12.sp, color = if (isDark) CyberCyan else ElectricBlue)
                    Text(text = "📍 Storage Location: ${eq.storageLocation.ifBlank { "Main Bay Cabinet" }}", fontSize = 12.sp)
                    Text(
                        text = "Stock: ${eq.availableQuantity} available / ${eq.totalQuantity} total (${eq.issuedQuantity} issued)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (eq.isOutOfStock) CircuitError else if (eq.isLowStock) CircuitWarning else CircuitSuccess
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = eq.description.ifBlank { "Standard lab robotics component." }, fontSize = 13.sp, color = if (isDark) TextSecondaryDark else TextSecondaryLight)
                }
            },
            confirmButton = {
                if (!eq.isOutOfStock) {
                    Button(
                        onClick = {
                            viewModel.closeDetails()
                            viewModel.openRequestDialog(eq)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = if (isDark) CyberCyan else ElectricBlue, contentColor = Color.Black)
                    ) {
                        Text("Request Item", fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.closeDetails() }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
private fun StudentEquipmentCard(
    equipment: Equipment,
    isDark: Boolean,
    onRequestClick: () -> Unit,
    onDetailsClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onDetailsClick)
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
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isDark) CyberCyan.copy(alpha = 0.15f) else ElectricBlue.copy(alpha = 0.12f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = equipment.category,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) CyberCyan else ElectricBlue
                    )
                }

                // Stock status badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (equipment.isOutOfStock) CircuitError.copy(alpha = 0.15f)
                            else if (equipment.isLowStock) CircuitWarning.copy(alpha = 0.15f)
                            else CircuitSuccess.copy(alpha = 0.15f)
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (equipment.isOutOfStock) "OUT OF STOCK"
                        else if (equipment.isLowStock) "LOW STOCK (${equipment.availableQuantity})"
                        else "${equipment.availableQuantity} AVAILABLE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (equipment.isOutOfStock) CircuitError
                        else if (equipment.isLowStock) CircuitWarning
                        else CircuitSuccess
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = equipment.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = if (isDark) TextPrimaryDark else TextPrimaryLight,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "📍 ${equipment.storageLocation.ifBlank { "Storage Rack" }} • Condition: ${equipment.condition}",
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
                    text = "Total Stock: ${equipment.totalQuantity}",
                    fontSize = 11.sp,
                    color = if (isDark) TextSecondaryDark else TextSecondaryLight
                )

                if (!equipment.isOutOfStock) {
                    Button(
                        onClick = onRequestClick,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDark) CyberCyan else ElectricBlue,
                            contentColor = if (isDark) Color.Black else Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text("Request", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun StudentRequestCard(
    request: EquipmentRequest,
    isDark: Boolean,
    onCancelClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = if (request.isOverdue) CircuitError.copy(alpha = 0.5f)
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
                text = "Requested: ${request.requestedQuantity} unit(s) • Return by: 📅 ${request.expectedReturnDate}",
                fontSize = 11.sp,
                color = if (request.isOverdue) CircuitError else if (isDark) TextSecondaryDark else TextSecondaryLight
            )

            if (request.purpose.isNotBlank()) {
                Text(
                    text = "Purpose: ${request.purpose}",
                    fontSize = 11.sp,
                    color = if (isDark) TextSecondaryDark else TextSecondaryLight
                )
            }

            if (request.isRejected && request.rejectionReason.isNotBlank()) {
                Text(
                    text = "Reason: ${request.rejectionReason}",
                    fontSize = 11.sp,
                    color = CircuitError,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (request.isPending) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(
                        onClick = onCancelClick,
                        colors = ButtonDefaults.textButtonColors(contentColor = CircuitError),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Cancel Request", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun RequestEquipmentDialog(
    equipment: Equipment,
    isDark: Boolean,
    isLoading: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onSubmit: (Int, String, String, String) -> Unit
) {
    var quantity by remember { mutableIntStateOf(1) }
    var purpose by remember { mutableStateOf("") }
    var returnDate by remember { mutableStateOf(BookingTimeUtils.getTodayDateString()) }
    var projectName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Request ${equipment.name}",
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

                Text(
                    text = "Available Stock: ${equipment.availableQuantity} units",
                    fontSize = 12.sp,
                    color = if (isDark) CyberCyan else ElectricBlue,
                    fontWeight = FontWeight.Bold
                )

                // Quantity selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Quantity Needed:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { if (quantity > 1) quantity-- },
                            enabled = quantity > 1
                        ) {
                            Icon(Icons.Filled.Remove, contentDescription = "Decrease")
                        }
                        Text(text = "$quantity", fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.padding(horizontal = 8.dp))
                        IconButton(
                            onClick = { if (quantity < equipment.availableQuantity) quantity++ },
                            enabled = quantity < equipment.availableQuantity
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = "Increase")
                        }
                    }
                }

                OutlinedTextField(
                    value = purpose,
                    onValueChange = { purpose = it },
                    label = { Text("Purpose / Work Description *") },
                    placeholder = { Text("e.g. Quadcopter PID Tuning") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = projectName,
                    onValueChange = { projectName = it },
                    label = { Text("Related Project (Optional)") },
                    placeholder = { Text("e.g. Autonomous Ground Vehicle") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = returnDate,
                    onValueChange = { returnDate = it },
                    label = { Text("Expected Return Date (yyyy-MM-dd) *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(quantity, purpose, returnDate, projectName) },
                enabled = !isLoading && purpose.isNotBlank() && returnDate.isNotBlank() && quantity <= equipment.availableQuantity,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDark) CyberCyan else ElectricBlue,
                    contentColor = if (isDark) Color.Black else Color.White
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                } else {
                    Text("Submit Request", fontWeight = FontWeight.Bold)
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
