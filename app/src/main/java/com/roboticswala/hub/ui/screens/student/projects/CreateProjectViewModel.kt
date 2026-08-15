package com.roboticswala.hub.ui.screens.student.projects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.roboticswala.hub.data.models.Project
import com.roboticswala.hub.data.models.ProjectTeamMember
import com.roboticswala.hub.data.models.UserProfile
import com.roboticswala.hub.data.repository.FirestoreProjectRepository
import com.roboticswala.hub.data.repository.ProjectRepository
import com.roboticswala.hub.utils.BookingTimeUtils
import com.roboticswala.hub.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CreateProjectUiState(
    val title: String = "",
    val description: String = "",
    val category: String = Project.CATEGORY_ROBOTICS,
    val type: String = Project.TYPE_HARDWARE,
    val mentorName: String = "",
    val startDate: String = BookingTimeUtils.getTodayDateString(),
    val expectedCompletionDate: String = "",
    val status: String = Project.STATUS_IN_PROGRESS,
    val progressPercentage: Int = 10,
    val requiredComponents: String = "",
    val estimatedBudget: String = "0",
    val actualExpense: String = "0",
    val githubLink: String = "",
    val imageBytes: ByteArray? = null,
    val teamMembers: List<ProjectTeamMember> = emptyList(),
    val availableStudents: List<UserProfile> = emptyList(),
    val isSubmitting: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

class CreateProjectViewModel(
    private val projectRepository: ProjectRepository = FirestoreProjectRepository(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateProjectUiState())
    val uiState: StateFlow<CreateProjectUiState> = _uiState.asStateFlow()

    init {
        fetchApprovedStudents()
    }

    private fun fetchApprovedStudents() {
        viewModelScope.launch {
            projectRepository.fetchApprovedStudents().collect { list ->
                _uiState.update { it.copy(availableStudents = list) }
            }
        }
    }

    fun onTitleChange(value: String) = _uiState.update { it.copy(title = value) }
    fun onDescriptionChange(value: String) = _uiState.update { it.copy(description = value) }
    fun onCategoryChange(value: String) = _uiState.update { it.copy(category = value) }
    fun onTypeChange(value: String) = _uiState.update { it.copy(type = value) }
    fun onMentorNameChange(value: String) = _uiState.update { it.copy(mentorName = value) }
    fun onStartDateChange(value: String) = _uiState.update { it.copy(startDate = value) }
    fun onExpectedDateChange(value: String) = _uiState.update { it.copy(expectedCompletionDate = value) }
    fun onStatusChange(value: String) = _uiState.update { it.copy(status = value) }
    fun onProgressChange(value: Int) = _uiState.update { it.copy(progressPercentage = value.coerceIn(0, 100)) }
    fun onRequiredComponentsChange(value: String) = _uiState.update { it.copy(requiredComponents = value) }
    fun onEstimatedBudgetChange(value: String) = _uiState.update { it.copy(estimatedBudget = value) }
    fun onActualExpenseChange(value: String) = _uiState.update { it.copy(actualExpense = value) }
    fun onGithubLinkChange(value: String) = _uiState.update { it.copy(githubLink = value) }
    fun onImageSelected(bytes: ByteArray?) = _uiState.update { it.copy(imageBytes = bytes) }

    fun addTeamMember(student: UserProfile, role: String) {
        val currentList = _uiState.value.teamMembers
        if (currentList.any { it.studentUid == student.uid }) {
            _uiState.update { it.copy(errorMessage = "Student is already in team.") }
            return
        }

        val newMember = ProjectTeamMember(
            studentUid = student.uid,
            studentId = student.studentId.ifBlank { "STU-${student.uid.take(4)}" },
            studentName = student.fullName.ifBlank { "Student" },
            role = role
        )

        _uiState.update {
            it.copy(
                teamMembers = currentList + newMember,
                errorMessage = null
            )
        }
    }

    fun removeTeamMember(studentUid: String) {
        _uiState.update { state ->
            state.copy(teamMembers = state.teamMembers.filter { it.studentUid != studentUid })
        }
    }

    fun createProject(ownerProfile: UserProfile) {
        val state = _uiState.value
        if (state.title.isBlank() || state.description.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Title and Description are required.") }
            return
        }

        val ownerUid = auth.currentUser?.uid ?: ownerProfile.uid
        val ownerMember = ProjectTeamMember(
            studentUid = ownerUid,
            studentId = ownerProfile.studentId.ifBlank { "STU-${ownerUid.take(4)}" },
            studentName = ownerProfile.fullName.ifBlank { "Project Owner" },
            role = ProjectTeamMember.ROLE_OWNER
        )

        // Ensure owner is at least in team
        val fullTeam = if (state.teamMembers.none { it.studentUid == ownerUid }) {
            listOf(ownerMember) + state.teamMembers
        } else {
            state.teamMembers
        }

        val budget = state.estimatedBudget.toDoubleOrNull() ?: 0.0
        val expense = state.actualExpense.toDoubleOrNull() ?: 0.0

        val project = Project(
            title = state.title.trim(),
            description = state.description.trim(),
            category = state.category,
            type = state.type,
            ownerUid = ownerUid,
            ownerStudentId = ownerProfile.studentId.ifBlank { "RW-STU-${ownerUid.take(4)}" },
            ownerName = ownerProfile.fullName.ifBlank { "Project Owner" },
            teamMembers = fullTeam,
            mentorName = state.mentorName.trim(),
            startDate = state.startDate,
            expectedCompletionDate = state.expectedCompletionDate,
            status = state.status,
            progressPercentage = state.progressPercentage,
            requiredComponents = state.requiredComponents.trim(),
            estimatedBudget = budget,
            actualExpense = expense,
            githubLink = state.githubLink.trim()
        )

        viewModelScope.launch {
            projectRepository.createProject(project, state.imageBytes).collect { resource ->
                when (resource) {
                    is Resource.Loading -> _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
                    is Resource.Success -> _uiState.update { it.copy(isSubmitting = false, isSuccess = true) }
                    is Resource.Error -> _uiState.update { it.copy(isSubmitting = false, errorMessage = resource.message) }
                }
            }
        }
    }

    fun clearError() = _uiState.update { it.copy(errorMessage = null) }
}
