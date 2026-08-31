package com.roboticswala.hub.ui.screens.auth.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.roboticswala.hub.ui.components.ForgotPasswordDialog
import com.roboticswala.hub.ui.components.RoboticsBackground
import com.roboticswala.hub.ui.components.RoboticsLogo
import com.roboticswala.hub.ui.components.RoboticsOutlinedButton
import com.roboticswala.hub.ui.components.RoboticsPrimaryButton
import com.roboticswala.hub.ui.components.RoboticsTextField
import com.roboticswala.hub.ui.components.RoboticsTextLink
import com.roboticswala.hub.ui.theme.CyberCyan
import com.roboticswala.hub.ui.theme.DarkSurface
import com.roboticswala.hub.ui.theme.DarkSurfaceBorder
import com.roboticswala.hub.ui.theme.ElectricBlue
import com.roboticswala.hub.ui.theme.LightSurface
import com.roboticswala.hub.ui.theme.LightSurfaceBorder
import com.roboticswala.hub.ui.theme.TextPrimaryDark
import com.roboticswala.hub.ui.theme.TextPrimaryLight
import com.roboticswala.hub.ui.theme.TextSecondaryDark
import com.roboticswala.hub.ui.theme.TextSecondaryLight

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToStudent: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onNavigateToPendingApproval: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isDark = isSystemInDarkTheme()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    LaunchedEffect(uiState.generalMessage) {
        uiState.generalMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearGeneralMessage()
        }
    }

    RoboticsBackground {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                // Brand Emblem Header
                RoboticsLogo(
                    size = 76.dp,
                    animate = false
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Screen Title & Subtitle
                Text(
                    text = "RW HUB",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp
                    ),
                    color = if (isDark) TextPrimaryDark else TextPrimaryLight,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Sign in to access your robotics lab & projects",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isDark) TextSecondaryDark else TextSecondaryLight,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Main Form Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = if (isDark) DarkSurfaceBorder else LightSurfaceBorder,
                            shape = RoundedCornerShape(20.dp)
                        ),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDark) DarkSurface.copy(alpha = 0.95f) else LightSurface.copy(alpha = 0.95f)
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = if (isDark) 6.dp else 2.dp
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        // Email Field
                        RoboticsTextField(
                            value = uiState.email,
                            onValueChange = viewModel::onEmailChanged,
                            label = "Email Address",
                            placeholder = "engineer@roboticswala.com",
                            leadingIcon = Icons.Filled.Email,
                            errorMessage = uiState.emailError,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Password Field
                        RoboticsTextField(
                            value = uiState.password,
                            onValueChange = viewModel::onPasswordChanged,
                            label = "Password",
                            placeholder = "Enter your password",
                            leadingIcon = Icons.Filled.Lock,
                            isPassword = true,
                            isPasswordVisible = uiState.isPasswordVisible,
                            onPasswordToggleClick = viewModel::togglePasswordVisibility,
                            errorMessage = uiState.passwordError,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    keyboardController?.hide()
                                    viewModel.onLoginClicked { destination ->
                                        when (destination) {
                                            LoginDestination.StudentDashboard -> onNavigateToStudent()
                                            LoginDestination.AdminDashboard -> onNavigateToAdmin()
                                            LoginDestination.PendingApproval -> onNavigateToPendingApproval()
                                        }
                                    }
                                }
                            )
                        )

                        // Forgot Password Link
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 2.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            RoboticsTextLink(
                                text = "Forgot Password?",
                                onClick = viewModel::openForgotPasswordDialog,
                                color = if (isDark) CyberCyan else ElectricBlue
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Sign In Button
                        RoboticsPrimaryButton(
                            text = "Sign In",
                            onClick = {
                                keyboardController?.hide()
                                viewModel.onLoginClicked { destination ->
                                    when (destination) {
                                        LoginDestination.StudentDashboard -> onNavigateToStudent()
                                        LoginDestination.AdminDashboard -> onNavigateToAdmin()
                                        LoginDestination.PendingApproval -> onNavigateToPendingApproval()
                                    }
                                }
                            },
                            isLoading = uiState.isLoading,
                            trailingIcon = Icons.Filled.ArrowForward
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Divider with "OR"
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = if (isDark) DarkSurfaceBorder else LightSurfaceBorder
                    )
                    Text(
                        text = "  NEW TO ROBOTICS WALA?  ",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp
                        ),
                        color = if (isDark) TextSecondaryDark.copy(alpha = 0.8f) else TextSecondaryLight.copy(alpha = 0.8f)
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = if (isDark) DarkSurfaceBorder else LightSurfaceBorder
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Create Account Button
                RoboticsOutlinedButton(
                    text = "Create Account",
                    onClick = onNavigateToRegister,
                    leadingIcon = Icons.Filled.PersonAdd
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Snackbar Host
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            )

            // Forgot Password Modal Dialog
            if (uiState.showForgotPasswordDialog) {
                ForgotPasswordDialog(
                    onDismissRequest = viewModel::closeForgotPasswordDialog,
                    onSendResetLink = { email ->
                        viewModel.sendPasswordReset(email)
                        viewModel.closeForgotPasswordDialog()
                    }
                )
            }
        }
    }
}
