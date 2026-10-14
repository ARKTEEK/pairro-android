package pairro.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val PairroLightColorScheme = lightColorScheme(
  primary = PairroColors.AccentPrimary,
  onPrimary = PairroColors.TextOnAccent,
  primaryContainer = PairroColors.AccentSubtle,
  onPrimaryContainer = PairroColors.AccentPrimary,
  secondary = PairroColors.AccentSecondary,
  onSecondary = PairroColors.TextOnAccent,
  background = PairroColors.BgRootEnd,
  onBackground = PairroColors.TextPrimary,
  surface = PairroColors.BgSurfaceHigh,
  onSurface = PairroColors.TextPrimary,
  surfaceVariant = PairroColors.BgSurfaceMid,
  onSurfaceVariant = PairroColors.TextSecondary,
  outline = PairroColors.BorderSubtle,
  outlineVariant = PairroColors.BorderSubtle,
  error = PairroColors.Danger,
  onError = PairroColors.TextOnAccent
)

@Composable
fun PairroTheme(content: @Composable () -> Unit) {
  MaterialTheme(
    colorScheme = PairroLightColorScheme,
    typography = Typography,
    content = content
  )
}