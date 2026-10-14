package pairro.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import pairro.controller.AudioController
import pairro.controller.ClipboardController
import pairro.controller.DeviceController
import pairro.controller.KeyboardController
import pairro.controller.MouseController
import pairro.device.DiscoveredDevice
import pairro.network.ConnectionSession
import pairro.network.client.PairingResult
import pairro.service.ConnectionForegroundService
import pairro.ui.state.PairingUiState
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
  private val deviceController: DeviceController,
  private val session: ConnectionSession,
  val mouseController: MouseController,
  val keyboardController: KeyboardController,
  val audioController: AudioController,
  val clipboardController: ClipboardController,
  @ApplicationContext private val context: Context
) : ViewModel() {

  private val _targetAddress = mutableStateOf<String?>(session.activeConnection.value?.address)
  val targetAddress: State<String?> = _targetAddress

  private val _connectedDeviceName =
    mutableStateOf(session.activeConnection.value?.deviceName)

  private val _pairingState = mutableStateOf<PairingUiState>(PairingUiState.Idle)
  val pairingState: State<PairingUiState> = _pairingState

  private val _selectedDevice = mutableStateOf<DiscoveredDevice?>(null)

  init {
    viewModelScope.launch {
      session.activeConnection.collect { active ->
        _targetAddress.value = active?.address
        _connectedDeviceName.value = active?.deviceName

        if (active == null) {
          _pairingState.value = PairingUiState.Idle
        }
      }
    }
  }

  fun connectToDevice(desktopId: String, address: String, name: String) {
    _pairingState.value = PairingUiState.Connecting
    _selectedDevice.value = null

    viewModelScope.launch {
      val result = deviceController.connectToDevice(
        desktopId = desktopId,
        address = address,
        onPinGenerated = { pin ->
          _pairingState.value = PairingUiState.AwaitingConfirmation(pin, address)
        }
      )

      when (result) {
        is PairingResult.Accepted -> {
          _pairingState.value = PairingUiState.Idle

          ConnectionForegroundService.start(context, result.desktopName)
        }

        is PairingResult.Rejected -> {
          _pairingState.value = PairingUiState.Rejected(result.reason)
        }

        PairingResult.TimedOut -> {
          _pairingState.value = PairingUiState.TimedOut
        }

        is PairingResult.Error -> {
          _pairingState.value = PairingUiState.Error(result.message)
        }
      }
    }
  }

  fun disconnect() {
    deviceController.disconnect()

    _pairingState.value = PairingUiState.Idle

    ConnectionForegroundService.stop(context)
  }

}