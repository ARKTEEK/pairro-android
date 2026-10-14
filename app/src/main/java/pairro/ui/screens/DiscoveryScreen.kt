package pairro.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import pairro.ui.theme.PairroBrushes
import pairro.ui.theme.PairroColors
import pairro.device.DiscoveredDevice
import pairro.ui.components.common.CaptionStrip
import pairro.ui.components.device.DeviceInfoSheet
import pairro.ui.components.device.DeviceRow
import pairro.ui.components.device.HeaderCard
import pairro.ui.components.sheet.SwipeToDismissBottomSheet
import pairro.ui.viewmodel.DiscoveryViewModel

@Composable
fun DiscoveryScreen(
  viewModel: DiscoveryViewModel,
  onConnect: (String, String, String) -> Unit,
  onOpenSettings: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isScanning = viewModel.isScanning
  val thisDeviceName by viewModel.thisDeviceName.collectAsState()
  var query by remember { mutableStateOf("") }
  var selectedDevice by remember { mutableStateOf<DiscoveredDevice?>(null) }

  DisposableEffect(isScanning) {
    if (isScanning) {
      viewModel.startScanning()
    } else {
      viewModel.stopScanning()
    }

    onDispose {
      viewModel.stopScanning()
    }
  }

  val filteredPaired = viewModel.pairedDevices.filter { it.name.contains(query, ignoreCase = true) }
  val filteredUnpaired =
    viewModel.unpairedDevices.filter { it.name.contains(query, ignoreCase = true) }

  Box(modifier = modifier.fillMaxSize()) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .background(PairroBrushes.Hero)
    ) {
      Column(modifier = Modifier.padding(start = 20.dp, top = 24.dp, end = 20.dp, bottom = 24.dp)) {
        HeaderCard(
          deviceName = thisDeviceName,
          isScanning = isScanning,
          onRefresh = { viewModel.startScanning() },
          onToggleScanning = viewModel::toggleScanning,
          onOpenSettings = onOpenSettings
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
          value = query,
          onValueChange = { query = it },
          placeholder = {
            Text("Search devices...", color = PairroColors.TextMuted)
          },
          leadingIcon = {
            Icon(
              Icons.Outlined.Search,
              contentDescription = null,
              tint = PairroColors.Magenta
            )
          },
          singleLine = true,
          shape = RoundedCornerShape(18.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = PairroColors.Card,
            unfocusedContainerColor = PairroColors.Card,
            focusedBorderColor = PairroColors.AccentSecondary,
            unfocusedBorderColor = PairroColors.BorderSubtle,
            focusedTextColor = PairroColors.TextPrimary,
            unfocusedTextColor = PairroColors.TextPrimary,
            cursorColor = PairroColors.AccentPrimary
          ),
          modifier = Modifier.fillMaxWidth()
        )
      }

      Column(
        modifier = Modifier
          .fillMaxSize()
          .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
          .background(PairroColors.Blush)
      ) {
        CaptionStrip(title = "Devices", horizontalPadding = 16.dp)

        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 12.dp, end = 16.dp)
        ) {
          if (filteredPaired.isEmpty() && filteredUnpaired.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
              Text(
                text = if (isScanning) "Searching for devices\u2026" else "Discovery is off",
                style = MaterialTheme.typography.bodySmall,
                color = PairroColors.TextSecondary
              )
            }
          } else {
            LazyColumn(
              modifier = Modifier.fillMaxSize(),
              contentPadding = PaddingValues(bottom = 24.dp)
            ) {
              items(filteredPaired, key = { it.id }) { device ->
                DeviceRow(device = device, onClick = { selectedDevice = device })
              }

              if (filteredPaired.isNotEmpty() && filteredUnpaired.isNotEmpty()) {
                item { Spacer(modifier = Modifier.height(8.dp)) }
              }

              items(filteredUnpaired, key = { it.id }) { device ->
                DeviceRow(device = device, onClick = { selectedDevice = device })
              }
            }
          }
        }
      }
    }

    selectedDevice?.let { device ->
      SwipeToDismissBottomSheet(onDismiss = { selectedDevice = null }) {
        DeviceInfoSheet(
          device = device,
          onConnect = {
            onConnect(device.id, device.address, device.name)
            selectedDevice = null
          },
          onUnpair = {
            viewModel.unpair(device.id)
            selectedDevice = null
          },
          shouldConfirmUnpair = viewModel::shouldConfirmUnpair
        )
      }
    }
  }
}