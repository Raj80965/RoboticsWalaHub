package com.roboticswala.hub.ui.screens.auth.register

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roboticswala.hub.data.repository.AuthRepository
import com.roboticswala.hub.data.repository.FirebaseAuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authRepository: AuthRepository = FirebaseAuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onFullNameChanged(newName: String) {
        _uiState.update {
            it.copy(fullName = newName, fullNameError = null, generalMessage = null)
        }
    }

    fun onEmailChanged(newEmail: String) {
        _uiState.update {
            it.copy(email = newEmail, emailError = null, generalMessage = null)
        }
    }

    fun onPhoneChanged(newPhone: String) {
        _uiState.update {
            it.copy(phone = newPhone, phoneError = null, generalMessage = null)
        }
    }

    fun onParentNameChanged(newParentName: String) {
        _uiState.update {
            it.copy(parentName = newParentName, generalMessage = null)
        }
    }

    fun onParentPhoneChanged(newParentPhone: String) {
        _uiState.update {
            it.copy(parentPhone = newParentPhone, generalMessage = null)
        }
    }

    fun onPasswordChanged(newPassword: String) {
        _uiState.update {
            it.copy(password = newPassword, passwordError = null, generalMessage = null)
        }
    }

    fun onConfirmPasswordChanged(newConfirm: String) {
        _uiState.update {
            it.copy(confirmPassword = newConfirm, confirmPasswordError = null, generalMessage = null)
        }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun toggleConfirmPasswordVisibility() {
        _uiState.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
    }

    fun onAgreeToTermsChanged(agree: Boolean) {
        _uiState.update { it.copy(agreeToTerms = agree, termsError = null) }
    }

    fun onRegisterClicked(onSuccess: () -> Unit = {}) {
        val current = _uiState.value
        var hasError = false
        var nameErr: String? = null
        var emailErr: String? = null
        var passErr: String? = null
        var confirmErr: String? = null
        var termsErr: String? = null

        if (current.fullName.trim().isEmpty()) {
            nameErr = "Full name is required"
            hasError = true
        }

        if (current.email.trim().isEmpty()) {
            emailErr = "Email is required"
            hasError = true
        } else if (!Patterns.EMAIL_ADDRESS.matcher(current.email.trim()).matches()) {
            emailErr = "Please enter a valid email address"
            hasError = true
        }

        if (current.password.isEmpty()) {
            passErr = "Password is required"
            hasError = true
        } else if (current.password.length < 6) {
            passErr = "Password must be at least 6 characters"
            hasError = true
        }

        if (current.confirmPassword.isEmpty()) {
            confirmErr = "Please confirm your password"
            hasError = true
        } else if (current.password != current.confirmPassword) {
            confirmErr = "Passwords do not match"
            hasError = true
        }

        if (!current.agreeToTerms) {
            termsErr = "You must agree to the Terms of Service"
            hasError = true
        }

        if (hasError) {
            _uiState.update {
                it.copy(
                    fullNameError = nameErr,
                    emailError = emailErr,
                    passwordError = passErr,
                    confirmPasswordError = confirmErr,
                    termsError = termsErr
                )
            }
            return
        }

        // Real Firebase Authentication & Firestore Registration
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, generalMessage = null) }
            
            authRepository.registerUser(
                fullName = current.fullName,
                email = current.email,
                password = current.password,
                phone = current.phone,
                parentName = current.parentName,
                parentPhone = current.parentPhone
            ).onSuccess { profile ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = true,
                        generalMessage = "Account registered successfully in Firebase!"
                    )
                }
                onSuccess()
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        generalMessage = error.message ?: "Registration failed. Please try again."
                    )
                }
            }
        }
    }

    fun clearGeneralMessage() {
        _uiState.update { it.copy(generalMessage = null) }
    }
}
