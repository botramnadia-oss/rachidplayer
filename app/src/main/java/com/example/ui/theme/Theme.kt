package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val KineticObsidianColorScheme = darkColorScheme(
  primary = Primary,
  onPrimary = OnPrimary,
  primaryContainer = PrimaryContainer,
  onPrimaryContainer = OnPrimaryContainer,
  inversePrimary = InversePrimary,
  secondary = Secondary,
  onSecondary = OnSecondary,
  secondaryContainer = SecondaryContainer,
  onSecondaryContainer = OnSecondaryContainer,
  tertiary = Tertiary,
  onTertiary = OnTertiary,
  tertiaryContainer = TertiaryContainer,
  onTertiaryContainer = OnTertiaryContainer,
  background = Background,
  onBackground = OnBackground,
  surface = Surface,
  onSurface = OnSurface,
  surfaceVariant = SurfaceVariant,
  onSurfaceVariant = OnSurfaceVariant,
  surfaceContainerLowest = SurfaceContainerLowest,
  surfaceContainerLow = SurfaceContainerLow,
  surfaceContainer = SurfaceContainer,
  surfaceContainerHigh = SurfaceContainerHigh,
  surfaceContainerHighest = SurfaceContainerHighest,
  surfaceDim = SurfaceDim,
  surfaceBright = SurfaceBright,
  inverseSurface = InverseSurface,
  inverseOnSurface = InverseOnSurface,
  outline = Outline,
  outlineVariant = OutlineVariant,
  error = Error,
  onError = OnError,
  errorContainer = ErrorContainer,
  onErrorContainer = OnErrorContainer,
)

@Composable
fun RachidplayerTheme(
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = KineticObsidianColorScheme,
    typography = Typography,
    content = content
  )
}

