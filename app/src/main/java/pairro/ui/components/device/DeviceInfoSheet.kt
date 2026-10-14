package pairro.ui.components.device

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import pairro.ui.theme.MonoLabelStyle
import pairro.ui.theme.PairroColors
import pairro.device.DiscoveredDevice
import pairro.ui.components.common.IconButtonTone
import pairro.ui.components.common.IconRoundButton
import pairro.ui.components.common.StatusPill
import pairro.ui.components.common.StatusTone

@Composable
fun DeviceInfoSheet(
  device: DiscoveredDevice,
  onConnect: () -> Unit,
  onUnpair: () -> Unit,
  shouldConfirmUnpair: () -> Boolean,
  modifier: Modifier = Modifier
) {
  var confirmingUnpair by remember(device.id) { mutableStateOf(false) }

  Column(modifier = modifier.padding(horizontal = 20.dp)) {
    Text(
      text = "Device Info",
      style = MaterialTheme.typography.titleLarge,
      color = PairroColors.Wine
    )

    Spacer(modifier = Modifier.height(16.dp))

    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .background(PairroColors.Card)
        .border(1.dp, PairroColors.BorderSubtle, RoundedCornerShape(16.dp))
        .padding(14.dp)
    ) {
      DeviceAvatar(kind = device.kind, showStatusDot = false, isOnline = device.isOnline)

      Spacer(modifier = Modifier.width(12.dp))

      Column {
        Text(text = device.name, style = MaterialTheme.typography.titleMedium)

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

    Spacer(modifier = Modifier.height(18.dp))

    Text(
      text = "CONNECTION DETAILS",
      style = MaterialTheme.typography.titleSmall,
      color = PairroColors.Magenta
    )

    Spacer(modifier = Modifier.height(8.dp))

    Column(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .background(PairroColors.Card)
        .border(1.dp, PairroColors.BorderSubtle, RoundedCornerShape(16.dp))
    ) {
      InfoDetailRow(
        label = "Client ID",
        value = device.id,
        onCopy = { }
      )

      InfoDetailRow(
        label = "IPv4 Address",
        value = device.address.ifEmpty { "\u2014" },
        onCopy = { }
      )
    }

    Spacer(modifier = Modifier.height(20.dp))

    if (device.isOnline) {
      Button(
        onClick = onConnect,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
          containerColor = PairroColors.AccentPrimary,
          contentColor = PairroColors.TextOnAccent
        )
      ) {
        Text(
          text = if (device.isPaired) {
            "Connect"
          } else {
            "Pair & Connect"
          },
          color = PairroColors.TextOnAccent
        )
      }
    }

    if (device.isPaired) {
      Spacer(modifier = Modifier.height(10.dp))

      if (confirmingUnpair) {
        UnpairConfirmation(
          deviceName = device.name,
          onCancel = { confirmingUnpair = false },
          onConfirm = onUnpair
        )
      } else {
        OutlinedButton(
          onClick = {
            if (shouldConfirmUnpair()) {
              confirmingUnpair = true
            } else {
              onUnpair()
            }
          },
          modifier = Modifier.fillMaxWidth(),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = PairroColors.Danger)
        ) {
          Text("Unpair")
        }
      }
    }

    Spacer(modifier = Modifier.height(24.dp))
  }
}

@Composable
private fun UnpairConfirmation(
  deviceName: String,
  onCancel: () -> Unit,
  onConfirm: () -> Unit
) {
  val shape = RoundedCornerShape(16.dp)

  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .fillMaxWidth()
      .clip(shape)
      .background(PairroColors.DangerConfirmSurface)
      .border(1.dp, PairroColors.DangerConfirmBorder, shape)
      .padding(horizontal = 14.dp, vertical = 10.dp)
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = "Unpair this device?",
        style = MaterialTheme.typography.titleMedium
      )

      Spacer(modifier = Modifier.height(2.dp))

      Text(
        text = deviceName,
        style = MaterialTheme.typography.bodySmall,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }

    Spacer(modifier = Modifier.width(12.dp))

    IconRoundButton(
      icon = Icons.Outlined.Close,
      contentDescription = "Cancel",
      onClick = onCancel,
      tone = IconButtonTone.Secondary
    )

    Spacer(modifier = Modifier.width(8.dp))

    IconRoundButton(
      icon = Icons.Outlined.Check,
      contentDescription = "Confirm unpair",
      onClick = onConfirm,
      tone = IconButtonTone.Danger,
      tinted = true
    )
  }
}

@Composable
private fun InfoDetailRow(label: String, value: String, onCopy: () -> Unit) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 14.dp, vertical = 4.dp)
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Text(text = label.uppercase(), style = MaterialTheme.typography.labelSmall)
      Spacer(modifier = Modifier.height(2.dp))
      Text(text = value, style = MonoLabelStyle)
    }

    IconButton(onClick = onCopy) {
      Icon(
        imageVector = Icons.Outlined.ContentCopy,
        contentDescription = "Copy",
        tint = PairroColors.Magenta
      )
    }
  }
}