package com.roboticswala.hub.ui.screens.student.qr

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.roboticswala.hub.data.models.AttendanceRecord
import com.roboticswala.hub.data.models.AttendanceScanResult
import com.roboticswala.hub.data.models.UserProfile
import com.roboticswala.hub.ui.components.CameraQRScanner
import com.roboticswala.hub.ui.components.RoboticsPrimaryButton
import com.roboticswala.hub.ui.components.RoboticsOutlinedButton
import com.roboticswala.hub.ui.components.RoboticsTextField
import com.roboticswala.hub.ui.screens.student.attendance.StudentAttendanceViewModel
import com.roboticswala.hub.ui.theme.CircuitError
import com.roboticswala.hub.ui.theme.CircuitSuccess
import com.roboticswala.hub.ui.theme.CircuitWarning
import com.roboticswala.hub.ui.theme.CyberCyan
import com.roboticswala.hub.ui.theme.DarkBackground
import com.roboticswala.hub.ui.theme.DarkSurface
import com.roboticswala.hub.ui.theme.DarkSurfaceBorder
import com.roboticswala.hub.ui.theme.DarkSurfaceElevated
import com.roboticswala.hub.ui.theme.ElectricBlue
import com.roboticswala.hub.ui.theme.LightBackground
import com.roboticswala.hub.ui.theme.LightSurface
import com.roboticswala.hub.ui.theme.LightSurfaceBorder
import com.roboticswala.hub.ui.theme.LightSurfaceElevated
import com.roboticswala.hub.ui.theme.TextPrimaryDark
import com.roboticswala.hub.ui.theme.TextPrimaryLight
import com.roboticswala.hub.ui.theme.TextSecondaryDark
import com.roboticswala.hub.ui.theme.TextSecondaryLight
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentQRScannerScreen(
    studentProfile: UserProfile,
    viewModel: StudentAttendanceViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isDark = isSystemInDarkTheme()

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    var showManualInputDialog by remember { mutableStateOf(false) }
    var manualPayloadInput by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Scaffold(
        containerColor = if (isDark) DarkBackground else LightBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Scan Attendance QR",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (isDark) TextPrimaryDark else TextPrimaryLight
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = if (isDark) TextPrimaryDark else TextPrimaryLight
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showManualInputDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "Manual Code",
                            tint = if (isDark) CyberCyan else ElectricBlue
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDark) DarkBackground.copy(alpha = 0.95f) else LightBackground.copy(alpha = 0.95f)
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (hasCameraPermission) {
                CameraQRScanner(
                    isScanningEnabled = !uiState.isProcessingScan && uiState.scanResult == null,
                    onQRCodeScanned = { scannedPayload ->
                        viewModel.onQRScanned(scannedPayload, studentProfile)
                    }
                )
            } else {
                // Permission Denied Fallback UI
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(CircuitWarning.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Camera",
                            modifier = Modifier.size(40.dp),
                            tint = CircuitWarning
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Camera Permission Required",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (isDark) TextPrimaryDark else TextPrimaryLight,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "To scan the lab attendance QR code, please grant camera permission or enter the session code manually.",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isDark) TextSecondaryDark else TextSecondaryLight,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    RoboticsPrimaryButton(
                        text = "Grant Camera Permission",
                        onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    RoboticsOutlinedButton(
                        text = "Enter QR Code Manually",
                        onClick = { showManualInputDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Processing Indicator
            if (uiState.isProcessingScan) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.6f)),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = if (isDark) DarkSurfaceElevated else LightSurfaceElevated)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                color = if (isDark) CyberCyan else ElectricBlue,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Validating QR with Firebase...",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (isDark) TextPrimaryDark else TextPrimaryLight
                            )
                        }
                    }
                }
            }

            // Result Alert Dialog
            uiState.scanResult?.let { result ->
                AttendanceResultDialog(
                    result = result,
                    onDismiss = {
                        viewModel.clearScanResult()
                        if (result !is AttendanceScanResult.Error) {
                            onNavigateBack()
                        }
                    }
                )
            }

            // Manual Code Input Dialog (For Testing/Emulators)
            if (showManualInputDialog) {
                AlertDialog(
                    onDismissRequest = { showManualInputDialog = false },
                    title = { Text("Manual QR Payload") },
                    text = {
                        Column {
                            Text(
                                text = "Paste or enter the session QR string below:",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            RoboticsTextField(
                                value = manualPayloadInput,
                                onValueChange = { manualPayloadInput = it },
                                label = "QR Payload",
                                placeholder = "ROBOTICS_WALA_ATTENDANCE|...",
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    },
                    confirmButton = {
                        RoboticsPrimaryButton(
                            text = "Submit Code",
                            onClick = {
                                showManualInputDialog = false
                                viewModel.onQRScanned(manualPayloadInput, studentProfile)
                            }
                        )
                    },
                    dismissButton = {
                        RoboticsOutlinedButton(
                            text = "Cancel",
                            onClick = { showManualInputDialog = false }
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun AttendanceResultDialog(
    result: AttendanceScanResult,
    onDismiss: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    val title: String
    val icon: androidx.compose.ui.graphics.vector.ImageVector
    val iconColor: Color
    val description: String

    when (result) {
        is AttendanceScanResult.CheckInSuccess -> {
            val rec = result.record
            title = "Check-In Successful! 🎉"
            icon = Icons.Default.CheckCircle
            iconColor = CircuitSuccess
            description = "Lab: ${rec.labName}\nCheck-In Time: ${timeFormat.format(Date(rec.checkInTime))}\n\nStatus: Checked In (Active Lab Session)"
        }
        is AttendanceScanResult.CheckOutSuccess -> {
            val rec = result.record
            val inTime = timeFormat.format(Date(rec.checkInTime))
            val outTime = rec.checkOutTime?.let { timeFormat.format(Date(it)) } ?: "Now"
            title = "Check-Out Successful! 🏁"
            icon = Icons.Default.CheckCircle
            iconColor = CyberCyan
            description = "Lab: ${rec.labName}\nCheck-In: $inTime\nCheck-Out: $outTime\n\nTotal Working Time: ${rec.formattedDuration}"
        }
        is AttendanceScanResult.AlreadyCompleted -> {
            title = "Attendance Already Completed"
            icon = Icons.Default.Info
            iconColor = CircuitWarning
            description = "You have already completed your Check-In and Check-Out for this lab session.\n\nTotal: ${result.record.formattedDuration}"
        }
        is AttendanceScanResult.Error -> {
            title = "Scan Verification Failed"
            icon = Icons.Default.ErrorOutline
            iconColor = CircuitError
            description = result.message
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(48.dp)
            )
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
            )
        },
        text = {
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            RoboticsPrimaryButton(
                text = "Continue",
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            )
        }
    )
}
