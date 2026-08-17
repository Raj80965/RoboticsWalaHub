package com.roboticswala.hub.ui.screens.student.tabs

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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Domain
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.roboticswala.hub.data.models.UserProfile
import com.roboticswala.hub.data.repository.FirestoreAchievementRepository
import com.roboticswala.hub.ui.components.RoboticsOutlinedButton
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
import java.io.ByteArrayOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

@Composable
fun StudentProfileScreen(
    userProfile: UserProfile?,
    onLogout: () -> Unit,
    onNavigateToAchievements: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val profile = userProfile ?: UserProfile()
    val achievementRepo = remember { FirestoreAchievementRepository() }
    val approvedAchievements by achievementRepo.observeApprovedAchievements(profile.uid).collectAsState(initial = emptyList())

    var isUploadingPhoto by remember { mutableStateOf(false) }
    var isUploadingAadhar by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showAadharPreviewDialog by remember { mutableStateOf(false) }

    // Image Picker Launcher that directly encodes to Base64 and updates Firestore (100% Free on Spark Plan)
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch(Dispatchers.IO) {
                try {
                    isUploadingPhoto = true
                    val base64Data = convertImageUriToBase64(context, uri)
                    if (base64Data == null) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Failed to read image", Toast.LENGTH_SHORT).show()
                        }
                        return@launch
                    }

                    val targetUid = profile.uid.ifBlank { FirebaseAuth.getInstance().currentUser?.uid.orEmpty() }
                    FirebaseFirestore.getInstance().collection("users")
                        .document(targetUid)
                        .update("photoUrl", base64Data)
                        .await()

                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Profile photo updated successfully!", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                } finally {
                    isUploadingPhoto = false
                }
            }
        }
    }

    val aadharPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch(Dispatchers.IO) {
                try {
                    isUploadingAadhar = true
                    val base64Data = convertImageUriToBase64(context, uri)
                    if (base64Data == null) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Failed to read document image", Toast.LENGTH_SHORT).show()
                        }
                        return@launch
                    }

                    val targetUid = profile.uid.ifBlank { FirebaseAuth.getInstance().currentUser?.uid.orEmpty() }
                    FirebaseFirestore.getInstance().collection("users")
                        .document(targetUid)
                        .update(
                            mapOf(
                                "aadharCardUrl" to base64Data,
                                "aadharUploadedAt" to System.currentTimeMillis()
                            )
                        )
                        .await()

                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Aadhaar Card uploaded successfully!", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Upload failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                } finally {
                    isUploadingAadhar = false
                }
            }
        }
    }

    val avatarBitmap = remember(profile.photoUrl) {
        if (profile.photoUrl.isNotBlank() && !profile.photoUrl.startsWith("http")) {
            try {
                val cleanBase64 = if (profile.photoUrl.contains(",")) {
                    profile.photoUrl.substringAfter(",")
                } else profile.photoUrl
                val bytes = Base64.decode(cleanBase64, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            } catch (e: Exception) {
                null
            }
        } else null
    }

    val aadharBitmap = remember(profile.aadharCardUrl) {
        if (profile.aadharCardUrl.isNotBlank() && !profile.aadharCardUrl.startsWith("http")) {
            try {
                val cleanBase64 = if (profile.aadharCardUrl.contains(",")) {
                    profile.aadharCardUrl.substringAfter(",")
                } else profile.aadharCardUrl
                val bytes = Base64.decode(cleanBase64, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            } catch (e: Exception) {
                null
            }
        } else null
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // ── Profile Avatar with Photo Upload Button ────────────────────────────
        Box(
            modifier = Modifier.size(105.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(ElectricBlue, CyberCyan)
                        )
                    )
                    .border(2.5.dp, if (isDark) CyberCyan else ElectricBlue, CircleShape)
                    .clickable {
                        imagePickerLauncher.launch("image/*")
                    },
                contentAlignment = Alignment.Center
            ) {
                if (isUploadingPhoto) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        color = Color.White,
                        strokeWidth = 3.dp
                    )
                } else if (avatarBitmap != null) {
                    androidx.compose.foundation.Image(
                        bitmap = avatarBitmap.asImageBitmap(),
                        contentDescription = "Profile Photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                } else if (profile.photoUrl.startsWith("http")) {
                    AsyncImage(
                        model = profile.photoUrl,
                        contentDescription = "Profile Photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                } else {
                    Text(
                        text = profile.initials,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 30.sp
                        ),
                        color = Color.White
                    )
                }
            }

            // Camera Action Badge
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(if (isDark) CyberCyan else ElectricBlue)
                    .border(2.dp, if (isDark) DarkSurface else LightSurface, CircleShape)
                    .clickable { imagePickerLauncher.launch("image/*") },
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

        // Name
        Text(
            text = profile.fullName.ifBlank { "Student Engineer" },
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = if (isDark) TextPrimaryDark else TextPrimaryLight
        )

        // Branch + Year badge
        val roleBadge = buildString {
            if (profile.branch.isNotBlank()) append(profile.branch)
            if (profile.year.isNotBlank()) {
                if (isNotEmpty()) append(" • ")
                append(profile.year)
            }
        }
        if (roleBadge.isNotBlank()) {
            Text(
                text = roleBadge,
                style = MaterialTheme.typography.bodySmall,
                color = if (isDark) CyberCyan else ElectricBlue
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        StatusChip(status = profile.status.ifBlank { "Approved" })

        Spacer(modifier = Modifier.height(24.dp))

        // ── Student Information Card ──────────────────────────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = if (isDark) DarkSurfaceBorder else LightSurfaceBorder,
                    shape = RoundedCornerShape(18.dp)
                ),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDark) DarkSurface.copy(alpha = 0.95f) else LightSurface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Student Information",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (isDark) CyberCyan else ElectricBlue
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { showEditProfileDialog = true }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Edit Profile",
                            tint = if (isDark) CyberCyan else ElectricBlue,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Edit",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (isDark) CyberCyan else ElectricBlue
                        )
                    }
                }

                ProfileInfoRow(
                    icon = Icons.Filled.Badge,
                    label = "Student ID",
                    value = profile.displayStudentId,
                    isDark = isDark,
                    valueColor = if (isDark) CyberCyan else ElectricBlue
                )

                ProfileInfoRow(
                    icon = Icons.Filled.Email,
                    label = "Email Address",
                    value = profile.email.ifBlank { "—" },
                    isDark = isDark
                )

                ProfileInfoRow(
                    icon = Icons.Filled.Domain,
                    label = "College / Institution",
                    value = profile.college.ifBlank { "Robotics Innovation Lab" },
                    isDark = isDark
                )

                ProfileInfoRow(
                    icon = Icons.Filled.School,
                    label = "Branch / Specialization",
                    value = profile.branch.ifBlank { "Robotics & Automation Engineering" },
                    isDark = isDark
                )

                ProfileInfoRow(
                    icon = Icons.Filled.Star,
                    label = "Academic Year",
                    value = profile.year.ifBlank { "Year 3" },
                    isDark = isDark
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Contact & Parents Information Card ───────────────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = if (isDark) DarkSurfaceBorder else LightSurfaceBorder,
                    shape = RoundedCornerShape(18.dp)
                ),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDark) DarkSurface.copy(alpha = 0.95f) else LightSurface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Contact & Guardian Details",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (isDark) CyberCyan else ElectricBlue
                )

                ProfileInfoRow(
                    icon = Icons.Filled.Phone,
                    label = "Student Mobile Number",
                    value = profile.phone.ifBlank { "Not provided" },
                    isDark = isDark
                )

                ProfileInfoRow(
                    icon = Icons.Filled.FamilyRestroom,
                    label = "Parent / Guardian Name",
                    value = profile.parentName.ifBlank { "Not provided" },
                    isDark = isDark
                )

                ProfileInfoRow(
                    icon = Icons.Filled.ContactPhone,
                    label = "Parent's Mobile Number",
                    value = profile.parentPhone.ifBlank { "Not provided" },
                    isDark = isDark
                )

                ProfileInfoRow(
                    icon = Icons.Filled.Emergency,
                    label = "Emergency Contact",
                    value = profile.emergencyContact.ifBlank { profile.parentPhone.ifBlank { "Not provided" } },
                    isDark = isDark
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Identity & Aadhaar Card Documentation ───────────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = if (isDark) DarkSurfaceBorder else LightSurfaceBorder,
                    shape = RoundedCornerShape(18.dp)
                ),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDark) DarkSurface.copy(alpha = 0.95f) else LightSurface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Identity & Aadhaar Card",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (isDark) CyberCyan else ElectricBlue
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (profile.aadharCardUrl.isNotBlank()) CircuitSuccess.copy(alpha = 0.15f) else CircuitWarning.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = if (profile.aadharCardUrl.isNotBlank()) "UPLOADED" else "PENDING UPLOAD",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (profile.aadharCardUrl.isNotBlank()) CircuitSuccess else CircuitWarning
                        )
                    }
                }

                ProfileInfoRow(
                    icon = Icons.Filled.Badge,
                    label = "Aadhaar Card Number",
                    value = if (profile.aadharNumber.isNotBlank()) profile.aadharNumber else "Not added (Edit to add)",
                    isDark = isDark
                )

                if (profile.aadharCardUrl.isNotBlank()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showAadharPreviewDialog = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("👁️ View Document", fontSize = 12.sp)
                        }

                        Button(
                            onClick = { aadharPickerLauncher.launch("image/*") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            enabled = !isUploadingAadhar
                        ) {
                            Text(if (isUploadingAadhar) "Uploading..." else "🔄 Replace", fontSize = 12.sp)
                        }
                    }
                } else {
                    Button(
                        onClick = { aadharPickerLauncher.launch("image/*") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isUploadingAadhar
                    ) {
                        Text(if (isUploadingAadhar) "Uploading Document..." else "📤 Upload Aadhaar Card (Image)")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Account & Security Card ───────────────────────────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = if (isDark) DarkSurfaceBorder else LightSurfaceBorder,
                    shape = RoundedCornerShape(18.dp)
                ),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDark) DarkSurfaceElevated else LightSurfaceElevated
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Account & System Security",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (isDark) TextPrimaryDark else TextPrimaryLight
                )

                ProfileInfoRow(
                    icon = Icons.Filled.Security,
                    label = "User Role",
                    value = profile.role.ifBlank { "STUDENT" },
                    isDark = isDark,
                    isLocked = true
                )

                ProfileInfoRow(
                    icon = Icons.Filled.CheckCircle,
                    label = "Account Status",
                    value = if (profile.isApproved) "Active & Verified" else profile.status.ifBlank { "Pending Review" },
                    isDark = isDark,
                    isLocked = true
                )

                ProfileInfoRow(
                    icon = Icons.Filled.DateRange,
                    label = "Lab Attendance Status",
                    value = "Tracked via QR Check-in (≥85% Target)",
                    isDark = isDark,
                    valueColor = CircuitSuccess
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Verified Achievements Showcase ───────────────────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = if (isDark) DarkSurfaceBorder else LightSurfaceBorder,
                    shape = RoundedCornerShape(18.dp)
                ),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDark) DarkSurface.copy(alpha = 0.95f) else LightSurface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.EmojiEvents,
                            contentDescription = null,
                            tint = if (isDark) CyberCyan else ElectricBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Verified Achievements (${approvedAchievements.size})",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (isDark) CyberCyan else ElectricBlue
                        )
                    }

                    Text(
                        text = "Manage ➔",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (isDark) CyberCyan else ElectricBlue,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable(onClick = onNavigateToAchievements)
                            .padding(4.dp)
                    )
                }

                if (approvedAchievements.isEmpty()) {
                    Text(
                        text = "No approved achievements yet. Submit certificates from the Achievements hub.",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isDark) TextSecondaryDark else TextSecondaryLight
                    )
                } else {
                    approvedAchievements.forEach { item ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isDark) DarkSurfaceElevated else LightSurfaceElevated)
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = if (isDark) TextPrimaryDark else TextPrimaryLight,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(CircuitSuccess.copy(alpha = 0.15f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(text = "VERIFIED", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = CircuitSuccess)
                                    }
                                }
                                Text(
                                    text = "${item.category} • ${item.achievementLevel} • 📅 ${item.achievementDate}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isDark) TextSecondaryDark else TextSecondaryLight,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // ── Logout Button ─────────────────────────────────────────────────────
        RoboticsOutlinedButton(
            text = "Log Out of Hub",
            onClick = onLogout,
            leadingIcon = Icons.Filled.Logout
        )

        Spacer(modifier = Modifier.height(40.dp))
    }

    // ── Edit Profile Dialog ───────────────────────────────────────────────────
    if (showEditProfileDialog) {
        var editName by remember { mutableStateOf(profile.fullName) }
        var editStudentId by remember { mutableStateOf(profile.studentId.ifBlank { profile.displayStudentId }) }
        var editCollege by remember { mutableStateOf(profile.college) }
        var editBranch by remember { mutableStateOf(profile.branch) }
        var editYear by remember { mutableStateOf(profile.year) }
        var editPhone by remember { mutableStateOf(profile.phone) }
        var editParentName by remember { mutableStateOf(profile.parentName) }
        var editParentPhone by remember { mutableStateOf(profile.parentPhone) }
        var editEmergencyContact by remember { mutableStateOf(profile.emergencyContact) }
        var editAadharNumber by remember { mutableStateOf(profile.aadharNumber) }
        var isSaving by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { if (!isSaving) showEditProfileDialog = false },
            title = { Text("Edit Student Profile") },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Full Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editStudentId,
                        onValueChange = { editStudentId = it },
                        label = { Text("Student ID (e.g. RWH-2026-042)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editAadharNumber,
                        onValueChange = { editAadharNumber = it },
                        label = { Text("Aadhaar Card Number (12 Digits)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editPhone,
                        onValueChange = { editPhone = it },
                        label = { Text("Student Mobile Number") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editParentName,
                        onValueChange = { editParentName = it },
                        label = { Text("Parent / Guardian Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editParentPhone,
                        onValueChange = { editParentPhone = it },
                        label = { Text("Parent's Mobile Number") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editEmergencyContact,
                        onValueChange = { editEmergencyContact = it },
                        label = { Text("Emergency Contact Number") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editCollege,
                        onValueChange = { editCollege = it },
                        label = { Text("College / Institute") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editBranch,
                        onValueChange = { editBranch = it },
                        label = { Text("Branch / Department") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editYear,
                        onValueChange = { editYear = it },
                        label = { Text("Academic Year (e.g. Year 3)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val targetUid = profile.uid.ifBlank { FirebaseAuth.getInstance().currentUser?.uid.orEmpty() }
                        scope.launch {
                            try {
                                isSaving = true
                                FirebaseFirestore.getInstance().collection("users")
                                    .document(targetUid)
                                    .update(
                                        mapOf(
                                            "fullName" to editName.trim(),
                                            "studentId" to editStudentId.trim(),
                                            "phone" to editPhone.trim(),
                                            "parentName" to editParentName.trim(),
                                            "parentPhone" to editParentPhone.trim(),
                                            "emergencyContact" to editEmergencyContact.trim(),
                                            "aadharNumber" to editAadharNumber.trim(),
                                            "college" to editCollege.trim(),
                                            "branch" to editBranch.trim(),
                                            "year" to editYear.trim()
                                        )
                                    )
                                    .await()
                                Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                                showEditProfileDialog = false
                            } catch (e: Exception) {
                                Toast.makeText(context, "Failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                            } finally {
                                isSaving = false
                            }
                        }
                    },
                    enabled = !isSaving
                ) {
                    Text(if (isSaving) "Saving..." else "Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }, enabled = !isSaving) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showAadharPreviewDialog) {
        AlertDialog(
            onDismissRequest = { showAadharPreviewDialog = false },
            title = {
                Text("🪪 Aadhaar Card Document")
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
                        text = "Aadhaar No: ${if (profile.aadharNumber.isNotBlank()) profile.aadharNumber else "Not added"}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = if (isDark) CyberCyan else ElectricBlue
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showAadharPreviewDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

/**
 * Converts image Uri to a compressed Base64 JPEG data string.
 * Completely free, bypasses Cloud Storage billing limits, and saves directly in Firestore.
 */
private fun convertImageUriToBase64(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val originalBitmap = BitmapFactory.decodeStream(inputStream)
        inputStream.close() ?: return null

        // Scale down to max 280x280 for crisp avatar & low Firestore footprint (<25KB)
        val maxDimension = 280
        val width = originalBitmap.width
        val height = originalBitmap.height
        val ratio = width.toFloat() / height.toFloat()

        val newWidth: Int
        val newHeight: Int
        if (width > height) {
            newWidth = maxDimension
            newHeight = (maxDimension / ratio).toInt()
        } else {
            newHeight = maxDimension
            newWidth = (maxDimension * ratio).toInt()
        }

        val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)
        val outputStream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        val bytes = outputStream.toByteArray()

        "data:image/jpeg;base64," + Base64.encodeToString(bytes, Base64.NO_WRAP)
    } catch (e: Exception) {
        null
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Profile Info Row
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ProfileInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    isDark: Boolean,
    isLocked: Boolean = false,
    valueColor: Color? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isDark) CyberCyan else ElectricBlue,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = if (isDark) TextSecondaryDark else TextSecondaryLight
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = valueColor ?: if (isDark) TextPrimaryDark else TextPrimaryLight
            )
        }
        if (isLocked) {
            Icon(
                imageVector = Icons.Filled.Lock,
                contentDescription = "Locked",
                tint = if (isDark) TextSecondaryDark.copy(alpha = 0.5f) else TextSecondaryLight.copy(alpha = 0.5f),
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
