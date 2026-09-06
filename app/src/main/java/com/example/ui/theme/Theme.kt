package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
    primary = GoldLight,
    onPrimary = Emerald900,
    primaryContainer = Emerald800,
    onPrimaryContainer = GoldShimmer,
    secondary = GoldMetallic,
    onSecondary = Emerald900,
    tertiary = Emerald500,
    background = Emerald900,
    onBackground = SandParchment,
    surface = Emerald800,
    onSurface = SandParchment,
    surfaceVariant = Emerald700,
    onSurfaceVariant = GoldShimmer,
    outline = Emerald600
  )

private val LightColorScheme =
  lightColorScheme(
    primary = Emerald700,
    onPrimary = Color.White,
    primaryContainer = Emerald800,
    onPrimaryContainer = GoldShimmer,
    secondary = GoldMetallic,
    onSecondary = Color.White,
    secondaryContainer = GoldShimmer,
    onSecondaryContainer = Emerald900,
    tertiary = GoldDark,
    background = SandParchment,
    onBackground = NeutralDark,
    surface = SandSurface,
    onSurface = NeutralDark,
    surfaceVariant = SandParchment,
    onSurfaceVariant = NeutralMuted,
    outline = NeutralBorder
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Use Mahima Fashion's bespoke brand colors
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
