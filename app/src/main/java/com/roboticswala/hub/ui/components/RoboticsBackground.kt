package com.roboticswala.hub.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.roboticswala.hub.ui.theme.CyberCyan
import com.roboticswala.hub.ui.theme.DarkBackground
import com.roboticswala.hub.ui.theme.ElectricBlue
import com.roboticswala.hub.ui.theme.LightBackground

/**
 * Modern robotics tech background with ambient glowing mesh and subtle circuit coordinate grid.
 */
@Composable
fun RoboticsBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val bgColor = if (isDark) DarkBackground else LightBackground
    val gridLineColor = if (isDark) ElectricBlue.copy(alpha = 0.04f) else ElectricBlue.copy(alpha = 0.035f)
    val dotColor = if (isDark) CyberCyan.copy(alpha = 0.12f) else ElectricBlue.copy(alpha = 0.10f)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        // Ambient Top/Bottom Glow Radial Gradients
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // Top Cyan/Blue Glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        if (isDark) CyberCyan.copy(alpha = 0.14f) else CyberCyan.copy(alpha = 0.09f),
                        if (isDark) ElectricBlue.copy(alpha = 0.06f) else ElectricBlue.copy(alpha = 0.04f),
                        Color.Transparent
                    ),
                    center = Offset(canvasWidth * 0.5f, -50f),
                    radius = canvasWidth * 0.9f
                ),
                radius = canvasWidth * 0.9f,
                center = Offset(canvasWidth * 0.5f, -50f)
            )

            // Bottom Right Ambient Glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        if (isDark) ElectricBlue.copy(alpha = 0.12f) else ElectricBlue.copy(alpha = 0.06f),
                        Color.Transparent
                    ),
                    center = Offset(canvasWidth * 0.9f, canvasHeight * 0.95f),
                    radius = canvasWidth * 0.8f
                ),
                radius = canvasWidth * 0.8f,
                center = Offset(canvasWidth * 0.9f, canvasHeight * 0.95f)
            )

            // Circuit Grid Lines & Intersection Dots
            val step = 44.dp.toPx()
            var x = 0f
            while (x < canvasWidth) {
                drawLine(
                    color = gridLineColor,
                    start = Offset(x, 0f),
                    end = Offset(x, canvasHeight),
                    strokeWidth = 1f
                )
                x += step
            }

            var y = 0f
            while (y < canvasHeight) {
                drawLine(
                    color = gridLineColor,
                    start = Offset(0f, y),
                    end = Offset(canvasWidth, y),
                    strokeWidth = 1f
                )
                y += step
            }

            // High-tech intersection dots
            x = 0f
            while (x < canvasWidth) {
                y = 0f
                while (y < canvasHeight) {
                    drawCircle(
                        color = dotColor,
                        radius = 1.8f,
                        center = Offset(x, y)
                    )
                    y += step * 2
                }
                x += step * 2
            }
        }

        // Screen Content
        content()
    }
}
