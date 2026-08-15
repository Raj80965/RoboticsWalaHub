package com.roboticswala.hub.ui.screens.student.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.roboticswala.hub.data.models.LabTask
import com.roboticswala.hub.data.repository.FirestoreWorkAndTaskRepository
import com.roboticswala.hub.data.repository.WorkAndTaskRepository
import com.roboticswala.hub.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StudentTasksUiState(
    val tasks: List<LabTask> = emptyList(),
    val filteredTasks: List<LabTask> = emptyList(),
    val statusFilter: String = "All",
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val selectedTaskForSubmission: LabTask? = null,
    val isSubmitting: Boolean = false,
    val snackbarMessage: String? = null,
    val errorMessage: String? = null
)

class StudentTasksViewModel(
    private val studentUid: String = FirebaseAuth.getInstance().currentUser?.uid ?: "",
    private val repository: WorkAndTaskRepository = FirestoreWorkAndTaskRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudentTasksUiState())
    val uiState: StateFlow<StudentTasksUiState> = _uiState.asStateFlow()

    init {
        if (studentUid.isNotBlank()) {
            observeTasks()
        }
    }

    private fun observeTasks() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.observeStudentTasks(studentUid).collect { list ->
                _uiState.update { state ->
                    state.copy(
                        tasks = list,
                        filteredTasks = filterTasks(list, state.statusFilter, state.searchQuery),
                        isLoading = false
                    )
                }
            }
        }
    }

    fun setStatusFilter(status: String) {
        _uiState.update { state ->
            state.copy(
                statusFilter = status,
                filteredTasks = filterTasks(state.tasks, status, state.searchQuery)
            )
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                filteredTasks = filterTasks(state.tasks, state.statusFilter, query)
            )
        }
    }

    private fun filterTasks(list: List<LabTask>, status: String, query: String): List<LabTask> {
        return list.filter { task ->
            val matchesStatus = status == "All" || task.status.equals(status, ignoreCase = true)
            val matchesQuery = query.isBlank() ||
                    task.title.contains(query, ignoreCase = true) ||
                    task.description.contains(query, ignoreCase = true) ||
                    task.relatedProjectName.contains(query, ignoreCase = true)

            matchesStatus && matchesQuery
        }
    }

    fun startTask(taskId: String) {
        viewModelScope.launch {
            repository.startTask(taskId, studentUid).collect { resource ->
                when (resource) {
                    is Resource.Success -> _uiState.update { it.copy(snackbarMessage = "Task marked In Progress! Work started.") }
                    is Resource.Error -> _uiState.update { it.copy(errorMessage = resource.message) }
                    else -> {}
                }
            }
        }
    }

    fun openSubmissionModal(task: LabTask) = _uiState.update { it.copy(selectedTaskForSubmission = task) }
    fun closeSubmissionModal() = _uiState.update { it.copy(selectedTaskForSubmission = null) }

    fun submitTaskWork(
        taskId: String,
        note: String,
        fileBytes: ByteArray?
    ) {
        viewModelScope.launch {
            repository.submitTaskWork(taskId, studentUid, note, fileBytes).collect { resource ->
                when (resource) {
                    is Resource.Loading -> _uiState.update { it.copy(isSubmitting = true) }
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                isSubmitting = false,
                                selectedTaskForSubmission = null,
                                snackbarMessage = "Task work submitted for Admin review!"
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isSubmitting = false,
                                errorMessage = resource.message ?: "Failed to submit task work."
                            )
                        }
                    }
                }
            }
        }
    }

    fun clearMessage() = _uiState.update { it.copy(snackbarMessage = null, errorMessage = null) }
}

class StudentTasksViewModelFactory(
    private val studentUid: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StudentTasksViewModel::class.java)) {
            return StudentTasksViewModel(studentUid = studentUid) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
