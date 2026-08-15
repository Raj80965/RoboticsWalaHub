package com.roboticswala.hub.data.repository

import com.roboticswala.hub.data.models.BudgetAuditLog
import com.roboticswala.hub.data.models.Project
import com.roboticswala.hub.data.models.ProjectExpense
import com.roboticswala.hub.utils.Resource
import kotlinx.coroutines.flow.Flow

interface BudgetRepository {

    // ── Student Expense Submission & Management ──────────────────────────────
    fun submitExpense(
        expense: ProjectExpense,
        receiptBytes: ByteArray? = null,
        receiptFileName: String? = null
    ): Flow<Resource<ProjectExpense>>

    fun updatePendingExpense(
        expense: ProjectExpense,
        receiptBytes: ByteArray? = null,
        receiptFileName: String? = null
    ): Flow<Resource<Unit>>

    fun deletePendingExpense(
        expenseId: String,
        projectId: String
    ): Flow<Resource<Unit>>

    fun observeStudentExpenses(studentUid: String): Flow<List<ProjectExpense>>

    fun observeProjectExpenses(projectId: String): Flow<List<ProjectExpense>>

    // ── Admin Budget Controls & Expense Approvals ────────────────────────────
    fun setProjectApprovedBudget(
        projectId: String,
        newBudget: Double,
        adminUid: String,
        adminName: String,
        reason: String
    ): Flow<Resource<Unit>>

    fun approveExpense(
        expenseId: String,
        adminUid: String,
        adminName: String,
        allowOverBudget: Boolean = false,
        overBudgetReason: String = ""
    ): Flow<Resource<Unit>>

    fun rejectExpense(
        expenseId: String,
        reason: String,
        adminUid: String,
        adminName: String
    ): Flow<Resource<Unit>>

    fun observeAllExpenses(): Flow<List<ProjectExpense>>

    fun observeBudgetAuditLogs(): Flow<List<BudgetAuditLog>>

    fun observeAllProjectsWithBudget(): Flow<List<Project>>
}
