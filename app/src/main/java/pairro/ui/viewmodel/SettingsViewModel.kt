package pairro.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import pairro.device.DeviceInfo
import pairro.store.SettingsStore
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
  private val settingsStore: SettingsStore,
  private val deviceInfo: DeviceInfo
) : ViewModel() {
  val systemDeviceName: String = deviceInfo.getSystemDeviceName()

  var autoStartDiscoverability by mutableStateOf(settingsStore.autoStartDiscoverability)
    private set

  var deviceName by mutableStateOf(settingsStore.deviceName)
    private set

  var touchpadSensitivity by mutableStateOf(settingsStore.touchpadSensitivity)
    private set

  var hapticFeedback by mutableStateOf(settingsStore.hapticFeedback)
    private set

  var keepScreenOn by mutableStateOf(settingsStore.keepScreenOn)
    private set

  var confirmUnpair by mutableStateOf(settingsStore.confirmUnpair)
    private set

  fun updateAutoStartDiscoverability(value: Boolean) {
    autoStartDiscoverability = value
    settingsStore.autoStartDiscoverability = value
  }

  fun updateDeviceName(value: String) {
    val trimmed = value.trim()

    deviceName = trimmed
    deviceInfo.setCustomDeviceName(trimmed)
  }

  fun updateTouchpadSensitivity(value: Float) {
    touchpadSensitivity = value
    settingsStore.touchpadSensitivity = value
  }

  fun updateHapticFeedback(value: Boolean) {
    hapticFeedback = value
    settingsStore.hapticFeedback = value
  }

  fun updateKeepScreenOn(value: Boolean) {
    keepScreenOn = value
    settingsStore.keepScreenOn = value
  }

  fun updateConfirmUnpair(value: Boolean) {
    confirmUnpair = value
    settingsStore.confirmUnpair = value
  }
}
