package com.roboticswala.hub.ui.screens.student.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.roboticswala.hub.data.models.LeaderboardEntry
import com.roboticswala.hub.data.models.StudentPerformanceReport
import com.roboticswala.hub.data.repository.AnalyticsRepository
import com.roboticswala.hub.data.repository.FirestoreAnalyticsRepository
import com.roboticswala.hub.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StudentLeaderboardUiState(
    val leaderboard: List<LeaderboardEntry> = emptyList(),
    val myPerformanceReport: StudentPerformanceReport? = null,
    val selectedPeriod: String = "ALL_TIME", // ALL_TIME, THIS_MONTH, THIS_WEEK
    val selectedEntryDetails: LeaderboardEntry? = null,
    val isLoading: Boolean = true,
    val isReportLoading: Boolean = true,
    val errorMessage: String? = null
)

class StudentLeaderboardViewModel(
    private val studentUid: String = FirebaseAuth.getInstance().currentUser?.uid ?: "",
    private val analyticsRepository: AnalyticsRepository = FirestoreAnalyticsRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudentLeaderboardUiState())
    val uiState: StateFlow<StudentLeaderboardUiState> = _uiState.asStateFlow()

    init {
        loadLeaderboard("ALL_TIME")
        loadMyPerformance()
    }

    fun loadLeaderboard(period: String) {
        _uiState.update { it.copy(selectedPeriod = period, isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            analyticsRepository.getLeaderboard(period).collect { res ->
                when (res) {
                    is Resource.Loading -> _uiState.update { it.copy(isLoading = true) }
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                leaderboard = res.data ?: emptyList(),
                                isLoading = false
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = res.message ?: "Failed to load leaderboard."
                            )
                        }
                    }
                }
            }
        }
    }

    fun loadMyPerformance() {
        if (studentUid.isBlank()) return
        viewModelScope.launch {
            analyticsRepository.getStudentPerformanceReport(studentUid).collect { res ->
                when (res) {
                    is Resource.Loading -> _uiState.update { it.copy(isReportLoading = true) }
                    is Resource.Success -> _uiState.update { it.copy(myPerformanceReport = res.data, isReportLoading = false) }
                    is Resource.Error -> _uiState.update { it.copy(isReportLoading = false) }
                }
            }
        }
    }

    fun selectEntry(entry: LeaderboardEntry) = _uiState.update { it.copy(selectedEntryDetails = entry) }
    fun closeDetails() = _uiState.update { it.copy(selectedEntryDetails = null) }
}

class StudentLeaderboardViewModelFactory(
    private val studentUid: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StudentLeaderboardViewModel::class.java)) {
            return StudentLeaderboardViewModel(studentUid = studentUid) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
