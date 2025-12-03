package com.example.vibedo.view.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = silver,
    secondary = aqua,
    tertiary = dustyRose,
    background = black,
    surface = Color(0xFF1E1E1E),
    surfaceVariant = Color(0xFF2D2D2D),
    onPrimary = black,
    onSecondary = black,
    onTertiary = black,
    onBackground = white,
    onSurface = white,
    onSurfaceVariant = Color(0xFFB0B0B0)
)

private val LightColorScheme = lightColorScheme(
    primary = silverDark,
    secondary = aquaDark,
    tertiary = dustyRoseDark,
    background = whiteMilk,
    surface = Color.White,
    surfaceVariant = Color(0xFFF0F0F0),
    onPrimary = white,
    onSecondary = black,
    onTertiary = black,
    onBackground = black,
    onSurface = black,
    onSurfaceVariant = grayText
)

@Composable
fun VibeDoTheme(
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