package com.roboticswala.hub.ui.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roboticswala.hub.data.models.Project
import com.roboticswala.hub.data.repository.FirestoreProjectRepository
import com.roboticswala.hub.data.repository.ProjectRepository
import com.roboticswala.hub.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminProjectsUiState(
    val projects: List<Project> = emptyList(),
    val filteredProjects: List<Project> = emptyList(),
    val searchQuery: String = "",
    val categoryFilter: String = "All",
    val statusFilter: String = "All",
    val selectedProjectForDetails: Project? = null,
    val projectToEditMentor: Project? = null,
    val isUpdating: Boolean = false,
    val snackbarMessage: String? = null
)

class AdminProjectsViewModel(
    private val projectRepository: ProjectRepository = FirestoreProjectRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminProjectsUiState())
    val uiState: StateFlow<AdminProjectsUiState> = _uiState.asStateFlow()

    init {
        observeAllProjects()
    }

    private fun observeAllProjects() {
        viewModelScope.launch {
            projectRepository.observeAllAdminProjects().collect { list ->
                _uiState.update { state ->
                    state.copy(
                        projects = list,
                        filteredProjects = filterList(list, state.searchQuery, state.categoryFilter, state.statusFilter)
                    )
                }
            }
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                filteredProjects = filterList(state.projects, query, state.categoryFilter, state.statusFilter)
            )
        }
    }

    fun setCategoryFilter(category: String) {
        _uiState.update { state ->
            state.copy(
                categoryFilter = category,
                filteredProjects = filterList(state.projects, state.searchQuery, category, state.statusFilter)
            )
        }
    }

    fun setStatusFilter(status: String) {
        _uiState.update { state ->
            state.copy(
                statusFilter = status,
                filteredProjects = filterList(state.projects, state.searchQuery, state.categoryFilter, status)
            )
        }
    }

    private fun filterList(
        list: List<Project>,
        query: String,
        category: String,
        status: String
    ): List<Project> {
        return list.filter { project ->
            val matchesQuery = query.isBlank() ||
                    project.title.contains(query, ignoreCase = true) ||
                    project.ownerName.contains(query, ignoreCase = true) ||
                    project.ownerStudentId.contains(query, ignoreCase = true) ||
                    project.mentorName.contains(query, ignoreCase = true)

            val matchesCategory = category == "All" || project.category.equals(category, ignoreCase = true)
            val matchesStatus = status == "All" || project.status.equals(status, ignoreCase = true)

            matchesQuery && matchesCategory && matchesStatus
        }
    }

    fun promptEditMentor(project: Project) {
        _uiState.update { it.copy(projectToEditMentor = project) }
    }

    fun dismissEditMentor() {
        _uiState.update { it.copy(projectToEditMentor = null) }
    }

    fun updateMentorAndStatus(projectId: String, mentorName: String, status: String) {
        viewModelScope.launch {
            projectRepository.adminUpdateProject(projectId, status, mentorName).collect { resource ->
                when (resource) {
                    is Resource.Loading -> _uiState.update { it.copy(isUpdating = true) }
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                isUpdating = false,
                                projectToEditMentor = null,
                                snackbarMessage = "Project details updated successfully."
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isUpdating = false,
                                snackbarMessage = resource.message ?: "Failed to update project."
                            )
                        }
                    }
                }
            }
        }
    }

    fun clearMessage() = _uiState.update { it.copy(snackbarMessage = null) }
}
