package com.roboticswala.hub.ui.screens.student.tabs

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Domain
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.roboticswala.hub.data.models.UserProfile
import com.roboticswala.hub.ui.components.RoboticsOutlinedButton
import com.roboticswala.hub.ui.components.StatusChip
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

// ─────────────────────────────────────────────────────────────────────────────
// StudentProfileScreen — Day 5: Real Firestore Profile Data
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun StudentProfileScreen(
    userProfile: UserProfile?,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val scrollState = rememberScrollState()
    val profile = userProfile ?: UserProfile()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // ── Profile Avatar ────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(ElectricBlue, CyberCyan)
                    )
                )
                .border(2.dp, if (isDark) CyberCyan else ElectricBlue, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (profile.photoUrl.isNotBlank()) {
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
                        fontSize = 28.sp
                    ),
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Name
        Text(
            text = profile.fullName.ifBlank { "Student" },
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

        StatusChip(status = "Approved")

        Spacer(modifier = Modifier.height(28.dp))

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
                Text(
                    text = "Student Information",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (isDark) CyberCyan else ElectricBlue
                )

                if (profile.studentId.isNotBlank()) {
                    ProfileInfoRow(
                        icon = Icons.Filled.Badge,
                        label = "Student ID",
                        value = profile.studentId,
                        isDark = isDark
                    )
                }

                ProfileInfoRow(
                    icon = Icons.Filled.Email,
                    label = "Email Address",
                    value = profile.email.ifBlank { "—" },
                    isDark = isDark
                )

                if (profile.college.isNotBlank()) {
                    ProfileInfoRow(
                        icon = Icons.Filled.Domain,
                        label = "College",
                        value = profile.college,
                        isDark = isDark
                    )
                }

                if (profile.branch.isNotBlank()) {
                    ProfileInfoRow(
                        icon = Icons.Filled.School,
                        label = "Branch",
                        value = profile.branch,
                        isDark = isDark
                    )
                }

                if (profile.year.isNotBlank()) {
                    ProfileInfoRow(
                        icon = Icons.Filled.Star,
                        label = "Year",
                        value = profile.year,
                        isDark = isDark
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Account Information Card (Read-Only locked fields) ────────────────
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Account Details",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (isDark) CyberCyan else ElectricBlue
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = "Read-only",
                        tint = if (isDark) TextSecondaryDark else TextSecondaryLight,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Read-only",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isDark) TextSecondaryDark else TextSecondaryLight
                    )
                }

                ProfileInfoRow(
                    icon = Icons.Filled.Person,
                    label = "Role",
                    value = profile.role,
                    isDark = isDark,
                    isLocked = true
                )

                ProfileInfoRow(
                    icon = Icons.Filled.CheckCircle,
                    label = "Account Status",
                    value = profile.status,
                    isDark = isDark,
                    isLocked = true,
                    valueColor = CircuitSuccess
                )

                if (profile.studentId.isNotBlank()) {
                    ProfileInfoRow(
                        icon = Icons.Filled.Badge,
                        label = "UID",
                        value = profile.uid.take(20) + "...",
                        isDark = isDark,
                        isLocked = true
                    )
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
