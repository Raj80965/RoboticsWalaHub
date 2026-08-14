package com.roboticswala.hub.ui.screens.auth.register

data class RegisterUiState(
    val fullName: String = "",
    val fullNameError: String? = null,
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val isPasswordVisible: Boolean = false,
    val confirmPassword: String = "",
    val confirmPasswordError: String? = null,
    val isConfirmPasswordVisible: Boolean = false,
    val agreeToTerms: Boolean = false,
    val termsError: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val generalMessage: String? = null
)
