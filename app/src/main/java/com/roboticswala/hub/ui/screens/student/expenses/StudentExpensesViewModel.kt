package com.roboticswala.hub.ui.screens.student.expenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.roboticswala.hub.data.models.Project
import com.roboticswala.hub.data.models.ProjectBudgetSummary
import com.roboticswala.hub.data.models.ProjectExpense
import com.roboticswala.hub.data.models.UserProfile
import com.roboticswala.hub.data.repository.BudgetRepository
import com.roboticswala.hub.data.repository.FirestoreBudgetRepository
import com.roboticswala.hub.data.repository.FirestoreProjectRepository
import com.roboticswala.hub.data.repository.ProjectRepository
import com.roboticswala.hub.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StudentExpensesUiState(
    val myProjects: List<Project> = emptyList(),
    val expenses: List<ProjectExpense> = emptyList(),
    val filteredExpenses: List<ProjectExpense> = emptyList(),
    val selectedProjectId: String = "All",
    val categoryFilter: String = "All",
    val statusFilter: String = "All",
    val searchQuery: String = "",
    val budgetSummary: ProjectBudgetSummary? = null,
    val isLoading: Boolean = true,
    val showAddDialog: Boolean = false,
    val expenseToEdit: ProjectExpense? = null,
    val selectedExpenseDetails: ProjectExpense? = null,
    val expenseToDelete: ProjectExpense? = null,
    val isActionLoading: Boolean = false,
    val uploadProgress: Float = 0f,
    val snackbarMessage: String? = null,
    val errorMessage: String? = null
)

class StudentExpensesViewModel(
    private val studentUid: String = FirebaseAuth.getInstance().currentUser?.uid ?: "",
    private val budgetRepository: BudgetRepository = FirestoreBudgetRepository(),
    private val projectRepository: ProjectRepository = FirestoreProjectRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudentExpensesUiState())
    val uiState: StateFlow<StudentExpensesUiState> = _uiState.asStateFlow()

    init {
        observeProjects()
        observeExpenses()
    }

    private fun observeProjects() {
        viewModelScope.launch {
            projectRepository.observeStudentProjects(studentUid).collect { projs ->
                _uiState.update { it.copy(myProjects = projs) }
                calculateSummary()
            }
        }
    }

    private fun observeExpenses() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            budgetRepository.observeStudentExpenses(studentUid).collect { list ->
                _uiState.update { state ->
                    state.copy(
                        expenses = list,
                        filteredExpenses = filterList(list, state.selectedProjectId, state.categoryFilter, state.statusFilter, state.searchQuery),
                        isLoading = false
                    )
                }
                calculateSummary()
            }
        }
    }

    private fun calculateSummary() {
        val state = _uiState.value
        val projs = state.myProjects
        if (projs.isEmpty()) {
            _uiState.update { it.copy(budgetSummary = null) }
            return
        }

        val targetProj = if (state.selectedProjectId != "All") {
            projs.find { it.projectId == state.selectedProjectId }
        } else {
            projs.firstOrNull()
        }

        if (targetProj != null) {
            val projExpenses = state.expenses.filter { it.projectId == targetProj.projectId }
            val summary = ProjectBudgetSummary.calculate(
                projectId = targetProj.projectId,
                title = targetProj.title,
                estimatedBudget = targetProj.estimatedBudget,
                approvedBudget = targetProj.approvedBudget,
                expenses = projExpenses
            )
            _uiState.update { it.copy(budgetSummary = summary) }
        }
    }

    fun setSelectedProject(projectId: String) {
        _uiState.update { state ->
            state.copy(
                selectedProjectId = projectId,
                filteredExpenses = filterList(state.expenses, projectId, state.categoryFilter, state.statusFilter, state.searchQuery)
            )
        }
        calculateSummary()
    }

    fun setCategoryFilter(category: String) {
        _uiState.update { state ->
            state.copy(
                categoryFilter = category,
                filteredExpenses = filterList(state.expenses, state.selectedProjectId, category, state.statusFilter, state.searchQuery)
            )
        }
    }

    fun setStatusFilter(status: String) {
        _uiState.update { state ->
            state.copy(
                statusFilter = status,
                filteredExpenses = filterList(state.expenses, state.selectedProjectId, state.categoryFilter, status, state.searchQuery)
            )
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                filteredExpenses = filterList(state.expenses, state.selectedProjectId, state.categoryFilter, state.statusFilter, query)
            )
        }
    }

    private fun filterList(
        list: List<ProjectExpense>,
        projectId: String,
        category: String,
        status: String,
        query: String
    ): List<ProjectExpense> {
        return list.filter { item ->
            val matchesProj = projectId == "All" || item.projectId == projectId
            val matchesCat = category == "All" || item.category.equals(category, ignoreCase = true)
            val matchesStat = status == "All" || item.status.equals(status, ignoreCase = true)
            val matchesQuery = query.isBlank() ||
                    item.title.contains(query, ignoreCase = true) ||
                    item.description.contains(query, ignoreCase = true) ||
                    item.vendorName.contains(query, ignoreCase = true) ||
                    item.projectName.contains(query, ignoreCase = true)

            matchesProj && matchesCat && matchesStat && matchesQuery
        }
    }

    fun openAddDialog() = _uiState.update { it.copy(showAddDialog = true, expenseToEdit = null, errorMessage = null) }
    fun openEditDialog(expense: ProjectExpense) = _uiState.update { it.copy(showAddDialog = true, expenseToEdit = expense, errorMessage = null) }
    fun closeAddDialog() = _uiState.update { it.copy(showAddDialog = false, expenseToEdit = null, errorMessage = null) }

    fun openDetails(expense: ProjectExpense) = _uiState.update { it.copy(selectedExpenseDetails = expense) }
    fun closeDetails() = _uiState.update { it.copy(selectedExpenseDetails = null) }

    fun promptDelete(expense: ProjectExpense) = _uiState.update { it.copy(expenseToDelete = expense) }
    fun dismissDelete() = _uiState.update { it.copy(expenseToDelete = null) }

    fun submitExpense(
        userProfile: UserProfile,
        projectId: String,
        title: String,
        category: String,
        description: String,
        amount: Double,
        purchaseDate: String,
        vendor: String,
        notes: String,
        receiptBytes: ByteArray?,
        receiptFileName: String?
    ) {
        val proj = _uiState.value.myProjects.find { it.projectId == projectId }
        val existing = _uiState.value.expenseToEdit

        val expense = (existing ?: ProjectExpense()).copy(
            projectId = projectId,
            projectName = proj?.title ?: (existing?.projectName ?: "Robotics Project"),
            submittedByUid = studentUid,
            submittedByStudentId = userProfile.studentId.ifBlank { "STU-${studentUid.take(4)}" },
            submittedByName = userProfile.fullName.ifBlank { "Student" },
            title = title.trim(),
            category = category,
            description = description.trim(),
            amount = amount,
            purchaseDate = purchaseDate,
            vendorName = vendor.trim(),
            notes = notes.trim()
        )

        viewModelScope.launch {
            if (existing != null) {
                budgetRepository.updatePendingExpense(expense, receiptBytes, receiptFileName).collect { res ->
                    handleResource(res, "Expense \"${expense.title}\" updated successfully!")
                }
            } else {
                budgetRepository.submitExpense(expense, receiptBytes, receiptFileName).collect { res ->
                    handleResource(res, "Expense of ₹${expense.amount} submitted for approval!")
                }
            }
        }
    }

    fun confirmDelete() {
        val item = _uiState.value.expenseToDelete ?: return
        viewModelScope.launch {
            budgetRepository.deletePendingExpense(item.expenseId, item.projectId).collect { res ->
                when (res) {
                    is Resource.Success -> _uiState.update { it.copy(expenseToDelete = null, snackbarMessage = "Expense deleted.") }
                    is Resource.Error -> _uiState.update { it.copy(expenseToDelete = null, errorMessage = res.message) }
                    else -> {}
                }
            }
        }
    }

    private fun handleResource(res: Resource<*>, successMsg: String) {
        when (res) {
            is Resource.Loading -> _uiState.update { it.copy(isActionLoading = true, errorMessage = null) }
            is Resource.Success -> {
                _uiState.update {
                    it.copy(
                        isActionLoading = false,
                        showAddDialog = false,
                        expenseToEdit = null,
                        snackbarMessage = successMsg
                    )
                }
            }
            is Resource.Error -> {
                _uiState.update {
                    it.copy(
                        isActionLoading = false,
                        errorMessage = res.message ?: "Operation failed."
                    )
                }
            }
        }
    }

    fun clearMessage() = _uiState.update { it.copy(snackbarMessage = null, errorMessage = null) }
}

class StudentExpensesViewModelFactory(
    private val studentUid: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StudentExpensesViewModel::class.java)) {
            return StudentExpensesViewModel(studentUid = studentUid) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
