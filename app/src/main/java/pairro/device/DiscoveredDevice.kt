package pairro.device

data class DiscoveredDevice(
  val id: String,
  val name: String,
  val address: String,
  val isPaired: Boolean,
  val isOnline: Boolean
)