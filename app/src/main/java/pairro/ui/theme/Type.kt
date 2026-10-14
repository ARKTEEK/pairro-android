package pairro.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.pairro.R

val PlusJakartaSans = FontFamily(
  Font(R.font.plus_jakarta_sans_semibold, FontWeight.SemiBold),
  Font(R.font.plus_jakarta_sans_bold, FontWeight.Bold)
)

val Inter = FontFamily(
  Font(R.font.inter_regular, FontWeight.Normal),
  Font(R.font.inter_medium, FontWeight.Medium)
)

val JetBrainsMono = FontFamily(
  Font(R.font.jetbrains_mono_regular, FontWeight.Normal)
)

val MonoLabelStyle = TextStyle(
  fontFamily = JetBrainsMono,
  fontSize = 12.sp,
  color = PairroColors.TextPrimary
)

val Typography = Typography(
  titleLarge = TextStyle(
    fontFamily = PlusJakartaSans,
    fontWeight = FontWeight.Bold,
    fontSize = 16.sp,
    color = PairroColors.TextPrimary
  ),
  titleMedium = TextStyle(
    fontFamily = PlusJakartaSans,
    fontWeight = FontWeight.Bold,
    fontSize = 13.5.sp,
    color = PairroColors.TextPrimary
  ),
  titleSmall = TextStyle(
    fontFamily = PlusJakartaSans,
    fontWeight = FontWeight.Bold,
    fontSize = 10.5.sp,
    color = PairroColors.Magenta
  ),
  bodyLarge = TextStyle(
    fontFamily = Inter,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    color = PairroColors.TextPrimary
  ),
  bodyMedium = TextStyle(
    fontFamily = Inter,
    fontWeight = FontWeight.Normal,
    fontSize = 13.sp,
    color = PairroColors.TextPrimary
  ),
  bodySmall = TextStyle(
    fontFamily = Inter,
    fontWeight = FontWeight.Medium,
    fontSize = 12.sp,
    color = PairroColors.TextSecondary
  ),
  labelSmall = TextStyle(
    fontFamily = Inter,
    fontWeight = FontWeight.Medium,
    fontSize = 10.5.sp,
    color = PairroColors.TextMuted
  )
)