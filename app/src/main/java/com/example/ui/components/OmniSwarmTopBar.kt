package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DisplayMode
import com.example.model.OperatingDivision
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.MintSecondary
import com.example.ui.theme.TechBackground
import com.example.ui.theme.TechOutline
import com.example.ui.theme.TechSurface
import com.example.ui.theme.TechSurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun OmniSwarmTopBar(
    displayMode: DisplayMode,
    isFullscreen: Boolean,
    activeDivision: OperatingDivision,
    feedbackCount: Int,
    onToggleDisplayMode: () -> Unit,
    onToggleFullscreen: () -> Unit,
    onOpenFeedback: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = TechSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, TechOutline.copy(alpha = 0.5f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("omniswarm_top_bar")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left branding & division pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(CyanPrimary, MintSecondary)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Ω",
                        color = Color(0xFF00383D),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "OMNISWARM",
                            color = TextPrimary,
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            letterSpacing = 1.2.sp
                        )

                        // Mode indicator badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    if (displayMode == DisplayMode.DESKTOP)
                                        CyanPrimary.copy(alpha = 0.15f)
                                    else
                                        MintSecondary.copy(alpha = 0.15f)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (displayMode == DisplayMode.DESKTOP) CyanPrimary else MintSecondary,
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = if (displayMode == DisplayMode.DESKTOP) "DESKTOP" else "MOBILE",
                                color = if (displayMode == DisplayMode.DESKTOP) CyanPrimary else MintSecondary,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    Text(
                        text = activeDivision.title,
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Right Quick Actions
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Desktop / Mobile Mode Switcher
                IconButton(
                    onClick = onToggleDisplayMode,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(TechSurfaceElevated)
                        .testTag("toggle_desktop_mode_button")
                ) {
                    AnimatedContent(
                        targetState = displayMode,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "displayModeAnimation"
                    ) { mode ->
                        if (mode == DisplayMode.DESKTOP) {
                            Icon(
                                imageVector = Icons.Default.Computer,
                                contentDescription = "Switch to Mobile Mode",
                                tint = CyanPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.PhoneAndroid,
                                contentDescription = "Switch to Desktop Mode",
                                tint = TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Full Screen Toggle Button
                IconButton(
                    onClick = onToggleFullscreen,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            if (isFullscreen) CyanPrimary.copy(alpha = 0.2f) else TechSurfaceElevated
                        )
                        .testTag("toggle_fullscreen_button")
                ) {
                    Icon(
                        imageVector = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                        contentDescription = if (isFullscreen) "Exit Full Screen" else "Enter Full Screen",
                        tint = if (isFullscreen) CyanPrimary else TextSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Feedback Button with badge if any submitted
                IconButton(
                    onClick = onOpenFeedback,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(TechSurfaceElevated)
                        .testTag("open_feedback_button")
                ) {
                    if (feedbackCount > 0) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = MintSecondary,
                                    contentColor = Color(0xFF00383D)
                                ) {
                                    Text("$feedbackCount", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChatBubbleOutline,
                                contentDescription = "Send Feedback",
                                tint = TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Default.ChatBubbleOutline,
                            contentDescription = "Send Feedback",
                            tint = TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Settings Button
                IconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(TechSurfaceElevated)
                        .testTag("open_settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Open Settings",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
