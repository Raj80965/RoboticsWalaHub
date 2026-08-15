package com.roboticswala.hub.ui.screens.admin.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.roboticswala.hub.data.models.LabTask
import com.roboticswala.hub.data.models.UserProfile
import com.roboticswala.hub.data.repository.FirestoreProjectRepository
import com.roboticswala.hub.data.repository.FirestoreWorkAndTaskRepository
import com.roboticswala.hub.data.repository.ProjectRepository
import com.roboticswala.hub.data.repository.WorkAndTaskRepository
import com.roboticswala.hub.utils.BookingTimeUtils
import com.roboticswala.hub.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminTasksUiState(
    val tasks: List<LabTask> = emptyList(),
    val filteredTasks: List<LabTask> = emptyList(),
    val statusFilter: String = "All",
    val priorityFilter: String = "All",
    val searchQuery: String = "",
    val availableStudents: List<UserProfile> = emptyList(),
    val showCreateTaskDialog: Boolean = false,
    val selectedTaskForReview: LabTask? = null,
    val taskToDelete: LabTask? = null,
    val isActionLoading: Boolean = false,
    val snackbarMessage: String? = null,
    val errorMessage: String? = null
)

class AdminTasksViewModel(
    private val repository: WorkAndTaskRepository = FirestoreWorkAndTaskRepository(),
    private val projectRepository: ProjectRepository = FirestoreProjectRepository(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminTasksUiState())
    val uiState: StateFlow<AdminTasksUiState> = _uiState.asStateFlow()

    init {
        observeAllTasks()
        fetchApprovedStudents()
    }

    private fun observeAllTasks() {
        viewModelScope.launch {
            repository.observeAllAdminTasks().collect { list ->
                _uiState.update { state ->
                    state.copy(
                        tasks = list,
                        filteredTasks = filterList(list, state.statusFilter, state.priorityFilter, state.searchQuery)
                    )
                }
            }
        }
    }

    private fun fetchApprovedStudents() {
        viewModelScope.launch {
            projectRepository.fetchApprovedStudents().collect { students ->
                _uiState.update { it.copy(availableStudents = students) }
            }
        }
    }

    fun setStatusFilter(status: String) {
        _uiState.update { state ->
            state.copy(
                statusFilter = status,
                filteredTasks = filterList(state.tasks, status, state.priorityFilter, state.searchQuery)
            )
        }
    }

    fun setPriorityFilter(priority: String) {
        _uiState.update { state ->
            state.copy(
                priorityFilter = priority,
                filteredTasks = filterList(state.tasks, state.statusFilter, priority, state.searchQuery)
            )
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                filteredTasks = filterList(state.tasks, state.statusFilter, state.priorityFilter, query)
            )
        }
    }

    private fun filterList(
        list: List<LabTask>,
        status: String,
        priority: String,
        query: String
    ): List<LabTask> {
        return list.filter { task ->
            val matchesStatus = status == "All" || task.status.equals(status, ignoreCase = true)
            val matchesPriority = priority == "All" || task.priority.equals(priority, ignoreCase = true)
            val matchesQuery = query.isBlank() ||
                    task.title.contains(query, ignoreCase = true) ||
                    task.assignedStudentName.contains(query, ignoreCase = true) ||
                    task.assignedStudentId.contains(query, ignoreCase = true) ||
                    task.relatedProjectName.contains(query, ignoreCase = true)

            matchesStatus && matchesPriority && matchesQuery
        }
    }

    fun openCreateTaskDialog() = _uiState.update { it.copy(showCreateTaskDialog = true, errorMessage = null) }
    fun closeCreateTaskDialog() = _uiState.update { it.copy(showCreateTaskDialog = false, errorMessage = null) }

    fun openReviewModal(task: LabTask) = _uiState.update { it.copy(selectedTaskForReview = task) }
    fun closeReviewModal() = _uiState.update { it.copy(selectedTaskForReview = null) }

    fun promptDeleteTask(task: LabTask) = _uiState.update { it.copy(taskToDelete = task) }
    fun dismissDeleteTask() = _uiState.update { it.copy(taskToDelete = null) }

    fun createAndAssignTask(
        title: String,
        description: String,
        student: UserProfile,
        projectName: String,
        deadline: String,
        priority: String
    ) {
        if (title.isBlank() || description.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Task title and description are required.") }
            return
        }

        val adminUid = auth.currentUser?.uid ?: "Admin"
        val task = LabTask(
            title = title.trim(),
            description = description.trim(),
            assignedStudentUid = student.uid,
            assignedStudentId = student.studentId.ifBlank { "STU-${student.uid.take(4)}" },
            assignedStudentName = student.fullName.ifBlank { "Student" },
            relatedProjectName = projectName.trim(),
            deadline = deadline.ifBlank { BookingTimeUtils.getTodayDateString() },
            priority = priority,
            createdByAdminUid = adminUid
        )

        viewModelScope.launch {
            repository.createTask(task).collect { resource ->
                when (resource) {
                    is Resource.Loading -> _uiState.update { it.copy(isActionLoading = true) }
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                isActionLoading = false,
                                showCreateTaskDialog = false,
                                snackbarMessage = "Task assigned to ${student.fullName}!"
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isActionLoading = false,
                                errorMessage = resource.message ?: "Failed to assign task."
                            )
                        }
                    }
                }
            }
        }
    }

    fun reviewTask(taskId: String, markCompleted: Boolean, feedbackNote: String?) {
        viewModelScope.launch {
            repository.adminReviewTask(taskId, markCompleted, feedbackNote).collect { resource ->
                when (resource) {
                    is Resource.Loading -> _uiState.update { it.copy(isActionLoading = true) }
                    is Resource.Success -> {
                        val msg = if (markCompleted) "Task marked Completed!" else "Task returned for changes."
                        _uiState.update {
                            it.copy(
                                isActionLoading = false,
                                selectedTaskForReview = null,
                                snackbarMessage = msg
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isActionLoading = false,
                                snackbarMessage = resource.message ?: "Failed to review task."
                            )
                        }
                    }
                }
            }
        }
    }

    fun confirmDeleteTask() {
        val task = _uiState.value.taskToDelete ?: return
        viewModelScope.launch {
            repository.deleteTask(task.taskId).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                taskToDelete = null,
                                snackbarMessage = "Task deleted."
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                taskToDelete = null,
                                snackbarMessage = resource.message ?: "Failed to delete task."
                            )
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    fun clearMessage() = _uiState.update { it.copy(snackbarMessage = null, errorMessage = null) }
}
