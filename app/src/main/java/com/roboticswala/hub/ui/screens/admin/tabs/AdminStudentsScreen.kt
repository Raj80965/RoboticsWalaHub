package com.roboticswala.hub.ui.screens.admin.tabs

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Domain
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.roboticswala.hub.data.models.StudentDirectoryItem
import com.roboticswala.hub.ui.components.StatusChip
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
fun AdminStudentsScreen(
    students: List<StudentDirectoryItem>,
    onApproveStudent: (String) -> Unit,
    onDeleteStudent: (String) -> Unit,
    activeFilter: String = "ALL",
    onFilterChanged: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    var searchQuery by remember { mutableStateOf("") }
    var currentFilter by remember(activeFilter) { mutableStateOf(activeFilter) }
    var studentToDelete by remember { mutableStateOf<StudentDirectoryItem?>(null) }
    var selectedStudentAadhar by remember { mutableStateOf<StudentDirectoryItem?>(null) }
    var selectedStudentForFullDetails by remember { mutableStateOf<StudentDirectoryItem?>(null) }

    val pendingCount = students.count { it.status.equals("Pending", ignoreCase = true) }
    val approvedCount = students.count { it.status.equals("Approved", ignoreCase = true) }

    val filteredList = students.filter { student ->
        val matchesSearch = student.name.contains(searchQuery, ignoreCase = true) ||
                student.id.contains(searchQuery, ignoreCase = true) ||
                student.currentProject.contains(searchQuery, ignoreCase = true)
        val matchesFilter = when (currentFilter.uppercase()) {
            "PENDING" -> student.status.equals("Pending", ignoreCase = true)
            "APPROVED" -> student.status.equals("Approved", ignoreCase = true)
            else -> true
        }
        matchesSearch && matchesFilter
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Student Directory & Approvals",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = if (isDark) TextPrimaryDark else TextPrimaryLight
            )
            Text(
                text = "Manage lab access permissions and RFID status",
                style = MaterialTheme.typography.bodySmall,
                color = if (isDark) TextSecondaryDark else TextSecondaryLight
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by name, ID, or project...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search",
                        tint = if (isDark) TextSecondaryDark else TextSecondaryLight
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = if (isDark) DarkSurfaceElevated else LightSurfaceElevated,
                    unfocusedContainerColor = if (isDark) DarkSurfaceElevated else LightSurfaceElevated,
                    focusedBorderColor = if (isDark) CyberCyan else ElectricBlue,
                    unfocusedBorderColor = if (isDark) DarkSurfaceBorder else LightSurfaceBorder,
                    focusedTextColor = if (isDark) TextPrimaryDark else TextPrimaryLight,
                    unfocusedTextColor = if (isDark) TextPrimaryDark else TextPrimaryLight
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Filter Tabs (All / Pending / Approved)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filters = listOf(
                    "ALL" to "All (${students.size})",
                    "PENDING" to "Pending ($pendingCount)",
                    "APPROVED" to "Approved ($approvedCount)"
                )
                filters.forEach { (filterKey, filterLabel) ->
                    val isSelected = currentFilter.equals(filterKey, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (isSelected) {
                                    if (filterKey == "PENDING") CircuitWarning.copy(alpha = 0.2f)
                                    else if (isDark) CyberCyan.copy(alpha = 0.2f) else ElectricBlue.copy(alpha = 0.15f)
                                } else {
                                    if (isDark) DarkSurfaceBorder.copy(alpha = 0.5f) else LightSurfaceBorder.copy(alpha = 0.5f)
                                }
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) {
                                    if (filterKey == "PENDING") CircuitWarning
                                    else if (isDark) CyberCyan else ElectricBlue
                                } else Color.Transparent,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable {
                                currentFilter = filterKey
                                onFilterChanged(filterKey)
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = filterLabel,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = if (isSelected) {
                                if (filterKey == "PENDING") CircuitWarning
                                else if (isDark) CyberCyan else ElectricBlue
                            } else {
                                if (isDark) TextSecondaryDark else TextSecondaryLight
                            }
                        )
                    }
                }
            }
        }

        if (filteredList.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp)
                        .border(
                            width = 1.dp,
                            color = if (isDark) DarkSurfaceBorder else LightSurfaceBorder,
                            shape = RoundedCornerShape(16.dp)
                        ),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDark) DarkSurface.copy(alpha = 0.8f) else LightSurface
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = if (isDark) CyberCyan.copy(alpha = 0.6f) else ElectricBlue.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No Students Found",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (isDark) TextPrimaryDark else TextPrimaryLight
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (searchQuery.isNotBlank()) "No students match '$searchQuery'" else "Registered students will appear here in real-time.",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isDark) TextSecondaryDark else TextSecondaryLight
                        )
                    }
                }
            }
        } else {
            items(filteredList) { student ->
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
                    containerColor = if (isDark) DarkSurface.copy(alpha = 0.95f) else LightSurface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            val studentAvatarBitmap = remember(student.photoUrl) {
                                if (student.photoUrl.isNotBlank() && !student.photoUrl.startsWith("http")) {
                                    try {
                                        val clean = if (student.photoUrl.contains(",")) student.photoUrl.substringAfter(",") else student.photoUrl
                                        val bytes = android.util.Base64.decode(clean, android.util.Base64.DEFAULT)
                                        android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                                    } catch (e: Exception) {
                                        null
                                    }
                                } else null
                            }

                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(if (isDark) DarkSurfaceElevated else LightSurfaceElevated)
                                    .border(
                                        width = 1.dp,
                                        color = if (isDark) CyberCyan.copy(alpha = 0.5f) else ElectricBlue.copy(alpha = 0.5f),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (studentAvatarBitmap != null) {
                                    androidx.compose.foundation.Image(
                                        bitmap = studentAvatarBitmap.asImageBitmap(),
                                        contentDescription = student.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                    )
                                } else if (student.photoUrl.startsWith("http")) {
                                    AsyncImage(
                                        model = student.photoUrl,
                                        contentDescription = student.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                    )
                                } else if (student.initials.isNotBlank()) {
                                    Text(
                                        text = student.initials,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (isDark) CyberCyan else ElectricBlue
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Filled.Person,
                                        contentDescription = null,
                                        tint = if (isDark) CyberCyan else ElectricBlue,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = student.name,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (isDark) TextPrimaryDark else TextPrimaryLight
                                )
                                Text(
                                    text = if (student.studentId.isNotBlank()) "${student.studentId} • ${student.email}" else student.email,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = if (isDark) TextSecondaryDark else TextSecondaryLight
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StatusChip(status = student.status)
                            Spacer(modifier = Modifier.width(4.dp))
                            IconButton(
                                onClick = { studentToDelete = student },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = "Remove Student",
                                    tint = CircuitError.copy(alpha = 0.85f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Project: ${student.currentProject}",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = if (isDark) CyberCyan else ElectricBlue
                        )
                        Text(
                            text = "Attendance: ${student.attendance}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isDark) TextSecondaryDark else TextSecondaryLight
                        )
                    }

                    if (student.phone.isNotBlank() || student.parentPhone.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isDark) DarkSurfaceElevated.copy(alpha = 0.5f) else LightSurfaceElevated.copy(alpha = 0.8f))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            if (student.phone.isNotBlank()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.Phone,
                                        contentDescription = null,
                                        tint = if (isDark) CyberCyan else ElectricBlue,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Student: ${student.phone}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                        color = if (isDark) TextPrimaryDark else TextPrimaryLight
                                    )
                                }
                            }
                            if (student.parentPhone.isNotBlank() || student.parentName.isNotBlank()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.Call,
                                        contentDescription = null,
                                        tint = CircuitSuccess,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Parent: ${student.parentName.ifBlank { "Guardian" }} (${student.parentPhone.ifBlank { "N/A" }})",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                        color = if (isDark) TextSecondaryDark else TextSecondaryLight
                                    )
                                }
                            }
                        }
                    }

                    if (student.aadharNumber.isNotBlank() || student.aadharCardUrl.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isDark) DarkSurfaceElevated.copy(alpha = 0.3f) else LightSurfaceElevated.copy(alpha = 0.5f))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🪪 Aadhaar: ${if (student.aadharNumber.isNotBlank()) student.aadharNumber else "Document Attached"}",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = if (isDark) TextSecondaryDark else TextSecondaryLight
                            )
                            if (student.aadharCardUrl.isNotBlank()) {
                                Text(
                                    text = "View Aadhaar ➔",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
                                    color = if (isDark) CyberCyan else ElectricBlue,
                                    modifier = Modifier
                                        .clickable { selectedStudentAadhar = student }
                                        .padding(2.dp)
                                )
                            }
                        }
                    }

                    if (student.status == "Pending") {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { onApproveStudent(student.id) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CircuitSuccess)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = Color.Black
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Approve",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }

                            OutlinedButton(
                                onClick = { studentToDelete = student },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CircuitError.copy(alpha = 0.8f)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = CircuitError)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = CircuitError
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Reject",
                                    color = CircuitError,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = { selectedStudentForFullDetails = student },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isDark) CyberCyan.copy(alpha = 0.08f) else ElectricBlue.copy(alpha = 0.08f),
                            contentColor = if (isDark) CyberCyan else ElectricBlue
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) CyberCyan.copy(alpha = 0.5f) else ElectricBlue.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = "📋 View Full Student Profile & Docs",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    if (studentToDelete != null) {
        AlertDialog(
            onDismissRequest = { studentToDelete = null },
            containerColor = if (isDark) DarkSurfaceElevated else LightSurfaceElevated,
            title = {
                Text(
                    text = "Remove Student",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (isDark) TextPrimaryDark else TextPrimaryLight
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to remove \"${studentToDelete?.name}\"? This will delete their lab access and profile.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isDark) TextSecondaryDark else TextSecondaryLight
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        studentToDelete?.let { onDeleteStudent(it.id) }
                        studentToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CircuitError),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Remove", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { studentToDelete = null }
                ) {
                    Text("Cancel", color = if (isDark) TextSecondaryDark else TextSecondaryLight)
                }
            }
        )
    }

    if (selectedStudentAadhar != null) {
        val student = selectedStudentAadhar!!
        val aadharBitmap = remember(student.aadharCardUrl) {
            if (student.aadharCardUrl.isNotBlank() && !student.aadharCardUrl.startsWith("http")) {
                try {
                    val cleanBase64 = if (student.aadharCardUrl.contains(",")) {
                        student.aadharCardUrl.substringAfter(",")
                    } else student.aadharCardUrl
                    val bytes = Base64.decode(cleanBase64, Base64.DEFAULT)
                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                } catch (e: Exception) {
                    null
                }
            } else null
        }

        AlertDialog(
            onDismissRequest = { selectedStudentAadhar = null },
            title = {
                Text("🪪 Student Aadhaar Card (${student.name})")
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (aadharBitmap != null) {
                        Image(
                            bitmap = aadharBitmap.asImageBitmap(),
                            contentDescription = "Aadhaar Card",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, if (isDark) DarkSurfaceBorder else LightSurfaceBorder, RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Text(
                            text = "No preview available for this document.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isDark) TextSecondaryDark else TextSecondaryLight
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Aadhaar No: ${if (student.aadharNumber.isNotBlank()) student.aadharNumber else "Document Attached"}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = if (isDark) CyberCyan else ElectricBlue
                    )
                }
            },
            confirmButton = {
                Button(onClick = { selectedStudentAadhar = null }) {
                    Text("Close")
                }
            }
        )
    }

    if (selectedStudentForFullDetails != null) {
        val student = selectedStudentForFullDetails!!
        val aadharBitmap = remember(student.aadharCardUrl) {
            if (student.aadharCardUrl.isNotBlank() && !student.aadharCardUrl.startsWith("http")) {
                try {
                    val cleanBase64 = if (student.aadharCardUrl.contains(",")) {
                        student.aadharCardUrl.substringAfter(",")
                    } else student.aadharCardUrl
                    val bytes = Base64.decode(cleanBase64, Base64.DEFAULT)
                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                } catch (e: Exception) {
                    null
                }
            } else null
        }

        val avatarBitmap = remember(student.photoUrl) {
            if (student.photoUrl.isNotBlank() && !student.photoUrl.startsWith("http")) {
                try {
                    val clean = if (student.photoUrl.contains(",")) student.photoUrl.substringAfter(",") else student.photoUrl
                    val bytes = Base64.decode(clean, Base64.DEFAULT)
                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                } catch (e: Exception) {
                    null
                }
            } else null
        }

        AlertDialog(
            onDismissRequest = { selectedStudentForFullDetails = null },
            modifier = Modifier.fillMaxWidth(0.96f),
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Student Profile & Dossier",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (isDark) CyberCyan else ElectricBlue
                    )
                    IconButton(onClick = { selectedStudentForFullDetails = null }) {
                        Icon(imageVector = Icons.Filled.Close, contentDescription = "Close")
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Header with Avatar & Status
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isDark) DarkSurfaceElevated else LightSurfaceElevated)
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(if (isDark) DarkSurface else LightSurface)
                                .border(2.dp, if (isDark) CyberCyan else ElectricBlue, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (avatarBitmap != null) {
                                Image(
                                    bitmap = avatarBitmap.asImageBitmap(),
                                    contentDescription = student.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else if (student.photoUrl.startsWith("http")) {
                                AsyncImage(
                                    model = student.photoUrl,
                                    contentDescription = student.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Text(
                                    text = student.initials.ifBlank { "ST" },
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) CyberCyan else ElectricBlue,
                                    fontSize = 18.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = student.name,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (isDark) TextPrimaryDark else TextPrimaryLight
                            )
                            Text(
                                text = "ID: ${student.studentId.ifBlank { "RWH-2026-042" }}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = if (isDark) CyberCyan else ElectricBlue
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            StatusChip(status = student.status)
                        }
                    }

                    // 1. ACADEMIC DETAILS
                    val collegeText = if (student.college.isNotBlank()) student.college else "Robotics Innovation Lab"
                    val branchText = if (student.branch.isNotBlank()) student.branch else "Robotics & Automation"
                    val yearText = if (student.year.isNotBlank()) student.year else "Year 3"
                    val studentPhoneText = if (student.phone.isNotBlank()) student.phone else "Not provided"
                    val parentNameText = if (student.parentName.isNotBlank()) student.parentName else "Guardian"
                    val parentPhoneText = if (student.parentPhone.isNotBlank()) student.parentPhone else "Not provided"
                    val emergencyText = if (student.emergencyContact.isNotBlank()) student.emergencyContact else parentPhoneText
                    val aadharText = if (student.aadharNumber.isNotBlank()) student.aadharNumber else "Not provided"

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = if (isDark) DarkSurface else LightSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) DarkSurfaceBorder else LightSurfaceBorder),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "🎓 Academic Details",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = if (isDark) TextPrimaryDark else TextPrimaryLight
                            )
                            DetailLine(icon = Icons.Filled.Domain, label = "College", value = collegeText, isDark = isDark)
                            DetailLine(icon = Icons.Filled.School, label = "Branch", value = branchText, isDark = isDark)
                            DetailLine(icon = Icons.Filled.DateRange, label = "Academic Year", value = yearText, isDark = isDark)
                            DetailLine(icon = Icons.Filled.Email, label = "Email", value = student.email, isDark = isDark)
                        }
                    }

                    // 2. CONTACT & PARENTS
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = if (isDark) DarkSurface else LightSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) DarkSurfaceBorder else LightSurfaceBorder),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "👨‍👩‍👧 Contact & Parents",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = if (isDark) TextPrimaryDark else TextPrimaryLight
                            )
                            DetailLine(icon = Icons.Filled.Phone, label = "Student Mobile", value = studentPhoneText, isDark = isDark)
                            DetailLine(icon = Icons.Filled.FamilyRestroom, label = "Parent Name", value = parentNameText, isDark = isDark)
                            DetailLine(icon = Icons.Filled.Call, label = "Parent Mobile", value = parentPhoneText, isDark = isDark)
                            DetailLine(icon = Icons.Filled.Emergency, label = "Emergency Contact", value = emergencyText, isDark = isDark)
                        }
                    }

                    // 3. AADHAAR CARD DOCUMENTATION
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = if (isDark) DarkSurface else LightSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) DarkSurfaceBorder else LightSurfaceBorder),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "🪪 Government Aadhaar Card",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                    color = if (isDark) TextPrimaryDark else TextPrimaryLight
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (student.aadharCardUrl.isNotBlank()) CircuitSuccess.copy(alpha = 0.15f) else CircuitError.copy(alpha = 0.15f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (student.aadharCardUrl.isNotBlank()) "ATTACHED" else "NOT UPLOADED",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (student.aadharCardUrl.isNotBlank()) CircuitSuccess else CircuitError
                                    )
                                }
                            }

                            DetailLine(icon = Icons.Filled.Badge, label = "Aadhaar No", value = aadharText, isDark = isDark)

                            if (aadharBitmap != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Image(
                                    bitmap = aadharBitmap.asImageBitmap(),
                                    contentDescription = "Aadhaar Card",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .border(1.dp, if (isDark) DarkSurfaceBorder else LightSurfaceBorder, RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Fit
                                )
                            }
                        }
                    }

                    // 4. ATTENDANCE & LAB ACCESS
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = if (isDark) DarkSurface else LightSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) DarkSurfaceBorder else LightSurfaceBorder),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "⚡ Lab Access & Participation",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = if (isDark) TextPrimaryDark else TextPrimaryLight
                            )
                            DetailLine(icon = Icons.Filled.DateRange, label = "Attendance", value = "${student.attendance.toInt()}% (Tracked)", isDark = isDark)
                            DetailLine(icon = Icons.Filled.Check, label = "RFID / Access Status", value = student.rfidStatus, isDark = isDark)
                        }
                    }
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (student.status == "Pending") {
                        Button(
                            onClick = {
                                onApproveStudent(student.id)
                                selectedStudentForFullDetails = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CircuitSuccess)
                        ) {
                            Icon(imageVector = Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Black)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Approve", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }

                    Button(onClick = { selectedStudentForFullDetails = null }) {
                        Text("Close")
                    }
                }
            }
        )
    }
}

@Composable
private fun DetailLine(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    isDark: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isDark) CyberCyan else ElectricBlue,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = if (isDark) TextSecondaryDark else TextSecondaryLight
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = if (isDark) TextPrimaryDark else TextPrimaryLight
        )
    }
}
