package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
    primary = LavenderDarkPrimary,
    secondary = LavenderDarkSecondary,
    tertiary = LavenderSecondary,
    background = Color(0xFF121215),
    surface = Color(0xFF1E1E24),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
  )

private val LightColorScheme =
  lightColorScheme(
    primary = LavenderPrimary,
    secondary = LavenderSecondary,
    tertiary = LavenderTertiary,
    background = BackgroundWhite,
    surface = BackgroundWhite,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = TextPrimary,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
  )

@Composable
fun AuraTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Disable dynamic color to enforce our beautiful Bold Lavender brand theme strictly!
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

// Fallback alias for the default template Theme
@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  AuraTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}
