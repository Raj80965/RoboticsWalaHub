package com.roboticswala.hub.ui.screens.student.projects

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.roboticswala.hub.data.models.Project
import com.roboticswala.hub.data.models.ProjectTeamMember
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateProjectScreen(
    studentProfile: UserProfile,
    onNavigateBack: () -> Unit,
    onProjectCreated: () -> Unit,
    viewModel: CreateProjectViewModel = viewModel()
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

    var showAddMemberDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onProjectCreated()
        }
    }

    val cal = remember { Calendar.getInstance() }

    // Start Date Picker
    val startDatePicker = remember {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                val formatted = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, day)
                viewModel.onStartDateChange(formatted)
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )
    }

    // Expected Date Picker
    val expectedDatePicker = remember {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                val formatted = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, day)
                viewModel.onExpectedDateChange(formatted)
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH)
        )
    }

    Scaffold(
        containerColor = bgColor,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Create New Project",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = textColor
                        )
                        Text(
                            text = "Register hardware or software project in lab registry",
                            style = MaterialTheme.typography.labelSmall,
                            color = textSecColor
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = textColor
                        )
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Error Message
            if (!uiState.errorMessage.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(CircuitError.copy(alpha = 0.15f))
                        .border(1.dp, CircuitError.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "⚠️ ${uiState.errorMessage}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = CircuitError
                    )
                }
            }

            // 1. BASIC DETAILS
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = surfaceColor),
                border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Project Overview",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = textColor
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    RoboticsTextField(
                        value = uiState.title,
                        onValueChange = viewModel::onTitleChange,
                        label = "Project Title *",
                        placeholder = "e.g. Autonomous SLAM Quadruped Robot"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    RoboticsTextField(
                        value = uiState.description,
                        onValueChange = viewModel::onDescriptionChange,
                        label = "Project Description *",
                        placeholder = "Describe technical architecture, objectives & testing methodology",
                        singleLine = false
                    )
                }
            }

            // 2. CATEGORY & TYPE
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = surfaceColor),
                border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Domain & Category *",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = textColor
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Project.ALL_CATEGORIES.forEach { cat ->
                            val isSelected = uiState.category == cat
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.onCategoryChange(cat) },
                                label = { Text(cat, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = if (isDark) CyberCyan else ElectricBlue,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Project Type",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = textColor
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Project.ALL_TYPES.forEach { t ->
                            val isSelected = uiState.type == t
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.onTypeChange(t) },
                                label = { Text(t, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = if (isDark) CyberCyan.copy(alpha = 0.3f) else ElectricBlue.copy(alpha = 0.2f),
                                    selectedLabelColor = if (isDark) CyberCyan else ElectricBlue
                                )
                            )
                        }
                    }
                }
            }

            // 3. TEAM MEMBERS
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = surfaceColor),
                border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Team Members (${uiState.teamMembers.size + 1})",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = textColor
                        )

                        Text(
                            text = "+ Add Member",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (isDark) CyberCyan else ElectricBlue,
                            modifier = Modifier
                                .clickable { showAddMemberDialog = true }
                                .padding(4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Project Owner Row
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(elevatedColor)
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = if (isDark) CyberCyan else ElectricBlue,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${studentProfile.fullName} (You)",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = textColor
                                )
                                Text(
                                    text = "Role: Project Owner • ${studentProfile.studentId}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = textSecColor
                                )
                            }
                        }
                    }

                    // Added Team Members
                    uiState.teamMembers.forEach { member ->
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(elevatedColor)
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Group,
                                    contentDescription = null,
                                    tint = textSecColor,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = member.studentName,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                        color = textColor
                                    )
                                    Text(
                                        text = "Role: ${member.role} • ${member.studentId}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = textSecColor
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.removeTeamMember(member.studentUid) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove",
                                        tint = CircuitError,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 4. TIMELINE & PROGRESS
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = surfaceColor),
                border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Timeline & Initial Progress",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = textColor
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Start Date
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Start Date", style = MaterialTheme.typography.labelSmall, color = textSecColor)
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(elevatedColor)
                                    .clickable { startDatePicker.show() }
                                    .padding(10.dp)
                            ) {
                                Text(text = uiState.startDate, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = textColor)
                            }
                        }

                        // Target Date
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Expected End Date", style = MaterialTheme.typography.labelSmall, color = textSecColor)
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(elevatedColor)
                                    .clickable { expectedDatePicker.show() }
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = uiState.expectedCompletionDate.ifBlank { "Select Date" },
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (uiState.expectedCompletionDate.isBlank()) textSecColor else textColor
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Initial Progress: ${uiState.progressPercentage}%",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = textColor
                    )
                    Slider(
                        value = uiState.progressPercentage.toFloat(),
                        onValueChange = { viewModel.onProgressChange(it.toInt()) },
                        valueRange = 0f..100f,
                        colors = SliderDefaults.colors(
                            thumbColor = if (isDark) CyberCyan else ElectricBlue,
                            activeTrackColor = if (isDark) CyberCyan else ElectricBlue
                        )
                    )
                }
            }

            // 5. COMPONENTS, BUDGET & GITHUB
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = surfaceColor),
                border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Hardware, Budget & Links",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = textColor
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    RoboticsTextField(
                        value = uiState.requiredComponents,
                        onValueChange = viewModel::onRequiredComponentsChange,
                        label = "Required Components",
                        placeholder = "e.g. Jetson Orin Nano, RPLiDAR A2, 4x BLDC Motors"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        RoboticsTextField(
                            value = uiState.estimatedBudget,
                            onValueChange = viewModel::onEstimatedBudgetChange,
                            label = "Budget (INR ₹)",
                            placeholder = "15000",
                            modifier = Modifier.weight(1f)
                        )

                        RoboticsTextField(
                            value = uiState.mentorName,
                            onValueChange = viewModel::onMentorNameChange,
                            label = "Mentor / Faculty",
                            placeholder = "Prof. Sharma",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    RoboticsTextField(
                        value = uiState.githubLink,
                        onValueChange = viewModel::onGithubLinkChange,
                        label = "GitHub Repository URL",
                        placeholder = "https://github.com/org/repo",
                        leadingIcon = Icons.Default.Link
                    )
                }
            }

            // SUBMIT BUTTON
            RoboticsPrimaryButton(
                text = if (uiState.isSubmitting) "Creating Project..." else "🚀 Save Project to Registry",
                onClick = { viewModel.createProject(studentProfile) },
                isLoading = uiState.isSubmitting,
                enabled = uiState.title.isNotBlank() && uiState.description.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // ADD TEAM MEMBER MODAL
    if (showAddMemberDialog) {
        AddTeamMemberDialog(
            availableStudents = uiState.availableStudents.filter { it.uid != studentProfile.uid },
            onDismiss = { showAddMemberDialog = false },
            onAddMember = { student, role ->
                viewModel.addTeamMember(student, role)
                showAddMemberDialog = false
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddTeamMemberDialog(
    availableStudents: List<UserProfile>,
    onDismiss: () -> Unit,
    onAddMember: (UserProfile, String) -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val textColor = if (isDark) TextPrimaryDark else TextPrimaryLight
    val textSecColor = if (isDark) TextSecondaryDark else TextSecondaryLight

    var selectedStudent by remember { mutableStateOf(availableStudents.firstOrNull()) }
    var selectedRole by remember { mutableStateOf(ProjectTeamMember.ROLE_DEVELOPER) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add Team Member",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Select an approved student from lab directory:",
                    style = MaterialTheme.typography.bodySmall,
                    color = textSecColor
                )

                if (availableStudents.isEmpty()) {
                    Text(
                        text = "No other approved students found in directory.",
                        style = MaterialTheme.typography.bodySmall,
                        color = textSecColor
                    )
                } else {
                    availableStudents.forEach { student ->
                        val isSelected = selectedStudent?.uid == student.uid
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) (if (isDark) CyberCyan.copy(alpha = 0.2f) else ElectricBlue.copy(alpha = 0.15f)) else Color.Transparent)
                                .border(1.dp, if (isSelected) (if (isDark) CyberCyan else ElectricBlue) else Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                                .clickable { selectedStudent = student }
                                .padding(8.dp)
                        ) {
                            Column {
                                Text(
                                    text = student.fullName,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = textColor
                                )
                                Text(
                                    text = "ID: ${student.studentId} • ${student.branch}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = textSecColor
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Assign Role:",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = textColor
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    ProjectTeamMember.ALL_ROLES.filter { it != ProjectTeamMember.ROLE_OWNER }.forEach { role ->
                        val isSelected = selectedRole == role
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedRole = role },
                            label = { Text(role, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = if (isDark) CyberCyan else ElectricBlue,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            RoboticsPrimaryButton(
                text = "Add to Project",
                onClick = {
                    selectedStudent?.let { onAddMember(it, selectedRole) }
                },
                enabled = selectedStudent != null
            )
        },
        dismissButton = {
            RoboticsOutlinedButton(
                text = "Cancel",
                onClick = onDismiss
            )
        }
    )
}
