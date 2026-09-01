package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = ForestGreen80,
    secondary = CardGold80,
    tertiary = Crimson80,
    background = DarkGreenSurface,
    surface = DarkGreenCard,
    surfaceVariant = Color(0xFF22442B),
    onPrimary = Color(0xFF003310),
    onSecondary = Color(0xFF3F2E00),
    onBackground = Color(0xFFE2ECE2),
    onSurface = Color(0xFFE2ECE2),
  )

private val LightColorScheme =
  lightColorScheme(
    primary = ForestGreenPrimary,
    secondary = CardGoldSecondary,
    tertiary = Crimson40,
    background = LightSurface,
    surface = LightCard,
    surfaceVariant = Color(0xFFE8EFE8),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF192019),
    onSurface = Color(0xFF192019),
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
