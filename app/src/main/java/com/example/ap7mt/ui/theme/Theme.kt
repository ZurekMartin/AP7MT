package com.example.ap7mt.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

private val DarkColorScheme = darkColorScheme(
    primary = GamingPrimary,
    onPrimary = GamingOnPrimary,
    primaryContainer = GamingPrimary.copy(alpha = 0.1f),
    onPrimaryContainer = GamingPrimary,
    secondary = GamingSecondary,
    onSecondary = GamingOnSecondary,
    secondaryContainer = GamingSecondary.copy(alpha = 0.1f),
    onSecondaryContainer = GamingSecondary,
    tertiary = GamingTertiary,
    onTertiary = GamingOnPrimary,
    tertiaryContainer = GamingTertiary.copy(alpha = 0.1f),
    onTertiaryContainer = GamingTertiary,
    error = GamingError,
    onError = GamingOnPrimary,
    errorContainer = GamingError.copy(alpha = 0.1f),
    onErrorContainer = GamingError,
    background = GamingBackground,
    onBackground = GamingOnSurface,
    surface = GamingSurface,
    onSurface = GamingOnSurface,
    surfaceVariant = GamingSurface.copy(alpha = 0.8f),
    onSurfaceVariant = GamingOnSurface.copy(alpha = 0.8f),
    surfaceTint = GamingPrimary,
    inverseSurface = GamingOnSurface,
    inverseOnSurface = GamingSurface,
    outline = GamingOnSurface.copy(alpha = 0.3f),
    outlineVariant = GamingOnSurface.copy(alpha = 0.2f),
    scrim = GamingSurface.copy(alpha = 0.5f)
)

private val LightColorScheme = lightColorScheme(
    primary = GamingPrimaryLight,
    onPrimary = GamingOnPrimary,
    primaryContainer = GamingPrimaryLight.copy(alpha = 0.1f),
    onPrimaryContainer = GamingPrimaryLight,
    secondary = GamingSecondaryLight,
    onSecondary = GamingOnSecondary,
    secondaryContainer = GamingSecondaryLight.copy(alpha = 0.1f),
    onSecondaryContainer = GamingSecondaryLight,
    tertiary = GamingTertiaryLight,
    onTertiary = GamingOnPrimary,
    tertiaryContainer = GamingTertiaryLight.copy(alpha = 0.1f),
    onTertiaryContainer = GamingTertiaryLight,
    error = GamingError,
    onError = GamingOnPrimary,
    errorContainer = GamingError.copy(alpha = 0.1f),
    onErrorContainer = GamingError,
    background = GamingOnSurface,
    onBackground = GamingSurface,
    surface = GamingOnPrimary,
    onSurface = GamingSurface,
    surfaceVariant = GamingOnPrimary.copy(alpha = 0.9f),
    onSurfaceVariant = GamingSurface.copy(alpha = 0.8f),
    surfaceTint = GamingPrimaryLight,
    inverseSurface = GamingSurface,
    inverseOnSurface = GamingOnSurface,
    outline = GamingSurface.copy(alpha = 0.3f),
    outlineVariant = GamingSurface.copy(alpha = 0.2f),
    scrim = GamingSurface.copy(alpha = 0.5f)
)

val GamingShapes = Shapes(
    extraSmall = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
    small = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
    medium = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
    large = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
    extraLarge = androidx.compose.foundation.shape.RoundedCornerShape(28.dp)
)

@Composable
fun GameDatabaseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = GamingShapes,
        content = content
    )
}
