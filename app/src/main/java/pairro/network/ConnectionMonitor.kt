package pairro.network

import pairro.network.client.TCPClient
import pairro.protocol.Protocol
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConnectionMonitor @Inject constructor() {
  fun watch(client: TCPClient, onRemoteDisconnect: () -> Unit) {
    client.startListening(
      isPushMessage = { line -> handle(line, onRemoteDisconnect) },
      onClosed = onRemoteDisconnect
    )
  }

  private fun handle(line: String, onRemoteDisconnect: () -> Unit): Boolean {
    return when (Protocol.decode(line).first) {
      PONG -> true

      DISCONNECT -> {
        onRemoteDisconnect()
        true
      }

      else -> false
    }
  }

  private companion object {
    const val PONG = "PONG"
    const val DISCONNECT = "DISCONNECT"
  }
}
