package com.example.veritas.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = VeritasMaroon,
    secondary = VeritasGold,
    tertiary = VeritasIvory,
    background = VeritasDarkest,
    surface = VeritasDeepMaroon,
    onPrimary = VeritasWhite,
    onSecondary = VeritasDarkest,
    onBackground = VeritasIvory,
    onSurface = VeritasIvory
)

@Composable
fun VeritasTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}