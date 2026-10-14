package pairro.ui.components.device

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Computer
import androidx.compose.material.icons.outlined.Smartphone
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import pairro.ui.theme.PairroColors
import pairro.device.DiscoveredDevice

enum class DeviceKind { Desktop, Phone }

val DiscoveredDevice.kind: DeviceKind
  get() = if (isPaired) DeviceKind.Desktop else DeviceKind.Phone

@Composable
fun DeviceAvatar(
  kind: DeviceKind,
  showStatusDot: Boolean,
  isOnline: Boolean,
  modifier: Modifier = Modifier,
  size: Dp = 40.dp,
  muted: Boolean = false
) {
  Box(
    modifier = modifier
      .size(size)
      .clip(RoundedCornerShape(12.dp))
      .background(
        if (muted) {
          PairroColors.AvatarTileMuted
        } else {
          PairroColors.AvatarTile
        }
      ),
    contentAlignment = Alignment.Center
  ) {
    Icon(
      imageVector = if (kind == DeviceKind.Desktop) {
        Icons.Outlined.Computer
      } else {
        Icons.Outlined.Smartphone
      },
      contentDescription = null,
      tint = if (muted) {
        PairroColors.TextMuted
      } else {
        PairroColors.Magenta
      },
      modifier = Modifier.size(size * 0.55f)
    )

    if (showStatusDot) {
      Box(
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .size(size * 0.24f)
          .clip(CircleShape)
          .background(
            if (isOnline) {
              PairroColors.AccentSecondary
            } else {
              PairroColors.TextMuted
            }
          )
      )
    }
  }
}
