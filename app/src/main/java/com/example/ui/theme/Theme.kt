package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = CyanPrimary,
    onPrimary = Color(0xFF00363A),
    primaryContainer = Color(0xFF004F55),
    onPrimaryContainer = Color(0xFF80F7FF),

    secondary = MintSecondary,
    onSecondary = Color(0xFF003822),
    secondaryContainer = Color(0xFF005234),
    onSecondaryContainer = Color(0xFF70FFBE),

    tertiary = AmberAccent,
    onTertiary = Color(0xFF452B00),
    tertiaryContainer = Color(0xFF634000),
    onTertiaryContainer = Color(0xFFFFDF9E),

    error = CoralError,
    onError = Color(0xFF680016),
    errorContainer = Color(0xFF920023),
    onErrorContainer = Color(0xFFFFDAD9),

    background = TechBackground,
    onBackground = TextPrimary,
    surface = TechSurface,
    onSurface = TextPrimary,
    surfaceVariant = TechSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = TechOutline,
    outlineVariant = TechOutlineSubtle
)

private val LightColorScheme = DarkColorScheme // OmniSwarm is an immersive laboratory HUD

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep distinctive cybernetic branding
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
