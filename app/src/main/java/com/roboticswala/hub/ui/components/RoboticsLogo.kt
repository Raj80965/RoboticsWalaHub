package com.roboticswala.hub.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.roboticswala.hub.ui.theme.CyberCyan
import com.roboticswala.hub.ui.theme.CyberCyanGlow
import com.roboticswala.hub.ui.theme.DarkSurface
import com.roboticswala.hub.ui.theme.ElectricBlue
import com.roboticswala.hub.ui.theme.ElectricBlueLight
import kotlin.math.cos
import kotlin.math.sin

/**
 * Modern, high-tech animated robotics emblem logo for "Robotics Wala Hub".
 * Features a glowing pulse aura, robotic visor eyes, hexagonal shield, and antenna node.
 */
@Composable
fun RoboticsLogo(
    modifier: Modifier = Modifier,
    size: Dp = 100.dp,
    animate: Boolean = true
) {
    val isDark = isSystemInDarkTheme()
    val infiniteTransition = rememberInfiniteTransition(label = "RoboticsLogoPulse")

    val pulseScale by if (animate) {
        infiniteTransition.animateFloat(
            initialValue = 0.95f,
            targetValue = 1.05f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulseScale"
        )
    } else {
        androidx.compose.runtime.remember { androidx.compose.runtime.mutableFloatStateOf(1f) }
    }

    val glowAlpha by if (animate) {
        infiniteTransition.animateFloat(
            initialValue = 0.35f,
            targetValue = 0.85f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "glowAlpha"
        )
    } else {
        androidx.compose.runtime.remember { androidx.compose.runtime.mutableFloatStateOf(0.6f) }
    }

    Box(
        modifier = modifier
            .size(size)
            .scale(pulseScale),
        contentAlignment = Alignment.Center
    ) {
        // Outer glowing pulse ring
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            CyberCyanGlow.copy(alpha = glowAlpha * 0.45f),
                            ElectricBlue.copy(alpha = glowAlpha * 0.15f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Main Robot Emblem Container
        Box(
            modifier = Modifier
                .size(size * 0.82f)
                .shadow(
                    elevation = if (isDark) 16.dp else 8.dp,
                    shape = RoundedCornerShape(24.dp),
                    ambientColor = ElectricBlue,
                    spotColor = CyberCyan
                )
                .clip(RoundedCornerShape(24.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = if (isDark) {
                            listOf(
                                Color(0xFF162032),
                                DarkSurface
                            )
                        } else {
                            listOf(
                                Color(0xFF0F1B2E),
                                Color(0xFF0A1220)
                            )
                        }
                    )
                )
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            CyberCyan.copy(alpha = 0.9f),
                            ElectricBlue.copy(alpha = 0.4f),
                            CyberCyanGlow.copy(alpha = 0.8f)
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            // High-Tech Canvas Drawing for Robot Head, Eyes & Circuits
            Canvas(
                modifier = Modifier.size(size * 0.65f)
            ) {
                val canvasWidth = this.size.width
                val canvasHeight = this.size.height

                // 1. Antenna stem & glowing node
                val antennaTop = Offset(canvasWidth * 0.5f, canvasHeight * 0.08f)
                val antennaBottom = Offset(canvasWidth * 0.5f, canvasHeight * 0.24f)
                
                drawLine(
                    color = CyberCyan,
                    start = antennaBottom,
                    end = antennaTop,
                    strokeWidth = 3.5f,
                    cap = StrokeCap.Round
                )
                
                // Antenna Beacon Orb
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color.White, CyberCyanGlow, ElectricBlue)
                    ),
                    radius = canvasWidth * 0.07f,
                    center = antennaTop
                )

                // 2. Robot Head Shield Path (Sleek Geometric Angular Polygon)
                val headPath = Path().apply {
                    moveTo(canvasWidth * 0.20f, canvasHeight * 0.28f) // Top Left
                    lineTo(canvasWidth * 0.80f, canvasHeight * 0.28f) // Top Right
                    lineTo(canvasWidth * 0.88f, canvasHeight * 0.62f) // Mid Right
                    lineTo(canvasWidth * 0.74f, canvasHeight * 0.88f) // Bottom Right
                    lineTo(canvasWidth * 0.26f, canvasHeight * 0.88f) // Bottom Left
                    lineTo(canvasWidth * 0.12f, canvasHeight * 0.62f) // Mid Left
                    close()
                }

                // Fill Head Armor
                drawPath(
                    path = headPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1E2D44),
                            Color(0xFF121B2A)
                        )
                    )
                )

                // Stroke Head Armor
                drawPath(
                    path = headPath,
                    color = CyberCyan.copy(alpha = 0.65f),
                    style = Stroke(width = 2.5f)
                )

                // 3. Futuristic Robotic Visor (Glowing Cyan Bar)
                val visorLeft = canvasWidth * 0.24f
                val visorTop = canvasHeight * 0.42f
                val visorWidth = canvasWidth * 0.52f
                val visorHeight = canvasHeight * 0.18f

                drawRoundRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            ElectricBlue,
                            CyberCyanGlow,
                            ElectricBlueLight
                        )
                    ),
                    topLeft = Offset(visorLeft, visorTop),
                    size = Size(visorWidth, visorHeight),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
                )

                // 4. Optical Eye Sensor Beacons (Dual Lenses inside Visor)
                val leftEyeCenter = Offset(canvasWidth * 0.38f, canvasHeight * 0.51f)
                val rightEyeCenter = Offset(canvasWidth * 0.62f, canvasHeight * 0.51f)
                val eyeRadius = canvasWidth * 0.045f

                // Left Eye
                drawCircle(
                    color = Color.White,
                    radius = eyeRadius,
                    center = leftEyeCenter
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.4f),
                    radius = eyeRadius * 1.6f,
                    center = leftEyeCenter
                )

                // Right Eye
                drawCircle(
                    color = Color.White,
                    radius = eyeRadius,
                    center = rightEyeCenter
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.4f),
                    radius = eyeRadius * 1.6f,
                    center = rightEyeCenter
                )

                // 5. Circuit Grid Mouth / Ventilation Slots
                val slotY = canvasHeight * 0.73f
                val slotSpacing = canvasWidth * 0.08f
                for (i in -2..2) {
                    val slotX = (canvasWidth * 0.5f) + (i * slotSpacing)
                    drawLine(
                        color = CyberCyan.copy(alpha = 0.7f),
                        start = Offset(slotX, slotY),
                        end = Offset(slotX, slotY + canvasHeight * 0.07f),
                        strokeWidth = 3f,
                        cap = StrokeCap.Round
                    )
                }

                // 6. Cyber Ear / Audio Sensors
                drawLine(
                    color = CyberCyanGlow,
                    start = Offset(canvasWidth * 0.12f, canvasHeight * 0.45f),
                    end = Offset(canvasWidth * 0.12f, canvasHeight * 0.58f),
                    strokeWidth = 4f,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = CyberCyanGlow,
                    start = Offset(canvasWidth * 0.88f, canvasHeight * 0.45f),
                    end = Offset(canvasWidth * 0.88f, canvasHeight * 0.58f),
                    strokeWidth = 4f,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}
