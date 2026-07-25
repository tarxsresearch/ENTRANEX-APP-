package com.tarxs.entranex.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val EntranexDarkColors = darkColorScheme(
    background = BgBase,
    surface = GlassBg,
    primary = AccentCyan,
    secondary = AccentOrange,
    error = DangerRed,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onPrimary = BgBase,
    onSecondary = BgBase
)

@Composable
fun EntranexTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = EntranexDarkColors,
        typography = MaterialTheme.typography,
        content = content
    )
}
