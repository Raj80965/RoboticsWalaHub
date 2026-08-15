package com.roboticswala.hub.data.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class ProjectExpense(
    @DocumentId
    val expenseId: String = "",
    val projectId: String = "",
    val projectName: String = "",
    val submittedByUid: String = "",
    val submittedByStudentId: String = "",
    val submittedByName: String = "",
    val title: String = "",
    val category: String = CATEGORY_COMPONENTS,
    val description: String = "",
    val amount: Double = 0.0,
    val purchaseDate: String = "", // Format: "yyyy-MM-dd"
    val vendorName: String = "",
    val receiptUrl: String = "",
    val receiptFileName: String = "",
    val notes: String = "",
    val status: String = STATUS_PENDING, // Pending, Approved, Rejected, Cancelled
    val rejectionReason: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val approvedAt: Long? = null,
    val approvedByAdminUid: String = "",
    val approvedByAdminName: String = "",
    val rejectedAt: Long? = null,
    val rejectedByAdminUid: String = ""
) {
    companion object {
        // Categories
        const val CATEGORY_COMPONENTS = "Components"
        const val CATEGORY_SENSORS = "Sensors"
        const val CATEGORY_MOTORS = "Motors"
        const val CATEGORY_MICROCONTROLLERS = "Microcontrollers"
        const val CATEGORY_BATTERIES = "Batteries"
        const val CATEGORY_TOOLS = "Tools"
        const val CATEGORY_MECHANICAL = "Mechanical Parts"
        const val CATEGORY_ELECTRONICS = "Electronics"
        const val CATEGORY_SOFTWARE = "Software"
        const val CATEGORY_3D_PRINTING = "3D Printing"
        const val CATEGORY_TRAVEL = "Travel"
        const val CATEGORY_OTHER = "Other"

        val ALL_CATEGORIES = listOf(
            CATEGORY_COMPONENTS,
            CATEGORY_SENSORS,
            CATEGORY_MOTORS,
            CATEGORY_MICROCONTROLLERS,
            CATEGORY_BATTERIES,
            CATEGORY_TOOLS,
            CATEGORY_MECHANICAL,
            CATEGORY_ELECTRONICS,
            CATEGORY_SOFTWARE,
            CATEGORY_3D_PRINTING,
            CATEGORY_TRAVEL,
            CATEGORY_OTHER
        )

        // Statuses
        const val STATUS_PENDING = "Pending"
        const val STATUS_APPROVED = "Approved"
        const val STATUS_REJECTED = "Rejected"
        const val STATUS_CANCELLED = "Cancelled"

        val ALL_STATUSES = listOf(
            STATUS_PENDING,
            STATUS_APPROVED,
            STATUS_REJECTED,
            STATUS_CANCELLED
        )
    }

    val isPending: Boolean get() = status.equals(STATUS_PENDING, ignoreCase = true)
    val isApproved: Boolean get() = status.equals(STATUS_APPROVED, ignoreCase = true)
    val isRejected: Boolean get() = status.equals(STATUS_REJECTED, ignoreCase = true)
    val isCancelled: Boolean get() = status.equals(STATUS_CANCELLED, ignoreCase = true)
}

@IgnoreExtraProperties
data class BudgetAuditLog(
    @DocumentId
    val auditId: String = "",
    val projectId: String = "",
    val projectName: String = "",
    val expenseId: String = "",
    val action: String = "", // "Budget Created", "Budget Updated", "Expense Submitted", "Expense Edited", "Expense Deleted", "Expense Approved", "Expense Rejected", "Expense Cancelled"
    val previousValue: Double = 0.0,
    val newValue: Double = 0.0,
    val performedByUid: String = "",
    val performedByName: String = "Admin",
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String = ""
)

data class ProjectBudgetSummary(
    val projectId: String = "",
    val projectTitle: String = "",
    val estimatedBudget: Double = 0.0,
    val approvedBudget: Double = 0.0,
    val totalApprovedExpenses: Double = 0.0,
    val totalPendingExpenses: Double = 0.0,
    val totalRejectedExpenses: Double = 0.0,
    val remainingBudget: Double = 0.0,
    val utilizationPercentage: Double = 0.0,
    val healthStatus: String = "Normal" // "Normal", "Warning", "Near Limit", "Over Budget"
) {
    companion object {
        fun calculate(
            projectId: String,
            title: String,
            estimatedBudget: Double,
            approvedBudget: Double,
            expenses: List<ProjectExpense>
        ): ProjectBudgetSummary {
            val approvedTotal = expenses.filter { it.isApproved }.sumOf { it.amount }
            val pendingTotal = expenses.filter { it.isPending }.sumOf { it.amount }
            val rejectedTotal = expenses.filter { it.isRejected }.sumOf { it.amount }

            val effectiveBudget = if (approvedBudget > 0.0) approvedBudget else estimatedBudget
            val remaining = (effectiveBudget - approvedTotal).coerceAtLeast(0.0)
            val utilization = if (effectiveBudget > 0.0) {
                ((approvedTotal / effectiveBudget) * 100.0).coerceAtLeast(0.0)
            } else {
                0.0
            }

            val health = when {
                effectiveBudget <= 0.0 && approvedTotal > 0.0 -> "Over Budget"
                utilization >= 100.0 -> "Over Budget"
                utilization >= 90.0 -> "Near Limit"
                utilization >= 75.0 -> "Warning"
                else -> "Normal"
            }

            return ProjectBudgetSummary(
                projectId = projectId,
                projectTitle = title,
                estimatedBudget = estimatedBudget,
                approvedBudget = approvedBudget,
                totalApprovedExpenses = approvedTotal,
                totalPendingExpenses = pendingTotal,
                totalRejectedExpenses = rejectedTotal,
                remainingBudget = remaining,
                utilizationPercentage = utilization,
                healthStatus = health
            )
        }
    }
}
