package com.roboticswala.hub.ui.screens.auth.login

import android.util.Patterns
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

sealed interface LoginDestination {
    object StudentDashboard : LoginDestination
    object AdminDashboard : LoginDestination
    object PendingApproval : LoginDestination
}

class LoginViewModel(
    private val authRepository: AuthRepository = FirebaseAuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChanged(newEmail: String) {
        _uiState.update {
            it.copy(
                email = newEmail,
                emailError = null,
                generalMessage = null
            )
        }
    }

    fun onPasswordChanged(newPassword: String) {
        _uiState.update {
            it.copy(
                password = newPassword,
                passwordError = null,
                generalMessage = null
            )
        }
    }

    fun togglePasswordVisibility() {
        _uiState.update {
            it.copy(isPasswordVisible = !it.isPasswordVisible)
        }
    }

    fun openForgotPasswordDialog() {
        _uiState.update { it.copy(showForgotPasswordDialog = true) }
    }

    fun closeForgotPasswordDialog() {
        _uiState.update { it.copy(showForgotPasswordDialog = false) }
    }

    fun sendPasswordReset(email: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    generalMessage = "Password reset link dispatched to $email"
                )
            }
        }
    }

    fun onLoginClicked(onDestinationResolved: (LoginDestination) -> Unit) {
        val current = _uiState.value
        var hasError = false
        var emailErr: String? = null
        var passErr: String? = null

        if (current.email.isBlank()) {
            emailErr = "Email address is required"
            hasError = true
        } else if (!Patterns.EMAIL_ADDRESS.matcher(current.email.trim()).matches()) {
            emailErr = "Please enter a valid email address"
            hasError = true
        }

        if (current.password.isBlank()) {
            passErr = "Password is required"
            hasError = true
        } else if (current.password.length < 6) {
            passErr = "Password must be at least 6 characters"
            hasError = true
        }

        if (hasError) {
            _uiState.update {
                it.copy(
                    emailError = emailErr,
                    passwordError = passErr
                )
            }
            return
        }

        // Real Firebase Authentication & Firestore Role Resolution
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, generalMessage = null) }
            
            authRepository.loginUser(current.email, current.password)
                .onSuccess { profile ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true,
                            generalMessage = "Welcome back, ${profile.fullName}!"
                        )
                    }

                    // Role & Status Routing
                    when {
                        profile.isAdmin -> onDestinationResolved(LoginDestination.AdminDashboard)
                        profile.isStudent && profile.isApproved -> onDestinationResolved(LoginDestination.StudentDashboard)
                        profile.isStudent && profile.isPending -> onDestinationResolved(LoginDestination.PendingApproval)
                        else -> onDestinationResolved(LoginDestination.PendingApproval)
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            generalMessage = error.message ?: "Authentication failed. Please verify credentials."
                        )
                    }
                }
        }
    }

    fun clearGeneralMessage() {
        _uiState.update { it.copy(generalMessage = null) }
    }
}
