package com.kamildex.kbinchecker.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

private val LightColors = lightColorScheme(
    primary = Color(0xFF5B45E0),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFECE8FF),
    onPrimaryContainer = Color(0xFF2A1A7A),
    secondary = Color(0xFF5E5A78),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFE9E6F6),
    onSecondaryContainer = Color(0xFF1C1930),
    tertiary = Color(0xFF12A150),
    onTertiary = Color(0xFFFFFFFF),
    background = Color(0xFFF8F7FD),
    onBackground = Color(0xFF15131F),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF15131F),
    surfaceVariant = Color(0xFFF3F1FA),
    onSurfaceVariant = Color(0xFF6A677C),
    outline = Color(0xFFCFCBE0),
    outlineVariant = Color(0xFFE8E5F3),
    error = Color(0xFFD93025),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFDECEA),
    onErrorContainer = Color(0xFF8C1D18),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFFBFAFE),
    surfaceContainer = Color(0xFFF5F3FB),
    surfaceContainerHigh = Color(0xFFF0EEF8),
    surfaceContainerHighest = Color(0xFFEAE7F4)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFA99BFF),
    onPrimary = Color(0xFF1B1050),
    primaryContainer = Color(0xFF2C2560),
    onPrimaryContainer = Color(0xFFE4DEFF),
    secondary = Color(0xFFC6C2DC),
    onSecondary = Color(0xFF2E2B42),
    secondaryContainer = Color(0xFF2A2838),
    onSecondaryContainer = Color(0xFFE4E1F2),
    tertiary = Color(0xFF4ADE80),
    onTertiary = Color(0xFF00391A),
    background = Color(0xFF100F17),
    onBackground = Color(0xFFECEAF4),
    surface = Color(0xFF191822),
    onSurface = Color(0xFFECEAF4),
    surfaceVariant = Color(0xFF22202E),
    onSurfaceVariant = Color(0xFFA29FB5),
    outline = Color(0xFF3A3750),
    outlineVariant = Color(0xFF2A2838),
    error = Color(0xFFFF8A80),
    onError = Color(0xFF5F1411),
    errorContainer = Color(0xFF4A1512),
    onErrorContainer = Color(0xFFFFDAD6),
    surfaceContainerLowest = Color(0xFF0C0B12),
    surfaceContainerLow = Color(0xFF15141D),
    surfaceContainer = Color(0xFF191822),
    surfaceContainerHigh = Color(0xFF22202E),
    surfaceContainerHighest = Color(0xFF2C2A3A)
)

/** Positive status colours, not part of the Material colour roles. */
val ColorScheme.success: Color
    get() = if (background.luminance() < 0.5f) Color(0xFF4ADE80) else Color(0xFF12A150)

val ColorScheme.successContainer: Color
    get() = if (background.luminance() < 0.5f) Color(0xFF123B24) else Color(0xFFE3F6EA)

@Composable
fun KBinTheme(dark: Boolean, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (dark) DarkColors else LightColors,
        content = content
    )
}
