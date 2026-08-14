package com.roboticswala.hub.ui.screens.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.roboticswala.hub.ui.components.RoboticsBackground
import com.roboticswala.hub.ui.components.RoboticsLogo
import com.roboticswala.hub.ui.theme.CyberCyan
import com.roboticswala.hub.ui.theme.CyberCyanGlow
import com.roboticswala.hub.ui.theme.DarkSurfaceBorder
import com.roboticswala.hub.ui.theme.ElectricBlue
import com.roboticswala.hub.ui.theme.LightSurfaceBorder
import com.roboticswala.hub.ui.theme.TextPrimaryDark
import com.roboticswala.hub.ui.theme.TextPrimaryLight
import com.roboticswala.hub.ui.theme.TextSecondaryDark
import com.roboticswala.hub.ui.theme.TextSecondaryLight
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToStudent: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onNavigateToPending: () -> Unit,
    viewModel: SplashViewModel = viewModel()
) {
    val isDark = isSystemInDarkTheme()
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
        viewModel.navigationEvent.collectLatest { event ->
            when (event) {
                is SplashNavigationEvent.NavigateToLogin -> onNavigateToLogin()
                is SplashNavigationEvent.NavigateToStudent -> onNavigateToStudent()
                is SplashNavigationEvent.NavigateToAdmin -> onNavigateToAdmin()
                is SplashNavigationEvent.NavigateToPending -> onNavigateToPending()
            }
        }
    }

    RoboticsBackground {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { viewModel.onSkipClicked() }
                ),
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(700)) + slideInVertically(
                    initialOffsetY = { 60 },
                    animationSpec = tween(700)
                )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(horizontal = 32.dp)
                ) {
                    // Robotics Logo with Pulse & Glowing Sensor Visor
                    RoboticsLogo(
                        size = 120.dp,
                        animate = true
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // App Title
                    Text(
                        text = "Robotics Wala Hub",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.5).sp
                        ),
                        color = if (isDark) TextPrimaryDark else TextPrimaryLight,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Tagline
                    Text(
                        text = "Powering Next-Gen Automation",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.5.sp
                        ),
                        color = if (isDark) CyberCyan else ElectricBlue,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(48.dp))

                    // Futuristic Loader
                    LinearProgressIndicator(
                        modifier = Modifier
                            .width(160.dp)
                            .height(4.dp),
                        color = CyberCyanGlow,
                        trackColor = if (isDark) DarkSurfaceBorder else LightSurfaceBorder,
                        strokeCap = StrokeCap.Round
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "VERIFYING FIREBASE SESSION...",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.2.sp
                        ),
                        color = if (isDark) TextSecondaryDark else TextSecondaryLight
                    )
                }
            }

            // Version info at bottom
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 32.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Text(
                    text = "v1.0.0 • Cloud Edition",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isDark) TextSecondaryDark.copy(alpha = 0.6f) else TextSecondaryLight.copy(alpha = 0.6f)
                )
            }
        }
    }
}
