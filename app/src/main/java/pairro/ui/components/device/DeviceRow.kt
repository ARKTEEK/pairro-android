package pairro.ui.components.device

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import pairro.ui.theme.PairroColors
import pairro.device.DiscoveredDevice
import pairro.ui.components.common.StatusPill
import pairro.ui.components.common.StatusTone
import pairro.ui.components.common.dashedBorder

@Composable
fun DeviceRow(
  device: DiscoveredDevice,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val shape = RoundedCornerShape(16.dp)
  val isUnpaired = !device.isPaired

  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp)
      .clip(shape)
      .background(if (isUnpaired) PairroColors.Card.copy(alpha = 0.6f) else PairroColors.Card)
      .then(
        if (isUnpaired) {
          Modifier.dashedBorder(color = PairroColors.BorderStrong, cornerRadius = 16.dp)
        } else {
          Modifier
        }
      )
      .clickable(onClick = onClick)
      .padding(12.dp)
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      DeviceAvatar(
        kind = device.kind,
        showStatusDot = false,
        isOnline = device.isOnline,
        size = 40.dp,
        muted = isUnpaired
      )

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = device.name,
          style = MaterialTheme.typography.bodyMedium,
          color = PairroColors.TextPrimary
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row {
          StatusPill(
            text = if (device.isOnline) {
              "Online"
            } else {
              "Offline"
            },
            tone = if (device.isOnline) {
              StatusTone.Positive
            } else {
              StatusTone.Muted
            }
          )

          Spacer(modifier = Modifier.width(6.dp))

          if (device.isPaired) {
            StatusPill(text = "Paired", tone = StatusTone.Neutral)
          } else {
            StatusPill(text = "Not paired", tone = StatusTone.Muted)
          }
        }
      }
    }
  }
}