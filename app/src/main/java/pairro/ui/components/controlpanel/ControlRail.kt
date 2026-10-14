package pairro.ui.components.controlpanel

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.automirrored.outlined.VolumeOff
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.outlined.ContentPaste
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.material.icons.outlined.TouchApp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import pairro.ui.theme.PairroColors
import pairro.ui.components.common.IconButtonTone
import pairro.ui.components.common.IconRoundButton

enum class ControlSection {
  Keyboard,
  Clipboard,
  Volume,
  Info
}

@Composable
fun ControlRail(
  activeSection: ControlSection?,
  isMuted: Boolean,
  onToggleSection: (ControlSection) -> Unit,
  onDisconnect: () -> Unit,
  modifier: Modifier = Modifier
) {
  val shape = RoundedCornerShape(16.dp)

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = modifier
      .clip(shape)
      .background(PairroColors.Card)
      .border(1.dp, PairroColors.BorderSubtle, shape)
      .padding(6.dp)
  ) {
    Column(
      verticalArrangement = Arrangement.spacedBy(6.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
      IconRoundButton(
        icon = Icons.Outlined.Keyboard,
        contentDescription = "Keyboard",
        onClick = { onToggleSection(ControlSection.Keyboard) },
        active = activeSection == ControlSection.Keyboard
      )

      IconRoundButton(
        icon = Icons.Outlined.ContentPaste,
        contentDescription = "Clipboard",
        onClick = { onToggleSection(ControlSection.Clipboard) },
        active = activeSection == ControlSection.Clipboard,
        tone = IconButtonTone.Secondary
      )

      IconRoundButton(
        icon = if (isMuted) {
          Icons.AutoMirrored.Outlined.VolumeOff
        } else {
          Icons.AutoMirrored.Outlined.VolumeUp
        },
        contentDescription = "Volume",
        onClick = { onToggleSection(ControlSection.Volume) },
        active = activeSection == ControlSection.Volume
      )

      IconRoundButton(
        icon = Icons.Outlined.TouchApp,
        contentDescription = "Gestures",
        onClick = { onToggleSection(ControlSection.Info) },
        active = activeSection == ControlSection.Info,
        tone = IconButtonTone.Secondary
      )

      Box(
        modifier = Modifier
          .width(20.dp)
          .height(1.dp)
          .background(PairroColors.BorderStrong)
      )

      IconRoundButton(
        icon = Icons.AutoMirrored.Outlined.Logout,
        contentDescription = "Disconnect",
        onClick = onDisconnect,
        tone = IconButtonTone.Danger
      )
    }
  }
}