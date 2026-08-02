package com.fitbyai.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Minimalist Neutral Palette
val Slate950 = Color(0xFF020617)
val Slate900 = Color(0xFF0F172A)
val Slate800 = Color(0xFF1E293B)
val Slate700 = Color(0xFF334155)
val Slate600 = Color(0xFF475569)
val Slate500 = Color(0xFF64748B)
val Slate400 = Color(0xFF94A3B8)
val Slate300 = Color(0xFFCBD5E1)
val Slate200 = Color(0xFFE2E8F0)
val Slate100 = Color(0xFFF1F5F9)
val Slate50 = Color(0xFFF8FAFC)

private val DarkColorScheme = darkColorScheme(
    primary = Slate100, // Soft white primary
    onPrimary = Slate900,
    primaryContainer = Slate700,
    onPrimaryContainer = Slate100,
    secondary = Slate400,
    background = Slate900, // Lighter background (Soft Charcoal)
    onBackground = Slate100,
    surface = Slate800, // Lighter surface
    onSurface = Slate100,
    surfaceVariant = Slate700,
    onSurfaceVariant = Slate300,
    outline = Slate600,
    outlineVariant = Slate700,
    error = Color(0xFFFDA4AF) // Very soft rose
)

private val LightColorScheme = lightColorScheme(
    primary = Slate900, // Dark slate primary
    onPrimary = Color.White,
    primaryContainer = Slate100,
    onPrimaryContainer = Slate900,
    secondary = Slate500,
    background = Slate50,
    onBackground = Slate900,
    surface = Color.White,
    onSurface = Slate900,
    surfaceVariant = Slate100,
    onSurfaceVariant = Slate600,
    outline = Slate300,
    outlineVariant = Slate200,
    error = Color(0xFFE11D48)
)

@Composable
fun FitByAiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
