package pairro.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pairro.ui.theme.PairroColors

enum class StatusTone { Positive, Neutral, Muted }

@Composable
fun StatusPill(
  text: String,
  tone: StatusTone,
  modifier: Modifier = Modifier
) {
  val background: Color
  val textColor: Color

  when (tone) {
    StatusTone.Positive -> {
      background = PairroColors.SuccessSurface
      textColor = PairroColors.TealDeep
    }

    StatusTone.Neutral -> {
      background = PairroColors.MagentaSurface
      textColor = PairroColors.Magenta
    }

    StatusTone.Muted -> {
      background = PairroColors.MutedSurface
      textColor = PairroColors.TextMuted
    }
  }

  Text(
    text = text,
    color = textColor,
    fontSize = 10.5.sp,
    modifier = modifier
      .background(background, RoundedCornerShape(999.dp))
      .padding(horizontal = 8.dp, vertical = 3.dp)
  )
}