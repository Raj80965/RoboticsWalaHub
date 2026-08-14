package com.roboticswala.hub.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Primary Robotics Theme Colors
val ElectricBlue = Color(0xFF0066FF)
val ElectricBlueLight = Color(0xFF3385FF)
val ElectricBlueDark = Color(0xFF0047B3)

val CyberCyan = Color(0xFF00D4FF)
val CyberCyanGlow = Color(0xFF00E5FF)
val DeepCobalt = Color(0xFF0038A8)

// Dark Theme Surfaces & Backgrounds
val DarkBackground = Color(0xFF0A0D14)
val DarkSurface = Color(0xFF121824)
val DarkSurfaceElevated = Color(0xFF1A2232)
val DarkSurfaceBorder = Color(0xFF222F44)
val DarkSurfaceBorderFocused = Color(0xFF00D4FF)

val TextPrimaryDark = Color(0xFFFFFFFF)
val TextSecondaryDark = Color(0xFF8E9EB5)
val TextTertiaryDark = Color(0xFF5A6C84)

// Light Theme Surfaces & Backgrounds
val LightBackground = Color(0xFFF5F8FC)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceElevated = Color(0xFFEDF2F9)
val LightSurfaceBorder = Color(0xFFD7E2EE)
val LightSurfaceBorderFocused = Color(0xFF0066FF)

val TextPrimaryLight = Color(0xFF0B1320)
val TextSecondaryLight = Color(0xFF536479)
val TextTertiaryLight = Color(0xFF8D9EAE)

// Functional / Status Colors
val CircuitSuccess = Color(0xFF00E676)
val CircuitWarning = Color(0xFFFFB300)
val CircuitError = Color(0xFFFF3B30)

// Tech Gradients
val RoboticsPrimaryGradient = Brush.horizontalGradient(
    colors = listOf(
        ElectricBlue,
        CyberCyan
    )
)

val RoboticsButtonHoverGradient = Brush.horizontalGradient(
    colors = listOf(
        ElectricBlueLight,
        CyberCyanGlow
    )
)

val RoboticsDarkCardGradient = Brush.verticalGradient(
    colors = listOf(
        DarkSurfaceElevated.copy(alpha = 0.95f),
        DarkSurface.copy(alpha = 0.98f)
    )
)

val RoboticsLightCardGradient = Brush.verticalGradient(
    colors = listOf(
        LightSurface,
        LightSurfaceElevated.copy(alpha = 0.6f)
    )
)

val RoboticsBackgroundDarkMesh = Brush.radialGradient(
    colors = listOf(
        ElectricBlue.copy(alpha = 0.15f),
        Color.Transparent
    ),
    radius = 1200f
)

val RoboticsBackgroundLightMesh = Brush.radialGradient(
    colors = listOf(
        CyberCyan.copy(alpha = 0.12f),
        Color.Transparent
    ),
    radius = 1200f
)
