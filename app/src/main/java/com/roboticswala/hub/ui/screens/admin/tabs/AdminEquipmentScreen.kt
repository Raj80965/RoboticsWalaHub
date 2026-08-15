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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.roboticswala.hub.data.models.Equipment
import com.roboticswala.hub.ui.screens.admin.equipment.AdminEquipmentViewModel
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
fun AdminEquipmentScreen(
    onNavigateToRequests: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: AdminEquipmentViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isDark = isSystemInDarkTheme()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(if (isDark) DarkSurface else LightSurface)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Equipment & Inventory",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = if (isDark) TextPrimaryDark else TextPrimaryLight
                        )
                        Text(
                            text = "Lab hardware stock, components & allocations",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isDark) TextSecondaryDark else TextSecondaryLight
                        )
                    }

                    Button(
                        onClick = onNavigateToRequests,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDark) CyberCyan.copy(alpha = 0.2f) else ElectricBlue.copy(alpha = 0.15f),
                            contentColor = if (isDark) CyberCyan else ElectricBlue
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Requests ➔", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Low Stock Alert Banner
                if (uiState.lowStockCount > 0 || uiState.outOfStockCount > 0) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.toggleLowStockOnly() }
                            .border(
                                width = 1.dp,
                                color = if (uiState.outOfStockCount > 0) CircuitError.copy(alpha = 0.5f) else CircuitWarning.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (uiState.outOfStockCount > 0) CircuitError.copy(alpha = 0.12f) else CircuitWarning.copy(alpha = 0.12f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Warning,
                                contentDescription = null,
                                tint = if (uiState.outOfStockCount > 0) CircuitError else CircuitWarning,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Alert: ${uiState.outOfStockCount} Out of Stock, ${uiState.lowStockCount} Low Stock items",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (uiState.outOfStockCount > 0) CircuitError else CircuitWarning,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = if (uiState.onlyLowStock) "Show All" else "Filter",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) CyberCyan else ElectricBlue
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Search Bar
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("Search equipment name, bin, location...", fontSize = 13.sp) },
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

                // Category Chips
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

            // Inventory List
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = if (isDark) CyberCyan else ElectricBlue)
                }
            } else if (uiState.filteredEquipment.isEmpty()) {
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
                            text = "No equipment in inventory",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (isDark) TextPrimaryDark else TextPrimaryLight
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(uiState.filteredEquipment, key = { it.equipmentId }) { item ->
                        AdminEquipmentCard(
                            equipment = item,
                            isDark = isDark,
                            onClick = { viewModel.openDetails(item) },
                            onEdit = { viewModel.openEditDialog(item) },
                            onDelete = { viewModel.promptDelete(item) }
                        )
                    }
                }
            }
        }

        // FAB Add Equipment
        FloatingActionButton(
            onClick = { viewModel.openAddDialog() },
            containerColor = if (isDark) CyberCyan else ElectricBlue,
            contentColor = if (isDark) Color.Black else Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
        ) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = "Add Equipment")
        }
    }

    // ── Add / Edit Equipment Modal ────────────────────────────────────────────
    if (uiState.showAddDialog) {
        AddEquipmentDialog(
            existing = uiState.equipmentToEdit,
            isDark = isDark,
            isLoading = uiState.isActionLoading,
            errorMessage = uiState.errorMessage,
            onDismiss = { viewModel.closeAddDialog() },
            onSave = { name, cat, desc, tot, avail, minStk, loc, cond, pDate, uPrice, supp ->
                viewModel.saveEquipment(name, cat, desc, tot, avail, minStk, loc, cond, pDate, uPrice, supp)
            }
        )
    }

    // ── Delete Confirmation Modal ─────────────────────────────────────────────
    uiState.equipmentToDelete?.let { eq ->
        AlertDialog(
            onDismissRequest = { viewModel.dismissDelete() },
            title = { Text("Delete Equipment", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete \"${eq.name}\" from lab inventory?") },
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
private fun AdminEquipmentCard(
    equipment: Equipment,
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
                        Text(
                            text = equipment.category,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) CyberCyan else ElectricBlue
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Condition: ${equipment.condition}",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isDark) TextSecondaryDark else TextSecondaryLight
                    )
                }

                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Filled.Edit, contentDescription = "Edit", tint = if (isDark) CyberCyan else ElectricBlue, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete", tint = CircuitError, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = equipment.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = if (isDark) TextPrimaryDark else TextPrimaryLight
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "📍 ${equipment.storageLocation.ifBlank { "Unassigned" }}",
                fontSize = 11.sp,
                color = if (isDark) TextSecondaryDark else TextSecondaryLight
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Stock: ${equipment.availableQuantity} available / ${equipment.totalQuantity} total (${equipment.issuedQuantity} issued)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (equipment.isOutOfStock) CircuitError else if (equipment.isLowStock) CircuitWarning else CircuitSuccess
                )

                if (equipment.isOutOfStock) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(CircuitError.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("OUT OF STOCK", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = CircuitError)
                    }
                }
            }
        }
    }
}

@Composable
private fun AddEquipmentDialog(
    existing: Equipment?,
    isDark: Boolean,
    isLoading: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onSave: (String, String, String, Int, Int, Int, String, String, String, Double, String) -> Unit
) {
    var name by remember { mutableStateOf(existing?.name ?: "") }
    var category by remember { mutableStateOf(existing?.category ?: Equipment.CATEGORY_SENSORS) }
    var description by remember { mutableStateOf(existing?.description ?: "") }
    var totalText by remember { mutableStateOf(existing?.totalQuantity?.toString() ?: "10") }
    var availableText by remember { mutableStateOf(existing?.availableQuantity?.toString() ?: "10") }
    var minStockText by remember { mutableStateOf(existing?.minimumStockLevel?.toString() ?: "2") }
    var location by remember { mutableStateOf(existing?.storageLocation ?: "Rack B - Bin 4") }
    var condition by remember { mutableStateOf(existing?.condition ?: Equipment.CONDITION_NEW) }
    var purchaseDate by remember { mutableStateOf(existing?.purchaseDate ?: "") }
    var unitPriceText by remember { mutableStateOf(existing?.unitPrice?.takeIf { it > 0 }?.toString() ?: "") }
    var supplier by remember { mutableStateOf(existing?.supplierName ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (existing != null) "Edit Equipment" else "Add Hardware Equipment",
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

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Equipment Name *") },
                    placeholder = { Text("e.g. RPLiDAR A1M8 360 Laser Scanner") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Category Selector
                Text("Category:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(Equipment.ALL_CATEGORIES) { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, fontSize = 10.sp) }
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(
                        value = totalText,
                        onValueChange = { totalText = it },
                        label = { Text("Total Qty *") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = availableText,
                        onValueChange = { availableText = it },
                        label = { Text("Avail Qty *") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = minStockText,
                        onValueChange = { minStockText = it },
                        label = { Text("Min Stock") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Storage Location / Bin *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Condition Selector
                Text("Condition:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(Equipment.ALL_CONDITIONS) { cond ->
                        FilterChip(
                            selected = condition == cond,
                            onClick = { condition = cond },
                            label = { Text(cond, fontSize = 10.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description & Specifications") },
                    minLines = 2,
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val tot = totalText.toIntOrNull() ?: 0
                    val avail = availableText.toIntOrNull() ?: 0
                    val minStk = minStockText.toIntOrNull() ?: 2
                    val price = unitPriceText.toDoubleOrNull() ?: 0.0
                    onSave(name, category, description, tot, avail, minStk, location, condition, purchaseDate, price, supplier)
                },
                enabled = !isLoading && name.isNotBlank() && location.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDark) CyberCyan else ElectricBlue,
                    contentColor = if (isDark) Color.Black else Color.White
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                } else {
                    Text(if (existing != null) "Update" else "Add Equipment", fontWeight = FontWeight.Bold)
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
