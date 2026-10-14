package pairro.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pairro.ui.theme.JetBrainsMono
import pairro.ui.theme.PairroBrushes
import pairro.ui.theme.PairroColors
import pairro.ui.components.common.AccentSplitBar

private const val DOT_COUNT = 3
private const val DOT_PULSE_MS = 600
private const val DOT_DELAY_MS = 200
private const val DOT_MIN_VALUE = 0.35f

@Composable
fun PairingProgress(
  pin: String?,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxSize()
      .background(PairroBrushes.Hero)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .windowInsetsPadding(WindowInsets.systemBars)
        .padding(horizontal = 32.dp),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      if (pin != null) {
        Text(
          text = "Confirm pairing",
          style = MaterialTheme.typography.titleLarge,
          color = PairroColors.TextOnHero
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = "Check that the code below matches the code on your desktop, then confirm on your desktop.",
          style = MaterialTheme.typography.bodyMedium,
          color = PairroColors.PinkSoft,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(PairroColors.Card)
            .padding(horizontal = 22.dp, vertical = 14.dp)
            .width(IntrinsicSize.Min)
        ) {
          Text(
            text = pin,
            style = MaterialTheme.typography.titleLarge.copy(
              fontFamily = JetBrainsMono,
              fontSize = 28.sp
            ),
            color = PairroColors.Wine,
            maxLines = 1,
            softWrap = false
          )

          Spacer(modifier = Modifier.height(10.dp))

          AccentSplitBar(height = 6.dp)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
          text = "Waiting for you to confirm on your desktop\u2026",
          style = MaterialTheme.typography.bodySmall,
          color = PairroColors.PinkSoft,
          textAlign = TextAlign.Center
        )
      } else {
        Text(
          text = "Connecting to your desktop",
          style = MaterialTheme.typography.titleLarge,
          color = PairroColors.TextOnHero
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = "Keep your desktop on and Pairro open on it.",
          style = MaterialTheme.typography.bodyMedium,
          color = PairroColors.PinkSoft,
          textAlign = TextAlign.Center
        )
      }

      Spacer(modifier = Modifier.height(28.dp))

      PairingDots()
    }
  }
}

@Composable
private fun PairingDots(modifier: Modifier = Modifier) {
  val transition = rememberInfiniteTransition(label = "pairingDots")

  Row(
    horizontalArrangement = Arrangement.spacedBy(10.dp),
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
  ) {
    repeat(DOT_COUNT) { dotIndex ->
      val pulse by transition.animateFloat(
        initialValue = DOT_MIN_VALUE,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
          animation = tween(durationMillis = DOT_PULSE_MS, easing = LinearEasing),
          repeatMode = RepeatMode.Reverse,
          initialStartOffset = StartOffset(dotIndex * DOT_DELAY_MS)
        ),
        label = "pairingDot$dotIndex"
      )

      Box(
        modifier = Modifier
          .size(10.dp)
          .graphicsLayer {
            scaleX = pulse
            scaleY = pulse
            alpha = pulse
          }
          .clip(CircleShape)
          .background(PairroColors.AccentSecondary)
      )
    }
  }
}