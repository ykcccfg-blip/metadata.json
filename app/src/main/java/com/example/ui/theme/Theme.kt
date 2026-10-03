package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = IndigoPrimary,
    onPrimary = Color.White,
    primaryContainer = Slate800,
    onPrimaryContainer = IndigoLight,
    secondary = EmeraldSecondary,
    onSecondary = Color.White,
    secondaryContainer = Slate800,
    onSecondaryContainer = EmeraldLight,
    tertiary = CyanAccent,
    onTertiary = Color.Black,
    background = Slate900,
    onBackground = Slate100,
    surface = Slate800,
    onSurface = Slate100,
    surfaceVariant = Slate700,
    onSurfaceVariant = Slate300,
    outline = Slate700,
    outlineVariant = Slate600,
    error = RoseDanger,
    onError = Color.White
)

private val LightColorScheme = DarkColorScheme // Default to sleek Slate-900 Dark theme requested by user

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Honor dark mode as primary
    dynamicColor: Boolean = false, // Keep custom brand palette
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
