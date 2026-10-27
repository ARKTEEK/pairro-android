package pairro.network
import pairro.network.client.TCPClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import pairro.network.client.UDPClient
import javax.inject.Inject
import javax.inject.Singleton

data class ActiveConnection(val address: String, val deviceName: String)

@Singleton
class ConnectionSession @Inject constructor() {
  @Volatile
  var tcpClient: TCPClient? = null
    private set

  @Volatile
  var udpClient: UDPClient? = null
    private set

  private val _activeConnection = MutableStateFlow<ActiveConnection?>(null)
  val activeConnection: StateFlow<ActiveConnection?> = _activeConnection.asStateFlow()

  fun attachTcpClient(client: TCPClient) {
    tcpClient = client
  }

  fun closeTcpClient() {
    val client = tcpClient
    tcpClient = null

    client?.close()
  }

  fun attachUdpClient(client: UDPClient) {
    udpClient?.close()
    udpClient = client
  }

  fun markConnected(address: String, deviceName: String) {
    _activeConnection.value = ActiveConnection(address, deviceName)
  }

  fun closeAll() {
    closeTcpClient()

    udpClient?.close()
    udpClient = null

    _activeConnection.value = null
  }
}