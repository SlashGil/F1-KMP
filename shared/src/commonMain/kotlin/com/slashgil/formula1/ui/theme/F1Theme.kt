package com.slashgil.formula1.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val F1Red = Color(0xFFE10600)
val F1Dark = Color(0xFF0A0A0A)
val F1Surface = Color(0xFF1C1C1E)
val F1SurfaceVariant = Color(0xFF2C2C2E)
val F1TextPrimary = Color(0xFFFFFFFF)
val F1TextSecondary = Color(0xFFA1A1A6)

private val F1DarkColorScheme = darkColorScheme(
    primary = F1Red,
    onPrimary = Color.White,
    primaryContainer = F1Red,
    onPrimaryContainer = Color.White,
    background = F1Dark,
    onBackground = F1TextPrimary,
    surface = F1Surface,
    onSurface = F1TextPrimary,
    surfaceVariant = F1SurfaceVariant,
    onSurfaceVariant = F1TextSecondary
)

@Composable
fun F1Theme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = F1DarkColorScheme,
        content = content
    )
}