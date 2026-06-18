package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = DarkGeometricMainGreen,
    onPrimary = GeometricBg,
    primaryContainer = DarkGeometricContainerGreen,
    onPrimaryContainer = DarkGeometricText,
    secondary = DarkGeometricBeigeDark,
    background = DarkGeometricBg,
    surface = DarkGeometricBeigeLight,
    onBackground = DarkGeometricText,
    onSurface = DarkGeometricText,
    outline = DarkGeometricBorder
)

private val LightColorScheme = lightColorScheme(
    primary = GeometricMainGreen,
    onPrimary = GeometricBg,
    primaryContainer = GeometricContainerGreen,
    onPrimaryContainer = GeometricText,
    secondary = GeometricBeigeDark,
    background = GeometricBg,
    surface = GeometricBeigeLight,
    onBackground = GeometricText,
    onSurface = GeometricText,
    outline = GeometricBorder
)

@Composable
fun MyApplicationTheme(
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
