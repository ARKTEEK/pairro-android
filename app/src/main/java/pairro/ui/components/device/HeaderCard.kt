package pairro.ui.components.device

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PowerSettingsNew
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import pairro.ui.theme.PairroColors
import pairro.ui.components.common.CaptionStrip
import pairro.ui.components.common.IconButtonTone
import pairro.ui.components.common.IconRoundButton
import pairro.ui.components.common.StatusPill
import pairro.ui.components.common.StatusTone

@Composable
fun HeaderCard(
  deviceName: String,
  isScanning: Boolean,
  onRefresh: () -> Unit,
  onToggleScanning: () -> Unit,
  onOpenSettings: () -> Unit,
  modifier: Modifier = Modifier
) {
  val shape = RoundedCornerShape(18.dp)

  Column(
    modifier = modifier
      .fillMaxWidth()
      .clip(shape)
      .background(PairroColors.Card)
  ) {
    CaptionStrip(title = "Local device")

    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(14.dp)
    ) {
      DeviceAvatar(kind = DeviceKind.Phone, showStatusDot = false, isOnline = true)

      Spacer(modifier = Modifier.width(10.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = deviceName,
          style = MaterialTheme.typography.titleMedium,
          color = PairroColors.TextPrimary,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(6.dp))

        StatusPill(
          text = if (isScanning) {
            "Discoverable"
          } else {
            "Hidden"
          },
          tone = if (isScanning) {
            StatusTone.Positive
          } else {
            StatusTone.Muted
          }
        )
      }

      IconRoundButton(
        icon = Icons.Outlined.Settings,
        contentDescription = "Settings",
        onClick = onOpenSettings
      )

      Spacer(modifier = Modifier.width(8.dp))

      IconRoundButton(
        icon = Icons.Outlined.Refresh,
        contentDescription = "Rescan",
        onClick = onRefresh,
        tone = IconButtonTone.Secondary
      )

      Spacer(modifier = Modifier.width(8.dp))

      IconRoundButton(
        icon = Icons.Outlined.PowerSettingsNew,
        contentDescription = "Toggle discoverability",
        onClick = onToggleScanning,
        active = isScanning,
        tone = IconButtonTone.Secondary
      )
    }
  }
}