package com.roboticswala.hub.ui.screens.student.achievements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.roboticswala.hub.data.models.Achievement
import com.roboticswala.hub.data.repository.AchievementRepository
import com.roboticswala.hub.data.repository.FirestoreAchievementRepository
import com.roboticswala.hub.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StudentAchievementsUiState(
    val achievements: List<Achievement> = emptyList(),
    val filteredAchievements: List<Achievement> = emptyList(),
    val statusFilter: String = "All",
    val categoryFilter: String = "All",
    val searchQuery: String = "",
    val sortNewestFirst: Boolean = true,
    val isLoading: Boolean = true,
    val selectedAchievementForDetails: Achievement? = null,
    val achievementToDelete: Achievement? = null,
    val snackbarMessage: String? = null,
    val errorMessage: String? = null
)

class StudentAchievementsViewModel(
    private val studentUid: String = FirebaseAuth.getInstance().currentUser?.uid ?: "",
    private val repository: AchievementRepository = FirestoreAchievementRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudentAchievementsUiState())
    val uiState: StateFlow<StudentAchievementsUiState> = _uiState.asStateFlow()

    init {
        if (studentUid.isNotBlank()) {
            observeAchievements()
        }
    }

    private fun observeAchievements() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.observeStudentAchievements(studentUid).collect { list ->
                _uiState.update { state ->
                    state.copy(
                        achievements = list,
                        filteredAchievements = filterAndSort(
                            list,
                            state.statusFilter,
                            state.categoryFilter,
                            state.searchQuery,
                            state.sortNewestFirst
                        ),
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
                filteredAchievements = filterAndSort(state.achievements, status, state.categoryFilter, state.searchQuery, state.sortNewestFirst)
            )
        }
    }

    fun setCategoryFilter(category: String) {
        _uiState.update { state ->
            state.copy(
                categoryFilter = category,
                filteredAchievements = filterAndSort(state.achievements, state.statusFilter, category, state.searchQuery, state.sortNewestFirst)
            )
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                filteredAchievements = filterAndSort(state.achievements, state.statusFilter, state.categoryFilter, query, state.sortNewestFirst)
            )
        }
    }

    fun toggleSortOrder() {
        _uiState.update { state ->
            val newSort = !state.sortNewestFirst
            state.copy(
                sortNewestFirst = newSort,
                filteredAchievements = filterAndSort(state.achievements, state.statusFilter, state.categoryFilter, state.searchQuery, newSort)
            )
        }
    }

    private fun filterAndSort(
        list: List<Achievement>,
        status: String,
        category: String,
        query: String,
        newestFirst: Boolean
    ): List<Achievement> {
        val filtered = list.filter { item ->
            val matchesStatus = status == "All" || item.status.equals(status, ignoreCase = true)
            val matchesCat = category == "All" || item.category.equals(category, ignoreCase = true)
            val matchesQuery = query.isBlank() ||
                    item.title.contains(query, ignoreCase = true) ||
                    item.organizationName.contains(query, ignoreCase = true) ||
                    item.description.contains(query, ignoreCase = true)

            matchesStatus && matchesCat && matchesQuery
        }

        return if (newestFirst) {
            filtered.sortedByDescending { it.achievementDate }
        } else {
            filtered.sortedBy { it.achievementDate }
        }
    }

    fun openDetails(achievement: Achievement) = _uiState.update { it.copy(selectedAchievementForDetails = achievement) }
    fun closeDetails() = _uiState.update { it.copy(selectedAchievementForDetails = null) }

    fun promptDelete(achievement: Achievement) = _uiState.update { it.copy(achievementToDelete = achievement) }
    fun dismissDelete() = _uiState.update { it.copy(achievementToDelete = null) }

    fun confirmDelete() {
        val item = _uiState.value.achievementToDelete ?: return
        viewModelScope.launch {
            repository.deleteAchievement(item.achievementId, studentUid).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                achievementToDelete = null,
                                selectedAchievementForDetails = null,
                                snackbarMessage = "Achievement deleted."
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                achievementToDelete = null,
                                snackbarMessage = resource.message ?: "Failed to delete."
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

class StudentAchievementsViewModelFactory(
    private val studentUid: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StudentAchievementsViewModel::class.java)) {
            return StudentAchievementsViewModel(studentUid = studentUid) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
