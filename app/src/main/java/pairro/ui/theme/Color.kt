package pairro.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object PairroColors {
  val WineDeep = Color(0xFF4E1238)
  val Wine = Color(0xFF7A1E52)
  val Magenta = Color(0xFFC2185B)
  val MagentaBright = Color(0xFFE91E63)
  val MagentaHover = Color(0xFFAD1457)
  val Pink = Color(0xFFF06292)
  val PinkSoft = Color(0xFFFFD6E5)
  val Blush = Color(0xFFFFF0F5)
  val BlushDeep = Color(0xFFF8D5E3)
  val Teal = Color(0xFF2EC4B6)
  val TealDeep = Color(0xFF1AA89C)
  val TealSoft = Color(0xFFD4F6F2)
  val Card = Color(0xFFFFFFFF)

  val BgRootStart = Wine
  val BgRootEnd = Blush
  val BgHeroStart = WineDeep
  val BgHeroEnd = Magenta
  val BgSurfaceLow = Color(0xF2FFFFFF)
  val BgSurfaceMid = Card
  val BgSurfaceHigh = Color(0xFFFFF7FA)
  val BgSurfaceHover = PinkSoft

  val BorderSubtle = Color(0x40C2185B)
  val BorderStrong = Color(0xFFEEB8CC)
  val BorderFocus = MagentaBright

  val AccentPrimary = Magenta
  val AccentPrimaryHover = MagentaHover
  val AccentSecondary = Teal
  val AccentTertiary = Pink
  val AccentSubtle = Color(0x26C2185B)

  val Danger = Color(0xFFE11D48)
  val DangerHover = Color(0xFFBE123C)
  val Success = TealDeep

  val TextPrimary = Color(0xFF3A1530)
  val TextSecondary = Color(0xFF8D5A74)
  val TextMuted = Color(0xFFB08A9C)
  val TextOnAccent = Color(0xFFFFFFFF)
  val TextOnHero = Color(0xFFFFFFFF)

  val SuccessSurface = Color(0x332EC4B6)
  val MutedSurface = Color(0x26B08A9C)
  val MagentaSurface = Color(0x33C2185B)

  val CaptionBar = Color(0xFFF8E3EB)
  val AvatarTile = Color(0x1FC2185B)
  val AvatarTileMuted = Color(0x2EB08A9C)
  val DangerSurface = Color(0x14E11D48)
  val DangerBorder = Color(0x40E11D48)
  val DangerConfirmSurface = Color(0x0FE11D48)
  val DangerConfirmBorder = Color(0x73E11D48)
}

object PairroBrushes {
  val Hero = Brush.verticalGradient(
    colors = listOf(PairroColors.WineDeep, PairroColors.Wine, PairroColors.Magenta)
  )
  val Screen = Brush.verticalGradient(
    colors = listOf(PairroColors.Wine, PairroColors.Magenta, PairroColors.Blush)
  )
  val Control = Brush.linearGradient(
    colors = listOf(
      PairroColors.BlushDeep,
      PairroColors.Blush,
      PairroColors.TealSoft.copy(alpha = 0.55f)
    )
  )
  val MagentaTeal = Brush.horizontalGradient(
    colors = listOf(PairroColors.Magenta, PairroColors.Pink, PairroColors.Teal)
  )
  val Avatar = Brush.linearGradient(
    colors = listOf(PairroColors.Magenta, PairroColors.Pink)
  )
}
