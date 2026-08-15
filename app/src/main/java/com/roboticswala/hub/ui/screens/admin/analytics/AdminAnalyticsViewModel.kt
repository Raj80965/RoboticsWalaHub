package com.roboticswala.hub.ui.screens.admin.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roboticswala.hub.data.models.AdminHubAnalytics
import com.roboticswala.hub.data.models.LeaderboardEntry
import com.roboticswala.hub.data.repository.AnalyticsRepository
import com.roboticswala.hub.data.repository.FirestoreAnalyticsRepository
import com.roboticswala.hub.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminAnalyticsUiState(
    val analytics: AdminHubAnalytics? = null,
    val leaderboard: List<LeaderboardEntry> = emptyList(),
    val selectedTab: Int = 0, // 0: Overview, 1: Projects & Tasks, 2: Inventory & Finance, 3: Leaderboard & Reports
    val isLoading: Boolean = true,
    val isExporting: Boolean = false,
    val exportSuccessMessage: String? = null,
    val errorMessage: String? = null
)

class AdminAnalyticsViewModel(
    private val analyticsRepository: AnalyticsRepository = FirestoreAnalyticsRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminAnalyticsUiState())
    val uiState: StateFlow<AdminAnalyticsUiState> = _uiState.asStateFlow()

    init {
        loadAnalytics()
        loadLeaderboard()
    }

    fun selectTab(tabIndex: Int) = _uiState.update { it.copy(selectedTab = tabIndex) }

    fun loadAnalytics() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            analyticsRepository.getAdminHubAnalytics().collect { res ->
                when (res) {
                    is Resource.Loading -> _uiState.update { it.copy(isLoading = true) }
                    is Resource.Success -> _uiState.update { it.copy(analytics = res.data, isLoading = false) }
                    is Resource.Error -> _uiState.update { it.copy(isLoading = false, errorMessage = res.message) }
                }
            }
        }
    }

    private fun loadLeaderboard() {
        viewModelScope.launch {
            analyticsRepository.getLeaderboard().collect { res ->
                if (res is Resource.Success) {
                    _uiState.update { it.copy(leaderboard = res.data ?: emptyList()) }
                }
            }
        }
    }

    fun exportReport(reportType: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isExporting = true) }
            kotlinx.coroutines.delay(1200) // Simulated PDF generator
            _uiState.update {
                it.copy(
                    isExporting = false,
                    exportSuccessMessage = "$reportType report compiled and saved to Documents/RoboticsWalaHub_${System.currentTimeMillis()}.pdf"
                )
            }
        }
    }

    fun clearExportMessage() = _uiState.update { it.copy(exportSuccessMessage = null, errorMessage = null) }
}
