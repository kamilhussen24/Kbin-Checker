package com.kamildex.kbinchecker.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

/** KDex brand colours: orange to pink gradient on neutral light or near-black surfaces. */
val BrandOrange = Color(0xFFFF6A35)
val BrandPink = Color(0xFFF04478)

private val LightColors = lightColorScheme(
    // A slightly deeper brand pink keeps small coloured text readable on white.
    primary = Color(0xFFD4285F),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFE9EE),
    onPrimaryContainer = Color(0xFF8A1238),
    secondary = Color(0xFFFF6A35),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFEDE5),
    onSecondaryContainer = Color(0xFF7A2A0C),
    tertiary = Color(0xFF12A150),
    onTertiary = Color(0xFFFFFFFF),
    background = Color(0xFFFBF9F8),
    onBackground = Color(0xFF141214),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF141214),
    surfaceVariant = Color(0xFFF4F2F1),
    onSurfaceVariant = Color(0xFF6B6669),
    outline = Color(0xFFD6D2D2),
    outlineVariant = Color(0xFFECE8E8),
    error = Color(0xFFD93025),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFDECEA),
    onErrorContainer = Color(0xFF8C1D18),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFFDFCFB),
    surfaceContainer = Color(0xFFF7F5F4),
    surfaceContainerHigh = Color(0xFFF2F0EF),
    surfaceContainerHighest = Color(0xFFECE9E8)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFF04478),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF3A1622),
    onPrimaryContainer = Color(0xFFFFD9E2),
    secondary = Color(0xFFFF6A35),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFF3A1E12),
    onSecondaryContainer = Color(0xFFFFDCCD),
    tertiary = Color(0xFF4ADE80),
    onTertiary = Color(0xFF00391A),
    background = Color(0xFF0B0B0D),
    onBackground = Color(0xFFF4F4F5),
    surface = Color(0xFF151518),
    onSurface = Color(0xFFF4F4F5),
    surfaceVariant = Color(0xFF1E1E22),
    onSurfaceVariant = Color(0xFFA1A1AA),
    outline = Color(0xFF3A3A40),
    outlineVariant = Color(0xFF26262B),
    error = Color(0xFFFF8A80),
    onError = Color(0xFF5F1411),
    errorContainer = Color(0xFF4A1512),
    onErrorContainer = Color(0xFFFFDAD6),
    surfaceContainerLowest = Color(0xFF070708),
    surfaceContainerLow = Color(0xFF111113),
    surfaceContainer = Color(0xFF151518),
    surfaceContainerHigh = Color(0xFF1E1E22),
    surfaceContainerHighest = Color(0xFF28282D)
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
