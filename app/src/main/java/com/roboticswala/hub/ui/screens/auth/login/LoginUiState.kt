package com.roboticswala.hub.ui.screens.auth.login

data class LoginUiState(
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val showForgotPasswordDialog: Boolean = false,
    val generalMessage: String? = null
)
