package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PromptXoPrimary,
    onPrimary = Color.White,
    primaryContainer = PromptXoPrimaryVariant,
    onPrimaryContainer = Color.White,
    secondary = PromptXoSecondary,
    onSecondary = Color.White,
    tertiary = PromptXoAccent,
    background = PromptXoDarkBg,
    onBackground = PromptXoTextPrimary,
    surface = PromptXoSurface,
    onSurface = PromptXoTextPrimary,
    surfaceVariant = PromptXoSurfaceVariant,
    onSurfaceVariant = PromptXoTextSecondary,
    outline = PromptXoBorder,
    error = PromptXoHeartRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // PromptXo is designed with an immersive dark aesthetic
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
