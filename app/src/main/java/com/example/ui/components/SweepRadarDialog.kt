package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.MintSecondary
import com.example.ui.theme.TechOutline
import com.example.ui.theme.TechSurface
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.cos
import kotlin.math.sin

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Composable
fun SweepRadarDialog(
    statusText: String,
    onDismiss: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val infiniteTransition = rememberInfiniteTransition(label = "radar_anim")
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radar_sweep"
    )

    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radar_pulse"
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("sweep_radar_dialog"),
            shape = RoundedCornerShape(20.dp),
            color = TechSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, TechOutline)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Assembling Workforce",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Radar Canvas
                Box(
                    modifier = Modifier.size(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(150.dp)) {
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val maxRadius = size.width / 2f

                        // Concentric target rings
                        drawCircle(
                            color = TechOutline,
                            radius = maxRadius * 0.33f,
                            center = center,
                            style = Stroke(width = 1.dp.toPx())
                        )
                        drawCircle(
                            color = TechOutline,
                            radius = maxRadius * 0.66f,
                            center = center,
                            style = Stroke(width = 1.dp.toPx())
                        )
                        drawCircle(
                            color = CyanPrimary.copy(alpha = 0.3f),
                            radius = maxRadius,
                            center = center,
                            style = Stroke(width = 1.5.dp.toPx())
                        )

                        // Crosshairs
                        drawLine(
                            color = TechOutline,
                            start = Offset(center.x, 0f),
                            end = Offset(center.x, size.height),
                            strokeWidth = 1.dp.toPx()
                        )
                        drawLine(
                            color = TechOutline,
                            start = Offset(0f, center.y),
                            end = Offset(size.width, center.y),
                            strokeWidth = 1.dp.toPx()
                        )

                        // Pulsing expansion ring
                        drawCircle(
                            color = MintSecondary.copy(alpha = (1f - pulseRadius) * 0.4f),
                            radius = maxRadius * pulseRadius,
                            center = center,
                            style = Stroke(width = 2.dp.toPx())
                        )

                        // Rotating Sweep Beam
                        val angleRad = Math.toRadians(sweepAngle.toDouble())
                        val beamEnd = Offset(
                            (center.x + maxRadius * cos(angleRad)).toFloat(),
                            (center.y + maxRadius * sin(angleRad)).toFloat()
                        )
                        drawLine(
                            brush = Brush.linearGradient(
                                colors = listOf(CyanPrimary, Color.Transparent),
                                start = center,
                                end = beamEnd
                            ),
                            start = center,
                            end = beamEnd,
                            strokeWidth = 2.5.dp.toPx()
                        )

                        // Blip targets found
                        drawCircle(
                            color = MintSecondary,
                            radius = 3.5.dp.toPx(),
                            center = Offset(center.x + maxRadius * 0.4f, center.y - maxRadius * 0.3f)
                        )
                        drawCircle(
                            color = CyanPrimary,
                            radius = 4.dp.toPx(),
                            center = Offset(center.x - maxRadius * 0.5f, center.y + maxRadius * 0.4f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = statusText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Matching free web programs & APIs without subscriptions",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
