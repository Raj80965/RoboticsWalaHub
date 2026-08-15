package com.roboticswala.hub.ui.screens.student.achievements

import android.app.DatePickerDialog
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.roboticswala.hub.data.models.Achievement
import com.roboticswala.hub.data.models.UserProfile
import com.roboticswala.hub.ui.components.RoboticsOutlinedButton
import com.roboticswala.hub.ui.components.RoboticsPrimaryButton
import com.roboticswala.hub.ui.components.RoboticsTextField
import com.roboticswala.hub.ui.theme.CircuitError
import com.roboticswala.hub.ui.theme.CircuitSuccess
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
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAchievementScreen(
    userProfile: UserProfile,
    existingAchievement: Achievement? = null,
    onNavigateBack: () -> Unit,
    viewModel: CreateAchievementViewModel = viewModel()
) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    val bgColor = if (isDark) DarkBackground else LightBackground
    val surfaceColor = if (isDark) DarkSurface else LightSurface
    val elevatedColor = if (isDark) DarkSurfaceElevated else LightSurfaceElevated
    val borderColor = if (isDark) DarkSurfaceBorder else LightSurfaceBorder
    val textColor = if (isDark) TextPrimaryDark else TextPrimaryLight
    val textSecColor = if (isDark) TextSecondaryDark else TextSecondaryLight

    LaunchedEffect(existingAchievement) {
        existingAchievement?.let {
            viewModel.onTitleChange(it.title)
            viewModel.onCategoryChange(it.category)
            viewModel.onDescriptionChange(it.description)
            viewModel.onDateChange(it.achievementDate)
            viewModel.onOrganizationChange(it.organizationName)
            viewModel.onLevelChange(it.achievementLevel)
            viewModel.onLinkChange(it.verificationLink)
        }
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onNavigateBack()
        }
    }

    val cal = remember { Calendar.getInstance() }
    val datePicker = remember {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                val formatted = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, day)
                viewModel.onDateChange(formatted)
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.maxDate = System.currentTimeMillis() // No future dates allowed
        }
    }

    Scaffold(
        containerColor = bgColor,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (existingAchievement != null) "Edit Achievement" else "Submit Achievement",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = textColor
                        )
                        Text(
                            text = "Add verified certifications, competition awards & publications",
                            style = MaterialTheme.typography.labelSmall,
                            color = textSecColor
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = textColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = surfaceColor)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (!uiState.errorMessage.isNullOrBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CircuitError.copy(alpha = 0.15f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CircuitError)
                ) {
                    Text(
                        text = "⚠️ ${uiState.errorMessage}",
                        color = CircuitError,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // Title & Category
            RoboticsTextField(
                value = uiState.title,
                onValueChange = viewModel::onTitleChange,
                label = "Achievement Title *",
                placeholder = "e.g. 1st Place - National Robotics Championship 2026"
            )

            Text(text = "Category *", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = textColor)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                items(Achievement.ALL_CATEGORIES) { cat ->
                    val isSelected = uiState.category == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.onCategoryChange(cat) },
                        label = { Text(cat, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = if (isDark) CyberCyan.copy(alpha = 0.25f) else ElectricBlue.copy(alpha = 0.2f),
                            selectedLabelColor = if (isDark) CyberCyan else ElectricBlue
                        )
                    )
                }
            }

            // Organization & Level
            RoboticsTextField(
                value = uiState.organizationName,
                onValueChange = viewModel::onOrganizationChange,
                label = "Issuing Organization / Platform *",
                placeholder = "e.g. IEEE Robotics Society / IIT Bombay"
            )

            Text(text = "Achievement Level *", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = textColor)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                items(Achievement.ALL_LEVELS) { lvl ->
                    val isSelected = uiState.achievementLevel == lvl
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.onLevelChange(lvl) },
                        label = { Text(lvl, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = if (isDark) CyberCyan else ElectricBlue,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Date picker
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Achievement Date *", style = MaterialTheme.typography.labelSmall, color = textSecColor)
                    Text(text = "📅 ${uiState.achievementDate}", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = textColor)
                }
                RoboticsOutlinedButton(
                    text = "Pick Date",
                    onClick = { datePicker.show() },
                    height = 36.dp
                )
            }

            // Description
            RoboticsTextField(
                value = uiState.description,
                onValueChange = viewModel::onDescriptionChange,
                label = "Description / Summary",
                placeholder = "Describe key highlights, team contribution, or paper findings...",
                singleLine = false
            )

            // Certificate Upload Simulation
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = elevatedColor),
                border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "Certificate / Proof Document", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = textColor)
                    Spacer(modifier = Modifier.height(6.dp))
                    if (uiState.certificateFileName.isNotBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = CircuitSuccess, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = uiState.certificateFileName, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = textColor)
                        }
                    } else {
                        RoboticsOutlinedButton(
                            text = "📎 Attach PDF / JPG Certificate",
                            onClick = {
                                viewModel.setCertificateFile(
                                    fileName = "Certificate_${uiState.title.take(10).replace(" ", "_")}.pdf",
                                    bytes = byteArrayOf(1, 2, 3)
                                )
                            },
                            height = 36.dp
                        )
                    }
                }
            }

            // Verification Link
            RoboticsTextField(
                value = uiState.verificationLink,
                onValueChange = viewModel::onLinkChange,
                label = "External Verification Link (Optional)",
                placeholder = "https://credentials.ieee.org/verify/..."
            )

            Spacer(modifier = Modifier.height(10.dp))

            RoboticsPrimaryButton(
                text = if (uiState.isUploading) "Submitting..." else if (existingAchievement != null) "Update & Resubmit" else "Submit for Admin Approval",
                onClick = { viewModel.submitAchievement(userProfile, existingAchievement) },
                isLoading = uiState.isUploading,
                enabled = uiState.title.isNotBlank() && uiState.organizationName.isNotBlank()
            )

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
