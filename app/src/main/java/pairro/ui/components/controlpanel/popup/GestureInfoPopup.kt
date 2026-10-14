package pairro.ui.components.controlpanel.popup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import pairro.ui.theme.PairroColors

@Composable
fun GesturesInfoPopup(
  modifier: Modifier = Modifier
) {
  Column(
    verticalArrangement = Arrangement.spacedBy(6.dp),
    modifier = modifier
      .widthIn(min = 200.dp)
      .clip(RoundedCornerShape(16.dp))
      .background(PairroColors.Card.copy(alpha = 0.97f))
      .border(1.dp, PairroColors.BorderSubtle, RoundedCornerShape(16.dp))
      .padding(12.dp)
  ) {
    Text(
      text = "Available Gestures",
      style = MaterialTheme.typography.titleSmall,
      color = PairroColors.Magenta
    )
    Text(
      text = "1 Finger Drag: Move cursor",
      style = MaterialTheme.typography.bodySmall,
      color = PairroColors.TextSecondary
    )
    Text(
      text = "1 Finger Tap: Left click",
      style = MaterialTheme.typography.bodySmall,
      color = PairroColors.TextSecondary
    )
    Text(
      text = "1 Finger Tap and Hold: Dragging",
      style = MaterialTheme.typography.bodySmall,
      color = PairroColors.TextSecondary
    )
    Text(
      text = "2 Finger Tap: Right click",
      style = MaterialTheme.typography.bodySmall,
      color = PairroColors.TextSecondary
    )
  }
}