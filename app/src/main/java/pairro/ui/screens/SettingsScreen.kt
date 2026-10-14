package pairro.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pairro.ui.theme.PairroBrushes
import pairro.ui.theme.PairroColors
import pairro.ui.components.settings.DeviceNameDialog
import pairro.ui.components.settings.SettingsCard
import pairro.ui.components.settings.SettingsDivider
import pairro.ui.components.settings.SettingsSliderRow
import pairro.ui.components.settings.SettingsSwitchRow
import pairro.ui.components.settings.SettingsValueRow
import pairro.ui.viewmodel.SettingsViewModel
import kotlin.math.roundToInt

@Composable
fun SettingsScreen(
  viewModel: SettingsViewModel,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler(onBack = onBack)

  var editingName by remember { mutableStateOf(false) }

  val context = LocalContext.current
  val versionName = remember {
    runCatching {
      context.packageManager.getPackageInfo(context.packageName, 0).versionName
    }.getOrNull()
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(PairroBrushes.Hero)
      .clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() },
        onClick = {}
      )
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .fillMaxWidth()
        .padding(start = 20.dp, top = 20.dp, end = 20.dp, bottom = 24.dp)
    ) {
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .size(40.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(Color.White.copy(alpha = 0.16f))
          .clickable(onClick = onBack)
      ) {
        Icon(
          imageVector = Icons.Outlined.ArrowBack,
          contentDescription = "Back",
          tint = Color.White,
          modifier = Modifier.size(20.dp)
        )
      }

      Spacer(modifier = Modifier.width(14.dp))

      Text(
        text = "Settings",
        style = MaterialTheme.typography.titleLarge.copy(
          color = Color.White,
          fontSize = 20.sp
        )
      )
    }

    Column(
      verticalArrangement = Arrangement.spacedBy(12.dp),
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth()
        .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
        .background(PairroColors.Blush)
        .verticalScroll(rememberScrollState())
        .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 24.dp)
    ) {
      SettingsCard(title = "Discovery") {
        SettingsSwitchRow(
          title = "Auto-start discoverability",
          subtitle = "Become visible to desktops as soon as the app opens",
          checked = viewModel.autoStartDiscoverability,
          onCheckedChange = viewModel::updateAutoStartDiscoverability
        )

        SettingsDivider()

        SettingsValueRow(
          title = "Device name",
          subtitle = "How this phone appears to other devices",
          value = viewModel.deviceName.ifBlank { viewModel.systemDeviceName },
          onClick = { editingName = true }
        )
      }

      SettingsCard(title = "Control") {
        SettingsSliderRow(
          title = "Touchpad sensitivity",
          subtitle = "How far the cursor moves per swipe",
          value = viewModel.touchpadSensitivity,
          valueLabel = "%.1f\u00D7".format(viewModel.touchpadSensitivity),
          valueRange = 0.1f..5.0f,
          steps = 48,
          onValueChange = { viewModel.updateTouchpadSensitivity((it * 10).roundToInt() / 10f) }
        )

        SettingsDivider()

        SettingsSwitchRow(
          title = "Haptic feedback",
          subtitle = "Vibrate on taps and gestures",
          checked = viewModel.hapticFeedback,
          onCheckedChange = viewModel::updateHapticFeedback
        )

        SettingsDivider()

        SettingsSwitchRow(
          title = "Keep screen on",
          subtitle = "Prevent the screen from sleeping while controlling",
          checked = viewModel.keepScreenOn,
          onCheckedChange = viewModel::updateKeepScreenOn
        )
      }

      SettingsCard(title = "Safety") {
        SettingsSwitchRow(
          title = "Confirm before unpairing",
          subtitle = "Ask first when removing a paired device",
          checked = viewModel.confirmUnpair,
          onCheckedChange = viewModel::updateConfirmUnpair
        )
      }

      Text(
        text = if (versionName != null) {
          "Pairro $versionName"
        } else {
          "Pairro"
        },
        style = MaterialTheme.typography.bodySmall,
        color = PairroColors.TextMuted,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
      )
    }
  }

  if (editingName) {
    DeviceNameDialog(
      currentName = viewModel.deviceName,
      systemName = viewModel.systemDeviceName,
      onDismiss = { editingName = false },
      onSave = {
        viewModel.updateDeviceName(it)
        editingName = false
      }
    )
  }
}
