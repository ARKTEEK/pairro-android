package pairro.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import pairro.device.DeviceInfo
import pairro.device.DiscoveredDevice
import pairro.network.NetworkConfig
import pairro.network.mdns.DeviceBroadcaster
import pairro.network.mdns.DeviceScanner
import pairro.store.PairedDesktopStore
import pairro.store.SettingsStore
import javax.inject.Inject

@HiltViewModel
class DiscoveryViewModel @Inject constructor(
  private val pairedDesktopStore: PairedDesktopStore,
  private val settingsStore: SettingsStore,
  private val deviceInfo: DeviceInfo,
  private val networkConfig: NetworkConfig,
  private val scanner: DeviceScanner,
  private val broadcaster: DeviceBroadcaster
) : ViewModel() {
  val thisDeviceName: StateFlow<String> = deviceInfo.deviceName

  private var isBroadcasting = false

  var isScanning by mutableStateOf(settingsStore.autoStartDiscoverability)
    private set

  val pairedDevices = mutableStateListOf<DiscoveredDevice>()
  val unpairedDevices = mutableStateListOf<DiscoveredDevice>()

  init {
    loadPairedDevices()

    viewModelScope.launch {
      deviceInfo.deviceName.drop(1).collect { newName ->
        if (isBroadcasting) {
          broadcaster.stop()
          broadcaster.start(newName, networkConfig.tcpPort)
        }
      }
    }
  }

  fun shouldConfirmUnpair(): Boolean = settingsStore.confirmUnpair

  fun toggleScanning() {
    isScanning = !isScanning
  }

  fun startScanning() {
    loadPairedDevices()
    unpairedDevices.clear()
    broadcaster.start(deviceInfo.getDeviceName(), networkConfig.tcpPort)
    isBroadcasting = true
    scanner.start { id, name, address -> onDeviceFound(id, name, address) }
  }

  fun stopScanning() {
    scanner.stop()
    broadcaster.stop()
    isBroadcasting = false
  }

  private fun loadPairedDevices() {
    pairedDevices.clear()

    pairedDesktopStore.getAll().forEach { (id, name, _) ->
      pairedDevices.add(DiscoveredDevice(id, name, "", isPaired = true, isOnline = false))
    }
  }

  fun unpair(id: String) {
    pairedDesktopStore.remove(id)

    val pairedIndex = pairedDevices.indexOfFirst { it.id == id }

    if (pairedIndex == -1) {
      return
    }

    val device = pairedDevices.removeAt(pairedIndex)

    if (device.isOnline) {
      upsertUnpaired(device.copy(isPaired = false))
    }
  }

  private fun onDeviceFound(id: String, name: String, address: String) {
    val pairedIndex = pairedDevices.indexOfFirst { it.id == id }

    if (pairedIndex != -1) {
      pairedDevices[pairedIndex] = pairedDevices[pairedIndex].copy(
        name = name,
        address = address,
        isOnline = true
      )

      return
    }

    upsertUnpaired(DiscoveredDevice(id, name, address, isPaired = false, isOnline = true))
  }

  private fun upsertUnpaired(device: DiscoveredDevice) {
    val unpairedIndex = unpairedDevices.indexOfFirst { it.id == device.id }

    if (unpairedIndex != -1) {
      unpairedDevices[unpairedIndex] = device
    } else {
      unpairedDevices.add(device)
    }
  }
}
