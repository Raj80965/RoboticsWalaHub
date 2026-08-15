package com.roboticswala.hub.ui.screens.student.projects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.roboticswala.hub.data.models.Project
import com.roboticswala.hub.data.repository.FirestoreProjectRepository
import com.roboticswala.hub.data.repository.ProjectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StudentProjectsUiState(
    val projects: List<Project> = emptyList(),
    val filteredProjects: List<Project> = emptyList(),
    val searchQuery: String = "",
    val categoryFilter: String = "All",
    val statusFilter: String = "All",
    val sortBy: String = SORT_UPDATED_DESC, // "Last Updated", "Progress: High to Low", "Progress: Low to High"
    val isLoading: Boolean = true,
    val errorMessage: String? = null
) {
    companion object {
        const val SORT_UPDATED_DESC = "Last Updated"
        const val SORT_PROGRESS_DESC = "Progress: High to Low"
        const val SORT_PROGRESS_ASC = "Progress: Low to High"
    }
}

class StudentProjectsViewModel(
    private val studentUid: String = FirebaseAuth.getInstance().currentUser?.uid ?: "",
    private val projectRepository: ProjectRepository = FirestoreProjectRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudentProjectsUiState())
    val uiState: StateFlow<StudentProjectsUiState> = _uiState.asStateFlow()

    init {
        if (studentUid.isNotBlank()) {
            observeProjects()
        }
    }

    private fun observeProjects() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            projectRepository.observeStudentProjects(studentUid).collect { list ->
                _uiState.update { state ->
                    state.copy(
                        projects = list,
                        filteredProjects = applyFilterAndSort(
                            list,
                            state.searchQuery,
                            state.categoryFilter,
                            state.statusFilter,
                            state.sortBy
                        ),
                        isLoading = false
                    )
                }
            }
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                filteredProjects = applyFilterAndSort(
                    state.projects,
                    query,
                    state.categoryFilter,
                    state.statusFilter,
                    state.sortBy
                )
            )
        }
    }

    fun setCategoryFilter(category: String) {
        _uiState.update { state ->
            state.copy(
                categoryFilter = category,
                filteredProjects = applyFilterAndSort(
                    state.projects,
                    state.searchQuery,
                    category,
                    state.statusFilter,
                    state.sortBy
                )
            )
        }
    }

    fun setStatusFilter(status: String) {
        _uiState.update { state ->
            state.copy(
                statusFilter = status,
                filteredProjects = applyFilterAndSort(
                    state.projects,
                    state.searchQuery,
                    state.categoryFilter,
                    status,
                    state.sortBy
                )
            )
        }
    }

    fun setSortBy(sort: String) {
        _uiState.update { state ->
            state.copy(
                sortBy = sort,
                filteredProjects = applyFilterAndSort(
                    state.projects,
                    state.searchQuery,
                    state.categoryFilter,
                    state.statusFilter,
                    sort
                )
            )
        }
    }

    private fun applyFilterAndSort(
        list: List<Project>,
        query: String,
        category: String,
        status: String,
        sort: String
    ): List<Project> {
        val filtered = list.filter { project ->
            val matchesQuery = query.isBlank() ||
                    project.title.contains(query, ignoreCase = true) ||
                    project.description.contains(query, ignoreCase = true) ||
                    project.requiredComponents.contains(query, ignoreCase = true)

            val matchesCategory = category == "All" || project.category.equals(category, ignoreCase = true)
            val matchesStatus = status == "All" || project.status.equals(status, ignoreCase = true)

            matchesQuery && matchesCategory && matchesStatus
        }

        return when (sort) {
            StudentProjectsUiState.SORT_PROGRESS_DESC -> filtered.sortedByDescending { it.progressPercentage }
            StudentProjectsUiState.SORT_PROGRESS_ASC -> filtered.sortedBy { it.progressPercentage }
            else -> filtered.sortedByDescending { it.updatedAt }
        }
    }
}

class StudentProjectsViewModelFactory(
    private val studentUid: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StudentProjectsViewModel::class.java)) {
            return StudentProjectsViewModel(studentUid = studentUid) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
