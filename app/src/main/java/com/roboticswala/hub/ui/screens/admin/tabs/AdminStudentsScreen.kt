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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
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
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    var searchQuery by remember { mutableStateOf("") }
    var studentToDelete by remember { mutableStateOf<StudentDirectoryItem?>(null) }
    var selectedStudentAadhar by remember { mutableStateOf<StudentDirectoryItem?>(null) }

    val filteredList = students.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
        it.id.contains(searchQuery, ignoreCase = true) ||
        it.currentProject.contains(searchQuery, ignoreCase = true)
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
                    } else {
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = { studentToDelete = student },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CircuitError.copy(alpha = 0.5f)),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = CircuitError.copy(alpha = 0.08f),
                                contentColor = CircuitError
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = CircuitError
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Remove Student",
                                color = CircuitError,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        }
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
}
