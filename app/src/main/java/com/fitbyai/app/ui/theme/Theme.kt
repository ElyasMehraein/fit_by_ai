package com.fitbyai.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Material 3 Expressive Palette (Emerald & Midnight Violet Accent)
val DarkBackground = Color(0xFF0D1117)
val DarkSurface = Color(0xFF161B22)
val DarkSurfaceContainerLow = Color(0xFF1C2128)
val DarkSurfaceContainer = Color(0xFF21262D)
val DarkSurfaceContainerHigh = Color(0xFF30363D)
val DarkSurfaceContainerHighest = Color(0xFF3B434E)

val DarkPrimary = Color(0xFF8B5CF6) // Expressive Electric Violet
val DarkOnPrimary = Color(0xFF1E1035)
val DarkPrimaryContainer = Color(0xFF3B2D54)
val DarkOnPrimaryContainer = Color(0xFFEDE9FE)

val DarkSecondary = Color(0xFF10B981) // Emerald Green Accent
val DarkOnSecondary = Color(0xFF022C22)
val DarkSecondaryContainer = Color(0xFF064E3B)
val DarkOnSecondaryContainer = Color(0xFFD1FAE5)

val DarkTertiary = Color(0xFFF43F5E) // Radiant Rose/Coral Accent
val DarkOnTertiary = Color(0xFF4C0519)
val DarkTertiaryContainer = Color(0xFF881337)
val DarkOnTertiaryContainer = Color(0xFFFFE4E6)

val DarkOutline = Color(0xFF48515E)
val DarkOutlineVariant = Color(0xFF30363D)

// Light Palette
val LightBackground = Color(0xFFF8FAFC)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceContainerLow = Color(0xFFF1F5F9)
val LightSurfaceContainer = Color(0xFFE2E8F0)
val LightSurfaceContainerHigh = Color(0xFFCBD5E1)
val LightSurfaceContainerHighest = Color(0xFF94A3B8)

val LightPrimary = Color(0xFF7C3AED)
val LightOnPrimary = Color(0xFFFFFFFF)
val LightPrimaryContainer = Color(0xFFEDE9FE)
val LightOnPrimaryContainer = Color(0xFF2E1065)

val LightSecondary = Color(0xFF059669)
val LightOnSecondary = Color(0xFFFFFFFF)
val LightSecondaryContainer = Color(0xFFD1FAE5)
val LightOnSecondaryContainer = Color(0xFF064E3B)

val LightTertiary = Color(0xFFE11D48)
val LightOnTertiary = Color(0xFFFFFFFF)
val LightTertiaryContainer = Color(0xFFFFE4E6)
val LightOnTertiaryContainer = Color(0xFF881337)

val LightOutline = Color(0xFFCBD5E1)
val LightOutlineVariant = Color(0xFFE2E8F0)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,
    tertiary = DarkTertiary,
    onTertiary = DarkOnTertiary,
    tertiaryContainer = DarkTertiaryContainer,
    onTertiaryContainer = DarkOnTertiaryContainer,
    background = DarkBackground,
    onBackground = Color(0xFFF0F6FC),
    surface = DarkSurface,
    onSurface = Color(0xFFF0F6FC),
    surfaceVariant = DarkSurfaceContainerHigh,
    onSurfaceVariant = Color(0xFF8B949E),
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant,
    error = Color(0xFFFB7185)
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimaryContainer,
    secondary = LightSecondary,
    onSecondary = LightOnSecondary,
    secondaryContainer = LightSecondaryContainer,
    onSecondaryContainer = LightOnSecondaryContainer,
    tertiary = LightTertiary,
    onTertiary = LightOnTertiary,
    tertiaryContainer = LightTertiaryContainer,
    onTertiaryContainer = LightOnTertiaryContainer,
    background = LightBackground,
    onBackground = Color(0xFF0F172A),
    surface = LightSurface,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = LightSurfaceContainerLow,
    onSurfaceVariant = Color(0xFF475569),
    outline = LightOutline,
    outlineVariant = LightOutlineVariant,
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
