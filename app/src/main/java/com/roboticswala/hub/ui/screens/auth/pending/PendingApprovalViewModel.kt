package com.roboticswala.hub.ui.screens.auth.pending

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roboticswala.hub.data.models.UserProfile
import com.roboticswala.hub.data.repository.AuthRepository
import com.roboticswala.hub.data.repository.FirebaseAuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PendingApprovalUiState(
    val userProfile: UserProfile? = null,
    val isChecking: Boolean = false,
    val statusMessage: String? = null,
    val isApproved: Boolean = false
)

class PendingApprovalViewModel(
    private val authRepository: AuthRepository = FirebaseAuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(PendingApprovalUiState())
    val uiState: StateFlow<PendingApprovalUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            authRepository.getCurrentUserProfile().onSuccess { profile ->
                _uiState.update { it.copy(userProfile = profile) }
            }
        }
    }

    fun checkApprovalStatus(onApproved: () -> Unit) {
        val uid = authRepository.currentUserId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isChecking = true, statusMessage = null) }
            authRepository.checkUserStatus(uid).onSuccess { status ->
                _uiState.update { it.copy(isChecking = false) }
                if (status.equals("Approved", ignoreCase = true)) {
                    _uiState.update { it.copy(isApproved = true, statusMessage = "Access approved! Entering Hub...") }
                    onApproved()
                } else {
                    _uiState.update { it.copy(statusMessage = "Status: Still Pending Admin Review. Please check back shortly.") }
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isChecking = false,
                        statusMessage = error.message ?: "Failed to check status"
                    )
                }
            }
        }
    }

    fun logout(onLoggedOut: () -> Unit) {
        authRepository.logout()
        onLoggedOut()
    }

    fun clearStatusMessage() {
        _uiState.update { it.copy(statusMessage = null) }
    }
}
