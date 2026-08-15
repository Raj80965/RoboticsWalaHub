package com.roboticswala.hub.ui.screens.admin.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.roboticswala.hub.data.models.AdminHubAnalytics
import com.roboticswala.hub.ui.screens.admin.analytics.AdminAnalyticsViewModel
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
fun AdminAnalyticsScreen(
    modifier: Modifier = Modifier,
    viewModel: AdminAnalyticsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isDark = isSystemInDarkTheme()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(if (isDark) DarkSurface else LightSurface)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Reports & Hub Analytics",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = if (isDark) TextPrimaryDark else TextPrimaryLight
                )
                Text(
                    text = "Real-time statistics across all 14 lab modules",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isDark) TextSecondaryDark else TextSecondaryLight
                )
            }
            IconButton(onClick = { viewModel.loadAnalytics() }) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = "Refresh",
                    tint = if (isDark) CyberCyan else ElectricBlue
                )
            }
        }

        // Tab Navigation
        val tabTitles = listOf("Overview", "Projects & Tasks", "Inventory & Budget", "PDF Reports")
        TabRow(
            selectedTabIndex = uiState.selectedTab,
            containerColor = if (isDark) DarkSurfaceElevated else LightSurfaceElevated,
            contentColor = if (isDark) CyberCyan else ElectricBlue,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[uiState.selectedTab]),
                    color = if (isDark) CyberCyan else ElectricBlue
                )
            }
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = uiState.selectedTab == index,
                    onClick = { viewModel.selectTab(index) },
                    text = { Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
            }
        }

        // Content
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = if (isDark) CyberCyan else ElectricBlue)
            }
        } else if (uiState.analytics == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Failed to load analytics data", color = CircuitError)
            }
        } else {
            val a = uiState.analytics!!
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                when (uiState.selectedTab) {
                    0 -> item { OverviewTab(a = a, isDark = isDark) }
                    1 -> item { ProjectsAndTasksTab(a = a, isDark = isDark) }
                    2 -> item { InventoryAndBudgetTab(a = a, isDark = isDark) }
                    3 -> item {
                        ReportsAndExportTab(
                            onExport = { viewModel.exportReport(it) },
                            isExporting = uiState.isExporting,
                            successMsg = uiState.exportSuccessMessage,
                            isDark = isDark
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OverviewTab(a: AdminHubAnalytics, isDark: Boolean) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Students KPI
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = if (isDark) DarkSurfaceElevated else LightSurfaceElevated)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Student Enrollment & Attendance", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    MetricBox("Total Students", "${a.totalStudents}", isDark)
                    MetricBox("Approved", "${a.approvedStudents}", isDark, CircuitSuccess)
                    MetricBox("Today Present", "${a.todayCheckIns}", isDark, CyberCyan)
                    MetricBox("Lab Hours", "${String.format("%.1f", a.totalLabHours)}h", isDark)
                }
            }
        }

        // Achievements & Events KPI
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = if (isDark) DarkSurfaceElevated else LightSurfaceElevated)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Achievements & Events Participation", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    MetricBox("Total Verified", "${a.approvedAchievements}", isDark, CircuitSuccess)
                    MetricBox("Pending Ach.", "${a.pendingAchievements}", isDark, CircuitWarning)
                    MetricBox("Upcoming Evts", "${a.upcomingEvents}", isDark, CyberCyan)
                    MetricBox("Registrations", "${a.totalEventRegistrations}", isDark)
                }
            }
        }
    }
}

@Composable
private fun ProjectsAndTasksTab(a: AdminHubAnalytics, isDark: Boolean) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = if (isDark) DarkSurfaceElevated else LightSurfaceElevated)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Project Status Distribution", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    MetricBox("Active", "${a.activeProjects}", isDark, CyberCyan)
                    MetricBox("Completed", "${a.completedProjects}", isDark, CircuitSuccess)
                    MetricBox("Planning", "${a.planningProjects}", isDark, CircuitWarning)
                    MetricBox("Avg Progress", "${String.format("%.1f", a.averageProjectProgress)}%", isDark)
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = if (isDark) DarkSurfaceElevated else LightSurfaceElevated)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Task Completion Rate", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    MetricBox("Completed", "${a.completedTasks}", isDark, CircuitSuccess)
                    MetricBox("In Progress", "${a.inProgressTasks}", isDark, CyberCyan)
                    MetricBox("Overdue", "${a.overdueTasks}", isDark, CircuitError)
                    MetricBox("Rate", "${String.format("%.1f", a.taskCompletionRate)}%", isDark)
                }
            }
        }
    }
}

@Composable
private fun InventoryAndBudgetTab(a: AdminHubAnalytics, isDark: Boolean) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = if (isDark) DarkSurfaceElevated else LightSurfaceElevated)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Hardware Inventory Health", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    MetricBox("Total Types", "${a.totalEquipmentItems}", isDark)
                    MetricBox("Issued Stock", "${a.issuedEquipmentCount}", isDark, CyberCyan)
                    MetricBox("Low Stock", "${a.lowStockItemsCount}", isDark, CircuitWarning)
                    MetricBox("Out of Stock", "${a.outOfStockItemsCount}", isDark, CircuitError)
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = if (isDark) DarkSurfaceElevated else LightSurfaceElevated)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Financial Budget Utilization", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    MetricBox("Allocated", "₹${a.totalApprovedBudget.toInt()}", isDark, CyberCyan)
                    MetricBox("Approved Exp", "₹${a.totalApprovedExpenses.toInt()}", isDark, CircuitSuccess)
                    MetricBox("Remaining", "₹${a.totalRemainingBudget.toInt()}", isDark)
                    MetricBox("Utilization", "${String.format("%.1f", a.budgetUtilizationPercentage)}%", isDark)
                }
            }
        }
    }
}

@Composable
private fun ReportsAndExportTab(
    onExport: (String) -> Unit,
    isExporting: Boolean,
    successMsg: String?,
    isDark: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        if (successMsg != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CircuitSuccess.copy(alpha = 0.15f))
            ) {
                Text(text = "✅ $successMsg", color = CircuitSuccess, fontSize = 12.sp, modifier = Modifier.padding(12.dp))
            }
        }

        val reports = listOf(
            "Overall Hub Performance Audit Report" to "Complete summary of student work, attendance, and project deliverables",
            "Attendance & Lab Station Usage Report" to "Daily, weekly and monthly QR check-ins and lab station hours",
            "Project Progress & Task Delivery Report" to "Milestone completions, active sprint tasks, and team contributions",
            "Hardware Inventory & Equipment Return Audit" to "Component stock counts, issued hardware, and overdue tracking",
            "Budget Allocation & Expense Claim Report" to "Grant approvals, vendor receipt audit logs, and remaining funds"
        )

        reports.forEach { (title, desc) ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = if (isDark) DarkSurfaceElevated else LightSurfaceElevated)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(desc, fontSize = 11.sp, color = if (isDark) TextSecondaryDark else TextSecondaryLight)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { onExport(title) },
                        enabled = !isExporting,
                        colors = ButtonDefaults.buttonColors(containerColor = if (isDark) CyberCyan else ElectricBlue, contentColor = if (isDark) Color.Black else Color.White),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Icon(Icons.Filled.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Export PDF", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricBox(
    label: String,
    value: String,
    isDark: Boolean,
    valueColor: Color? = null
) {
    Column {
        Text(text = label, fontSize = 10.sp, color = if (isDark) TextSecondaryDark else TextSecondaryLight)
        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = valueColor ?: (if (isDark) TextPrimaryDark else TextPrimaryLight)
        )
    }
}
