package com.example.codecraft.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Accent,
    background = BgDark,
    surface = Surface,
    onPrimary = BgDark,
    onBackground = TextPrim,
    onSurface = TextPrim,
    secondary = TextSecond,
    outline = CardBorder
)

@Composable
fun CodeCraftTheme(
    darkTheme: Boolean = true, // Force dark theme as per IDE/Terminal style
    dynamicColor: Boolean = false, // Disable dynamic colors to keep brand identity
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}