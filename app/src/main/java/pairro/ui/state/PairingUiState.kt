package pairro.ui.state

sealed class PairingUiState {
  object Idle : PairingUiState()
  object Connecting : PairingUiState()
  data class AwaitingConfirmation(val pin: String, val address: String) : PairingUiState()
  data class Rejected(val reason: String?) : PairingUiState()
  object TimedOut : PairingUiState()
  data class Error(val message: String) : PairingUiState()
}