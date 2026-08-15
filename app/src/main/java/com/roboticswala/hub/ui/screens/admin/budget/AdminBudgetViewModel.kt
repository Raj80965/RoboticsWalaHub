package com.roboticswala.hub.ui.screens.admin.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.roboticswala.hub.data.models.BudgetAuditLog
import com.roboticswala.hub.data.models.Project
import com.roboticswala.hub.data.models.ProjectBudgetSummary
import com.roboticswala.hub.data.models.ProjectExpense
import com.roboticswala.hub.data.repository.BudgetRepository
import com.roboticswala.hub.data.repository.FirestoreBudgetRepository
import com.roboticswala.hub.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminBudgetUiState(
    val projects: List<Project> = emptyList(),
    val allExpenses: List<ProjectExpense> = emptyList(),
    val filteredExpenses: List<ProjectExpense> = emptyList(),
    val auditLogs: List<BudgetAuditLog> = emptyList(),
    val totalApprovedBudget: Double = 0.0,
    val totalApprovedExpenses: Double = 0.0,
    val totalPendingExpenses: Double = 0.0,
    val totalRemainingBudget: Double = 0.0,
    val pendingApprovalsCount: Int = 0,
    val highestSpendingProjectTitle: String = "None",
    val selectedProjectId: String = "All",
    val statusFilter: String = "All",
    val categoryFilter: String = "All",
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val selectedProjectForBudgetEdit: Project? = null,
    val selectedExpenseForAction: ProjectExpense? = null,
    val isRejecting: Boolean = false,
    val isOverBudgetWarning: Boolean = false,
    val overBudgetWarningDetails: String = "",
    val isActionLoading: Boolean = false,
    val snackbarMessage: String? = null,
    val errorMessage: String? = null
)

class AdminBudgetViewModel(
    private val budgetRepository: BudgetRepository = FirestoreBudgetRepository(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminBudgetUiState())
    val uiState: StateFlow<AdminBudgetUiState> = _uiState.asStateFlow()

    init {
        observeProjects()
        observeExpenses()
        observeAudit()
    }

    private fun observeProjects() {
        viewModelScope.launch {
            budgetRepository.observeAllProjectsWithBudget().collect { list ->
                _uiState.update { it.copy(projects = list) }
                calculateAnalytics()
            }
        }
    }

    private fun observeExpenses() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            budgetRepository.observeAllExpenses().collect { list ->
                _uiState.update { state ->
                    state.copy(
                        allExpenses = list,
                        filteredExpenses = filterList(list, state.selectedProjectId, state.statusFilter, state.categoryFilter, state.searchQuery),
                        pendingApprovalsCount = list.count { it.isPending },
                        isLoading = false
                    )
                }
                calculateAnalytics()
            }
        }
    }

    private fun observeAudit() {
        viewModelScope.launch {
            budgetRepository.observeBudgetAuditLogs().collect { logs ->
                _uiState.update { it.copy(auditLogs = logs) }
            }
        }
    }

    private fun calculateAnalytics() {
        val state = _uiState.value
        val projs = state.projects
        val expenses = state.allExpenses

        val totalApprBudget = projs.sumOf { if (it.approvedBudget > 0.0) it.approvedBudget else it.estimatedBudget }
        val totalApprExp = expenses.filter { it.isApproved }.sumOf { it.amount }
        val totalPendExp = expenses.filter { it.isPending }.sumOf { it.amount }
        val totalRemBudget = (totalApprBudget - totalApprExp).coerceAtLeast(0.0)

        // Find highest spending project
        val spendingByProj = expenses.filter { it.isApproved }.groupBy { it.projectId }
        var topProjTitle = "None"
        var maxSpend = 0.0
        spendingByProj.forEach { (pId, list) ->
            val sum = list.sumOf { it.amount }
            if (sum > maxSpend) {
                maxSpend = sum
                topProjTitle = list.firstOrNull()?.projectName ?: pId
            }
        }

        _uiState.update {
            it.copy(
                totalApprovedBudget = totalApprBudget,
                totalApprovedExpenses = totalApprExp,
                totalPendingExpenses = totalPendExp,
                totalRemainingBudget = totalRemBudget,
                highestSpendingProjectTitle = if (maxSpend > 0.0) "$topProjTitle (₹$maxSpend)" else "None"
            )
        }
    }

    fun setSelectedProject(projectId: String) {
        _uiState.update { state ->
            state.copy(
                selectedProjectId = projectId,
                filteredExpenses = filterList(state.allExpenses, projectId, state.statusFilter, state.categoryFilter, state.searchQuery)
            )
        }
    }

    fun setStatusFilter(status: String) {
        _uiState.update { state ->
            state.copy(
                statusFilter = status,
                filteredExpenses = filterList(state.allExpenses, state.selectedProjectId, status, state.categoryFilter, state.searchQuery)
            )
        }
    }

    fun setCategoryFilter(category: String) {
        _uiState.update { state ->
            state.copy(
                categoryFilter = category,
                filteredExpenses = filterList(state.allExpenses, state.selectedProjectId, state.statusFilter, category, state.searchQuery)
            )
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                filteredExpenses = filterList(state.allExpenses, state.selectedProjectId, state.statusFilter, state.categoryFilter, query)
            )
        }
    }

    private fun filterList(
        list: List<ProjectExpense>,
        projectId: String,
        status: String,
        category: String,
        query: String
    ): List<ProjectExpense> {
        return list.filter { item ->
            val matchesProj = projectId == "All" || item.projectId == projectId
            val matchesStat = status == "All" || item.status.equals(status, ignoreCase = true)
            val matchesCat = category == "All" || item.category.equals(category, ignoreCase = true)
            val matchesQuery = query.isBlank() ||
                    item.title.contains(query, ignoreCase = true) ||
                    item.submittedByName.contains(query, ignoreCase = true) ||
                    item.submittedByStudentId.contains(query, ignoreCase = true) ||
                    item.projectName.contains(query, ignoreCase = true) ||
                    item.vendorName.contains(query, ignoreCase = true)

            matchesProj && matchesStat && matchesCat && matchesQuery
        }
    }

    fun openBudgetEditDialog(project: Project) = _uiState.update { it.copy(selectedProjectForBudgetEdit = project, errorMessage = null) }
    fun closeBudgetEditDialog() = _uiState.update { it.copy(selectedProjectForBudgetEdit = null, errorMessage = null) }

    fun setProjectBudget(projectId: String, budget: Double, reason: String) {
        val adminUid = auth.currentUser?.uid ?: "Admin"
        val adminName = auth.currentUser?.displayName ?: "Lab Admin"

        viewModelScope.launch {
            budgetRepository.setProjectApprovedBudget(projectId, budget, adminUid, adminName, reason).collect { res ->
                when (res) {
                    is Resource.Loading -> _uiState.update { it.copy(isActionLoading = true, errorMessage = null) }
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                isActionLoading = false,
                                selectedProjectForBudgetEdit = null,
                                snackbarMessage = "Project approved budget updated to ₹$budget!"
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isActionLoading = false,
                                errorMessage = res.message ?: "Failed to update budget."
                            )
                        }
                    }
                }
            }
        }
    }

    fun promptApproveExpense(expense: ProjectExpense) {
        val adminUid = auth.currentUser?.uid ?: "Admin"
        val adminName = auth.currentUser?.displayName ?: "Lab Admin"

        viewModelScope.launch {
            budgetRepository.approveExpense(expense.expenseId, adminUid, adminName, allowOverBudget = false).collect { res ->
                when (res) {
                    is Resource.Loading -> _uiState.update { it.copy(isActionLoading = true, errorMessage = null) }
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                isActionLoading = false,
                                snackbarMessage = "Expense of ₹${expense.amount} approved successfully!"
                            )
                        }
                    }
                    is Resource.Error -> {
                        if (res.message?.contains("OVER_BUDGET") == true) {
                            _uiState.update {
                                it.copy(
                                    isActionLoading = false,
                                    selectedExpenseForAction = expense,
                                    isOverBudgetWarning = true,
                                    overBudgetWarningDetails = res.message ?: "Expense exceeds project budget limit."
                                )
                            }
                        } else {
                            _uiState.update {
                                it.copy(
                                    isActionLoading = false,
                                    errorMessage = res.message ?: "Failed to approve expense."
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    fun confirmOverBudgetApproval(reason: String) {
        val exp = _uiState.value.selectedExpenseForAction ?: return
        val adminUid = auth.currentUser?.uid ?: "Admin"
        val adminName = auth.currentUser?.displayName ?: "Lab Admin"

        viewModelScope.launch {
            budgetRepository.approveExpense(exp.expenseId, adminUid, adminName, allowOverBudget = true, overBudgetReason = reason).collect { res ->
                when (res) {
                    is Resource.Loading -> _uiState.update { it.copy(isActionLoading = true) }
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                isActionLoading = false,
                                isOverBudgetWarning = false,
                                selectedExpenseForAction = null,
                                snackbarMessage = "Over-budget expense approved as an exception."
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isActionLoading = false,
                                errorMessage = res.message
                            )
                        }
                    }
                }
            }
        }
    }

    fun openRejectDialog(expense: ProjectExpense) = _uiState.update { it.copy(selectedExpenseForAction = expense, isRejecting = true, errorMessage = null) }
    fun closeActionDialog() = _uiState.update { it.copy(selectedExpenseForAction = null, isRejecting = false, isOverBudgetWarning = false, errorMessage = null) }

    fun confirmReject(reason: String) {
        val exp = _uiState.value.selectedExpenseForAction ?: return
        val adminUid = auth.currentUser?.uid ?: "Admin"
        val adminName = auth.currentUser?.displayName ?: "Lab Admin"

        viewModelScope.launch {
            budgetRepository.rejectExpense(exp.expenseId, reason, adminUid, adminName).collect { res ->
                when (res) {
                    is Resource.Loading -> _uiState.update { it.copy(isActionLoading = true) }
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                isActionLoading = false,
                                isRejecting = false,
                                selectedExpenseForAction = null,
                                snackbarMessage = "Expense rejected."
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isActionLoading = false,
                                errorMessage = res.message
                            )
                        }
                    }
                }
            }
        }
    }

    fun clearMessage() = _uiState.update { it.copy(snackbarMessage = null, errorMessage = null) }
}
