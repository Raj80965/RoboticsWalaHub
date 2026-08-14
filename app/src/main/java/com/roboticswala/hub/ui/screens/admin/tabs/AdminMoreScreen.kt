package com.roboticswala.hub.ui.screens.admin.tabs

import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.roboticswala.hub.ui.components.RoboticsOutlinedButton
import com.roboticswala.hub.ui.theme.CyberCyan
import com.roboticswala.hub.ui.theme.DarkSurface
import com.roboticswala.hub.ui.theme.DarkSurfaceBorder
import com.roboticswala.hub.ui.theme.ElectricBlue
import com.roboticswala.hub.ui.theme.LightSurface
import com.roboticswala.hub.ui.theme.LightSurfaceBorder
import com.roboticswala.hub.ui.theme.TextPrimaryDark
import com.roboticswala.hub.ui.theme.TextPrimaryLight
import com.roboticswala.hub.ui.theme.TextSecondaryDark
import com.roboticswala.hub.ui.theme.TextSecondaryLight

@Composable
fun AdminMoreScreen(
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Admin Settings & Management",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = if (isDark) TextPrimaryDark else TextPrimaryLight
        )
        Text(
            text = "Facility controls, inventory management, and broadcast alerts",
            style = MaterialTheme.typography.bodySmall,
            color = if (isDark) TextSecondaryDark else TextSecondaryLight
        )

        Spacer(modifier = Modifier.height(18.dp))

        AdminOptionCard(
            title = "Inventory & Equipment Stock",
            subtitle = "Manage sensors, LiPo batteries, actuators, and controllers",
            icon = Icons.Filled.Inventory2,
            isDark = isDark
        )

        Spacer(modifier = Modifier.height(12.dp))

        AdminOptionCard(
            title = "Broadcast Lab Notice",
            subtitle = "Publish facility alerts, safety guidelines & workshop dates",
            icon = Icons.Filled.Campaign,
            isDark = isDark
        )

        Spacer(modifier = Modifier.height(12.dp))

        AdminOptionCard(
            title = "Security & RFID Gate Log",
            subtitle = "Live biometric access records and bay telemetry streams",
            icon = Icons.Filled.Security,
            isDark = isDark
        )

        Spacer(modifier = Modifier.height(12.dp))

        AdminOptionCard(
            title = "Robotics Lab Preferences",
            subtitle = "Configure station operating hours and machine quotas",
            icon = Icons.Filled.Tune,
            isDark = isDark
        )

        Spacer(modifier = Modifier.height(28.dp))

        RoboticsOutlinedButton(
            text = "Log Out of Admin Hub",
            onClick = onLogout,
            leadingIcon = Icons.Filled.Logout
        )

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun AdminOptionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isDark: Boolean
) {
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertY
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isDark) CyberCyan else ElectricBlue,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (isDark) TextPrimaryDark else TextPrimaryLight
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isDark) TextSecondaryDark else TextSecondaryLight
                )
            }
        }
    }
}
