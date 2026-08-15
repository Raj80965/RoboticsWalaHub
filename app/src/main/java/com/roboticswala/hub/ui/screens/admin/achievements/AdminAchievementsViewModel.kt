package com.roboticswala.hub.ui.screens.admin.achievements

import androidx.lifecycle.ViewModel
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

data class AdminAchievementsUiState(
    val achievements: List<Achievement> = emptyList(),
    val filteredAchievements: List<Achievement> = emptyList(),
    val statusFilter: String = "All",
    val categoryFilter: String = "All",
    val levelFilter: String = "All",
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val selectedAchievementForReview: Achievement? = null,
    val isActionLoading: Boolean = false,
    val snackbarMessage: String? = null,
    val errorMessage: String? = null
)

class AdminAchievementsViewModel(
    private val repository: AchievementRepository = FirestoreAchievementRepository(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminAchievementsUiState())
    val uiState: StateFlow<AdminAchievementsUiState> = _uiState.asStateFlow()

    init {
        observeAllAchievements()
    }

    private fun observeAllAchievements() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.observeAllAdminAchievements().collect { list ->
                _uiState.update { state ->
                    state.copy(
                        achievements = list,
                        filteredAchievements = filterList(list, state.statusFilter, state.categoryFilter, state.levelFilter, state.searchQuery),
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
                filteredAchievements = filterList(state.achievements, status, state.categoryFilter, state.levelFilter, state.searchQuery)
            )
        }
    }

    fun setCategoryFilter(category: String) {
        _uiState.update { state ->
            state.copy(
                categoryFilter = category,
                filteredAchievements = filterList(state.achievements, state.statusFilter, category, state.levelFilter, state.searchQuery)
            )
        }
    }

    fun setLevelFilter(level: String) {
        _uiState.update { state ->
            state.copy(
                levelFilter = level,
                filteredAchievements = filterList(state.achievements, state.statusFilter, state.categoryFilter, level, state.searchQuery)
            )
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                filteredAchievements = filterList(state.achievements, state.statusFilter, state.categoryFilter, state.levelFilter, query)
            )
        }
    }

    private fun filterList(
        list: List<Achievement>,
        status: String,
        category: String,
        level: String,
        query: String
    ): List<Achievement> {
        return list.filter { item ->
            val matchesStatus = status == "All" || item.status.equals(status, ignoreCase = true)
            val matchesCat = category == "All" || item.category.equals(category, ignoreCase = true)
            val matchesLevel = level == "All" || item.achievementLevel.equals(level, ignoreCase = true)
            val matchesQuery = query.isBlank() ||
                    item.title.contains(query, ignoreCase = true) ||
                    item.studentName.contains(query, ignoreCase = true) ||
                    item.studentId.contains(query, ignoreCase = true) ||
                    item.organizationName.contains(query, ignoreCase = true)

            matchesStatus && matchesCat && matchesLevel && matchesQuery
        }
    }

    fun openReviewModal(achievement: Achievement) = _uiState.update { it.copy(selectedAchievementForReview = achievement) }
    fun closeReviewModal() = _uiState.update { it.copy(selectedAchievementForReview = null) }

    fun approveAchievement(achievementId: String) {
        val adminUid = auth.currentUser?.uid ?: "Admin"
        val adminName = auth.currentUser?.displayName ?: "Lab Admin"

        viewModelScope.launch {
            repository.adminApproveAchievement(achievementId, adminUid, adminName).collect { res ->
                when (res) {
                    is Resource.Loading -> _uiState.update { it.copy(isActionLoading = true) }
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                isActionLoading = false,
                                selectedAchievementForReview = null,
                                snackbarMessage = "Achievement approved and added to student profile!"
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isActionLoading = false,
                                errorMessage = res.message ?: "Failed to approve."
                            )
                        }
                    }
                }
            }
        }
    }

    fun rejectAchievement(achievementId: String, reason: String) {
        viewModelScope.launch {
            repository.adminRejectAchievement(achievementId, reason).collect { res ->
                when (res) {
                    is Resource.Loading -> _uiState.update { it.copy(isActionLoading = true) }
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                isActionLoading = false,
                                selectedAchievementForReview = null,
                                snackbarMessage = "Achievement marked as Rejected."
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isActionLoading = false,
                                errorMessage = res.message ?: "Failed to reject."
                            )
                        }
                    }
                }
            }
        }
    }

    fun returnForCorrection(achievementId: String, message: String) {
        viewModelScope.launch {
            repository.adminReturnForCorrection(achievementId, message).collect { res ->
                when (res) {
                    is Resource.Loading -> _uiState.update { it.copy(isActionLoading = true) }
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                isActionLoading = false,
                                selectedAchievementForReview = null,
                                snackbarMessage = "Achievement returned to student for corrections."
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isActionLoading = false,
                                errorMessage = res.message ?: "Failed to return."
                            )
                        }
                    }
                }
            }
        }
    }

    fun clearMessage() = _uiState.update { it.copy(snackbarMessage = null, errorMessage = null) }
}
