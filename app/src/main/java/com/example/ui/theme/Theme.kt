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
    primary = AmberPrimaryDark,
    onPrimary = Color(0xFF0F2006),
    primaryContainer = NaturalSagePrimary,
    onPrimaryContainer = Color(0xFFB8F397),
    secondary = Color(0xFFC6B9A8),
    onSecondary = Color(0xFF2E241A),
    secondaryContainer = Color(0xFF453A2E),
    onSecondaryContainer = Color(0xFFE4D6C4),
    tertiary = Color(0xFFD3C8BA),
    background = BackgroundDark,
    surface = SurfaceDark,
    surfaceVariant = SurfaceContainerDark,
    onBackground = Color(0xFFEDE0D4),
    onSurface = Color(0xFFEDE0D4),
    onSurfaceVariant = Color(0xFFC6B9A8),
  )

private val LightColorScheme =
  lightColorScheme(
    primary = NaturalSagePrimary,
    onPrimary = Color.White,
    primaryContainer = NaturalSageContainer,
    onPrimaryContainer = Color(0xFF14270B),
    secondary = TextMutedEarth,
    onSecondary = Color.White,
    secondaryContainer = NaturalSandstone,
    onSecondaryContainer = TextDarkHeading,
    tertiary = TextDeepOchre,
    onTertiary = Color.White,
    background = BackgroundLight,
    surface = SurfaceLight,
    surfaceVariant = NaturalSandstone,
    onBackground = TextDarkBark,
    onSurface = TextDarkBark,
    onSurfaceVariant = TextMutedEarth,
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Keep warm branding vibrant by default
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

