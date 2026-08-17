package com.roboticswala.hub.ui.screens.admin.tabs

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Domain
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.WorkspacePremium
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.roboticswala.hub.data.models.UserProfile
import com.roboticswala.hub.ui.components.RoboticsOutlinedButton
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
import java.io.ByteArrayOutputStream

@Composable
fun AdminProfileScreen(
    adminProfile: UserProfile?,
    onUpdateProfile: (fullName: String, phone: String, college: String, branch: String, year: String, adminId: String, emergencyContact: String) -> Unit,
    onUpdatePhoto: (photoBase64: String) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scrollState = rememberScrollState()

    var showEditDialog by remember { mutableStateOf(false) }

    val profile = adminProfile ?: UserProfile(
        fullName = "Administrator",
        email = "admin@roboticswala.com",
        role = "Admin",
        status = "Approved"
    )

    val adminId = profile.displayAdminId

    val photoBitmap = remember(profile.photoUrl) {
        if (profile.photoUrl.isNotBlank() && !profile.photoUrl.startsWith("http")) {
            try {
                val clean = if (profile.photoUrl.contains(",")) profile.photoUrl.substringAfter(",") else profile.photoUrl
                val bytes = Base64.decode(clean, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            } catch (e: Exception) {
                null
            }
        } else null
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val base64 = compressAndEncodeImage(context, it)
            if (base64 != null) {
                onUpdatePhoto(base64)
                Toast.makeText(context, "Admin photo updated!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Failed to process photo.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- ADMIN HERO CARD ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.5.dp,
                    brush = Brush.horizontalGradient(listOf(CircuitWarning, ElectricBlue, CyberCyan)),
                    shape = RoundedCornerShape(20.dp)
                ),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDark) DarkSurfaceElevated else LightSurfaceElevated
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar with Camera Badge
                Box(
                    contentAlignment = Alignment.BottomEnd,
                    modifier = Modifier.size(96.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.linearGradient(listOf(CircuitWarning, ElectricBlue, CyberCyan))
                            )
                            .padding(3.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(if (isDark) DarkSurface else LightSurface),
                            contentAlignment = Alignment.Center
                        ) {
                            if (photoBitmap != null) {
                                Image(
                                    bitmap = photoBitmap.asImageBitmap(),
                                    contentDescription = "Admin Avatar",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else if (profile.photoUrl.startsWith("http")) {
                                AsyncImage(
                                    model = profile.photoUrl,
                                    contentDescription = "Admin Avatar",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Text(
                                    text = profile.initials,
                                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                    color = CircuitWarning
                                )
                            }
                        }
                    }

                    // Edit Photo Button
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(CircuitWarning)
                            .clickable { photoPickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CameraAlt,
                            contentDescription = "Change Photo",
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = profile.fullName.ifBlank { "System Administrator" },
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = if (isDark) TextPrimaryDark else TextPrimaryLight
                )

                Text(
                    text = profile.email,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isDark) TextSecondaryDark else TextSecondaryLight
                )

                Spacer(modifier = Modifier.height(10.dp))

                // ADMIN ID BADGE WITH COPY BUTTON
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isDark) DarkSurface else LightSurface)
                        .border(1.dp, CircuitWarning.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .clickable {
                            clipboardManager.setText(AnnotatedString(adminId))
                            Toast.makeText(context, "Admin ID copied: $adminId", Toast.LENGTH_SHORT).show()
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Badge,
                        contentDescription = null,
                        tint = CircuitWarning,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ID: $adminId",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = CircuitWarning
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Filled.ContentCopy,
                        contentDescription = "Copy ID",
                        tint = if (isDark) TextSecondaryDark else TextSecondaryLight,
                        modifier = Modifier.size(14.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(CircuitWarning.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "👑 ROOT ADMINISTRATOR",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = CircuitWarning,
                            fontSize = 10.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(CircuitSuccess.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "🛡️ VERIFIED INCHARGE",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = CircuitSuccess,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        // --- SECTION 1: ADMINISTRATIVE AUTHORITY ---
        AdminSectionCard(
            title = "🏛️ Authority & Designation",
            isDark = isDark
        ) {
            AdminProfileField(
                icon = Icons.Filled.WorkspacePremium,
                label = "Designation / Role",
                value = if (profile.branch.isNotBlank()) profile.branch else "Lab Director & Robotics Head",
                isDark = isDark
            )
            AdminProfileField(
                icon = Icons.Filled.Domain,
                label = "Organization / Center",
                value = if (profile.college.isNotBlank()) profile.college else "Robotics Innovation Center & Hub",
                isDark = isDark
            )
            AdminProfileField(
                icon = Icons.Filled.LocationOn,
                label = "Control Station Desk",
                value = if (profile.year.isNotBlank()) profile.year else "Lab Control Center A-1",
                isDark = isDark
            )
            AdminProfileField(
                icon = Icons.Filled.VerifiedUser,
                label = "System Clearance Level",
                value = "Tier-1 Master Controller (Full Superadmin)",
                isDark = isDark
            )
        }

        // --- SECTION 2: OFFICIAL COMMUNICATIONS ---
        AdminSectionCard(
            title = "📞 Official Contact & Communications",
            isDark = isDark
        ) {
            AdminProfileField(
                icon = Icons.Filled.Email,
                label = "Official Admin Email",
                value = profile.email,
                isDark = isDark
            )
            AdminProfileField(
                icon = Icons.Filled.Phone,
                label = "Admin Contact Mobile",
                value = if (profile.phone.isNotBlank()) profile.phone else "Not configured",
                isDark = isDark
            )
            AdminProfileField(
                icon = Icons.Filled.Emergency,
                label = "Direct Escalation Line",
                value = if (profile.emergencyContact.isNotBlank()) profile.emergencyContact else if (profile.phone.isNotBlank()) profile.phone else "Direct Hub Dispatch",
                isDark = isDark
            )
        }

        // --- SECTION 3: SYSTEM PERMISSIONS & SECURITY ---
        AdminSectionCard(
            title = "🔒 Master Clearances & Permissions",
            isDark = isDark
        ) {
            AdminProfileField(
                icon = Icons.Filled.Fingerprint,
                label = "RFID Hardware & Gate Override",
                value = "Authorized (Master Access)",
                isDark = isDark
            )
            AdminProfileField(
                icon = Icons.Filled.Key,
                label = "Student Approvals & Directory",
                value = "Full Read & Write Authorization",
                isDark = isDark
            )
            AdminProfileField(
                icon = Icons.Filled.Shield,
                label = "Inventory & Budget Approvals",
                value = "Authorized Signatory",
                isDark = isDark
            )
        }

        // --- ACTIONS ---
        Button(
            onClick = { showEditDialog = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isDark) CyberCyan else ElectricBlue
            )
        ) {
            Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = Color.Black
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Edit Admin Profile & Designation",
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        }

        RoboticsOutlinedButton(
            text = "Log Out of Admin Hub",
            onClick = onLogout,
            leadingIcon = Icons.Filled.Logout
        )

        Spacer(modifier = Modifier.height(30.dp))
    }

    if (showEditDialog) {
        EditAdminProfileDialog(
            currentProfile = profile,
            onDismiss = { showEditDialog = false },
            onSave = { name, phone, org, desig, desk, adminIdVal, emergency ->
                onUpdateProfile(name, phone, org, desig, desk, adminIdVal, emergency)
                showEditDialog = false
            }
        )
    }
}

@Composable
private fun AdminSectionCard(
    title: String,
    isDark: Boolean,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) DarkSurface else LightSurface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDark) DarkSurfaceBorder else LightSurfaceBorder
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = if (isDark) TextPrimaryDark else TextPrimaryLight
            )
            content()
        }
    }
}

@Composable
private fun AdminProfileField(
    icon: ImageVector,
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
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = if (isDark) TextSecondaryDark else TextSecondaryLight
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = if (isDark) TextPrimaryDark else TextPrimaryLight
            )
        }
    }
}

@Composable
private fun EditAdminProfileDialog(
    currentProfile: UserProfile,
    onDismiss: () -> Unit,
    onSave: (fullName: String, phone: String, org: String, designation: String, desk: String, adminId: String, emergency: String) -> Unit
) {
    val isDark = isSystemInDarkTheme()
    var fullName by remember { mutableStateOf(currentProfile.fullName) }
    var adminId by remember { mutableStateOf(currentProfile.displayAdminId) }
    var designation by remember { mutableStateOf(if (currentProfile.branch.isNotBlank()) currentProfile.branch else "Lab Director & Robotics Head") }
    var organization by remember { mutableStateOf(if (currentProfile.college.isNotBlank()) currentProfile.college else "Robotics Innovation Center & Hub") }
    var deskStation by remember { mutableStateOf(if (currentProfile.year.isNotBlank()) currentProfile.year else "Lab Control Center A-1") }
    var phone by remember { mutableStateOf(currentProfile.phone) }
    var emergencyContact by remember { mutableStateOf(currentProfile.emergencyContact) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "👑 Edit Admin Profile & ID",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = CircuitWarning
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Admin Full Name") },
                    leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = adminId,
                    onValueChange = { adminId = it },
                    label = { Text("Admin ID (e.g. RWH-ADM-2026-001)") },
                    leadingIcon = { Icon(Icons.Filled.Badge, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = designation,
                    onValueChange = { designation = it },
                    label = { Text("Designation / Title") },
                    leadingIcon = { Icon(Icons.Filled.WorkspacePremium, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = organization,
                    onValueChange = { organization = it },
                    label = { Text("Organization / Hub") },
                    leadingIcon = { Icon(Icons.Filled.Domain, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = deskStation,
                    onValueChange = { deskStation = it },
                    label = { Text("Control Station Desk") },
                    leadingIcon = { Icon(Icons.Filled.LocationOn, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Official Mobile Number") },
                    leadingIcon = { Icon(Icons.Filled.Phone, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = emergencyContact,
                    onValueChange = { emergencyContact = it },
                    label = { Text("Emergency Escalation Line") },
                    leadingIcon = { Icon(Icons.Filled.Emergency, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(fullName.trim(), phone.trim(), organization.trim(), designation.trim(), deskStation.trim(), adminId.trim(), emergencyContact.trim())
                },
                colors = ButtonDefaults.buttonColors(containerColor = CircuitWarning)
            ) {
                Text("Save Profile", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun compressAndEncodeImage(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val originalBitmap = BitmapFactory.decodeStream(inputStream)
        inputStream.close()

        val maxDimension = 600
        val scale = minOf(
            maxDimension.toFloat() / originalBitmap.width,
            maxDimension.toFloat() / originalBitmap.height,
            1.0f
        )
        val scaledBitmap = if (scale < 1.0f) {
            Bitmap.createScaledBitmap(
                originalBitmap,
                (originalBitmap.width * scale).toInt(),
                (originalBitmap.height * scale).toInt(),
                true
            )
        } else {
            originalBitmap
        }

        val outputStream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
        val byteArray = outputStream.toByteArray()
        Base64.encodeToString(byteArray, Base64.DEFAULT)
    } catch (e: Exception) {
        null
    }
}
