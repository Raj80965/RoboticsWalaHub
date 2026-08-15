package com.roboticswala.hub.ui.screens.student.work

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.roboticswala.hub.data.models.DailyWorkUpdate
import com.roboticswala.hub.data.models.UserProfile
import com.roboticswala.hub.data.repository.FirestoreWorkAndTaskRepository
import com.roboticswala.hub.data.repository.WorkAndTaskRepository
import com.roboticswala.hub.utils.BookingTimeUtils
import com.roboticswala.hub.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DailyWorkUiState(
    val workHistory: List<DailyWorkUpdate> = emptyList(),
    val labFeed: List<DailyWorkUpdate> = emptyList(),
    val selectedTab: Int = 0, // 0 = Feed, 1 = My History
    val searchQuery: String = "",
    val isLogging: Boolean = false,
    val showCreateDialog: Boolean = false,
    val selectedUpdateForDetail: DailyWorkUpdate? = null,
    val updateToDelete: DailyWorkUpdate? = null,
    val snackbarMessage: String? = null,
    val errorMessage: String? = null
)

class DailyWorkViewModel(
    private val studentUid: String = FirebaseAuth.getInstance().currentUser?.uid ?: "",
    private val repository: WorkAndTaskRepository = FirestoreWorkAndTaskRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DailyWorkUiState())
    val uiState: StateFlow<DailyWorkUiState> = _uiState.asStateFlow()

    init {
        observeFeed()
        if (studentUid.isNotBlank()) {
            observeHistory()
        }
    }

    private fun observeFeed() {
        viewModelScope.launch {
            repository.observeLabWorkFeed().collect { feed ->
                _uiState.update { it.copy(labFeed = feed) }
            }
        }
    }

    private fun observeHistory() {
        viewModelScope.launch {
            repository.observeStudentWorkHistory(studentUid).collect { history ->
                _uiState.update { it.copy(workHistory = history) }
            }
        }
    }

    fun selectTab(tabIndex: Int) = _uiState.update { it.copy(selectedTab = tabIndex) }
    fun setSearchQuery(query: String) = _uiState.update { it.copy(searchQuery = query) }

    fun openCreateDialog() = _uiState.update { it.copy(showCreateDialog = true, errorMessage = null) }
    fun closeCreateDialog() = _uiState.update { it.copy(showCreateDialog = false, errorMessage = null) }

    fun openDetailModal(update: DailyWorkUpdate) = _uiState.update { it.copy(selectedUpdateForDetail = update) }
    fun closeDetailModal() = _uiState.update { it.copy(selectedUpdateForDetail = null) }

    fun promptDelete(update: DailyWorkUpdate) = _uiState.update { it.copy(updateToDelete = update) }
    fun dismissDelete() = _uiState.update { it.copy(updateToDelete = null) }

    fun createWorkUpdate(
        title: String,
        description: String,
        projectName: String,
        hoursWorked: Double,
        problemsFaced: String,
        nextSteps: String,
        workDate: String,
        imageBytes: ByteArray?,
        userProfile: UserProfile
    ) {
        val update = DailyWorkUpdate(
            studentUid = studentUid,
            studentId = userProfile.studentId.ifBlank { "STU-${studentUid.take(4)}" },
            studentName = userProfile.fullName.ifBlank { "Student" },
            studentProfilePhotoUrl = userProfile.photoUrl,
            title = title.trim(),
            description = description.trim(),
            relatedProjectName = projectName.trim(),
            hoursWorked = hoursWorked,
            problemsFaced = problemsFaced.trim(),
            nextSteps = nextSteps.trim(),
            workDate = workDate.ifBlank { BookingTimeUtils.getTodayDateString() }
        )

        viewModelScope.launch {
            repository.createDailyWorkUpdate(update, imageBytes).collect { resource ->
                when (resource) {
                    is Resource.Loading -> _uiState.update { it.copy(isLogging = true, errorMessage = null) }
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                isLogging = false,
                                showCreateDialog = false,
                                snackbarMessage = "Daily work progress recorded successfully!"
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isLogging = false,
                                errorMessage = resource.message ?: "Failed to record daily work."
                            )
                        }
                    }
                }
            }
        }
    }

    fun confirmDeleteWorkUpdate() {
        val update = _uiState.value.updateToDelete ?: return
        viewModelScope.launch {
            repository.deleteDailyWorkUpdate(update.updateId, studentUid).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                updateToDelete = null,
                                selectedUpdateForDetail = null,
                                snackbarMessage = "Work log entry deleted."
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                updateToDelete = null,
                                snackbarMessage = resource.message ?: "Failed to delete log entry."
                            )
                        }
                    }
                }
            }
        }
    }

    fun clearMessage() = _uiState.update { it.copy(snackbarMessage = null, errorMessage = null) }
}

class DailyWorkViewModelFactory(
    private val studentUid: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DailyWorkViewModel::class.java)) {
            return DailyWorkViewModel(studentUid = studentUid) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
