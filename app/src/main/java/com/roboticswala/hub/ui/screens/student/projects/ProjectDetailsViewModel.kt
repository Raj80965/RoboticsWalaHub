package com.roboticswala.hub.ui.screens.student.projects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.roboticswala.hub.data.models.Project
import com.roboticswala.hub.data.models.ProjectUpdate
import com.roboticswala.hub.data.models.UserProfile
import com.roboticswala.hub.data.repository.FirestoreProjectRepository
import com.roboticswala.hub.data.repository.ProjectRepository
import com.roboticswala.hub.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProjectDetailsUiState(
    val project: Project? = null,
    val updates: List<ProjectUpdate> = emptyList(),
    val isLoading: Boolean = true,
    val isPostingUpdate: Boolean = false,
    val isUpdatingProject: Boolean = false,
    val isDeleting: Boolean = false,
    val showAddUpdateDialog: Boolean = false,
    val showEditProjectDialog: Boolean = false,
    val showDeleteConfirmDialog: Boolean = false,
    val isDeleted: Boolean = false,
    val snackbarMessage: String? = null,
    val errorMessage: String? = null
)

class ProjectDetailsViewModel(
    private val projectId: String,
    private val projectRepository: ProjectRepository = FirestoreProjectRepository(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProjectDetailsUiState())
    val uiState: StateFlow<ProjectDetailsUiState> = _uiState.asStateFlow()

    init {
        observeProject()
        observeUpdates()
    }

    private fun observeProject() {
        viewModelScope.launch {
            projectRepository.observeProjectDetails(projectId).collect { proj ->
                _uiState.update { it.copy(project = proj, isLoading = false) }
            }
        }
    }

    private fun observeUpdates() {
        viewModelScope.launch {
            projectRepository.observeProjectUpdates(projectId).collect { list ->
                _uiState.update { it.copy(updates = list) }
            }
        }
    }

    fun openAddUpdateDialog() = _uiState.update { it.copy(showAddUpdateDialog = true, errorMessage = null) }
    fun closeAddUpdateDialog() = _uiState.update { it.copy(showAddUpdateDialog = false, errorMessage = null) }

    fun openEditProjectDialog() = _uiState.update { it.copy(showEditProjectDialog = true, errorMessage = null) }
    fun closeEditProjectDialog() = _uiState.update { it.copy(showEditProjectDialog = false, errorMessage = null) }

    fun openDeleteConfirmDialog() = _uiState.update { it.copy(showDeleteConfirmDialog = true) }
    fun closeDeleteConfirmDialog() = _uiState.update { it.copy(showDeleteConfirmDialog = false) }

    fun updateProjectDetails(
        title: String,
        description: String,
        category: String,
        githubLink: String,
        requiredComponents: String
    ) {
        val currentProject = _uiState.value.project ?: return
        val updated = currentProject.copy(
            title = title.trim(),
            description = description.trim(),
            category = category.trim(),
            githubLink = githubLink.trim(),
            requiredComponents = requiredComponents.trim(),
            updatedAt = System.currentTimeMillis()
        )

        viewModelScope.launch {
            projectRepository.updateProject(updated).collect { resource ->
                when (resource) {
                    is Resource.Loading -> _uiState.update { it.copy(isUpdatingProject = true, errorMessage = null) }
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                isUpdatingProject = false,
                                showEditProjectDialog = false,
                                snackbarMessage = "Project details updated successfully!"
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isUpdatingProject = false,
                                errorMessage = resource.message ?: "Failed to update project."
                            )
                        }
                    }
                }
            }
        }
    }

    fun deleteProjectUpdate(updateId: String) {
        viewModelScope.launch {
            try {
                com.google.firebase.firestore.FirebaseFirestore.getInstance()
                    .collection("projects")
                    .document(projectId)
                    .collection("updates")
                    .document(updateId)
                    .delete()
                _uiState.update { it.copy(snackbarMessage = "Update removed successfully.") }
            } catch (e: Exception) {
                _uiState.update { it.copy(snackbarMessage = "Failed to delete update.") }
            }
        }
    }

    fun postProgressUpdate(
        title: String,
        workDescription: String,
        progressPercentage: Int,
        problemsFaced: String,
        nextSteps: String,
        imageBytes: ByteArray?,
        userProfile: UserProfile
    ) {
        if (title.isBlank() || workDescription.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Update title and work description are required.") }
            return
        }

        val uid = auth.currentUser?.uid ?: userProfile.uid
        val update = ProjectUpdate(
            projectId = projectId,
            title = title.trim(),
            workDescription = workDescription.trim(),
            progressPercentage = progressPercentage.coerceIn(0, 100),
            problemsFaced = problemsFaced.trim(),
            nextSteps = nextSteps.trim(),
            createdByUid = uid,
            createdByName = userProfile.fullName.ifBlank { "Team Member" }
        )

        viewModelScope.launch {
            projectRepository.addProjectUpdate(projectId, update, imageBytes).collect { resource ->
                when (resource) {
                    is Resource.Loading -> _uiState.update { it.copy(isPostingUpdate = true, errorMessage = null) }
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                isPostingUpdate = false,
                                showAddUpdateDialog = false,
                                snackbarMessage = "Progress update published! Project progress synced."
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isPostingUpdate = false,
                                errorMessage = resource.message ?: "Failed to publish update."
                            )
                        }
                    }
                }
            }
        }
    }

    fun deleteProject() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            projectRepository.deleteProject(projectId, uid).collect { resource ->
                when (resource) {
                    is Resource.Loading -> _uiState.update { it.copy(isDeleting = true) }
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                isDeleting = false,
                                showDeleteConfirmDialog = false,
                                isDeleted = true,
                                snackbarMessage = "Project deleted successfully."
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isDeleting = false,
                                showDeleteConfirmDialog = false,
                                snackbarMessage = resource.message ?: "Failed to delete project."
                            )
                        }
                    }
                }
            }
        }
    }

    fun clearMessage() = _uiState.update { it.copy(snackbarMessage = null, errorMessage = null) }
}

class ProjectDetailsViewModelFactory(
    private val projectId: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProjectDetailsViewModel::class.java)) {
            return ProjectDetailsViewModel(projectId = projectId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
