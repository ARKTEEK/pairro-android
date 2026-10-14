package pairro.controller

import pairro.network.client.TCPClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import pairro.device.DeviceInfo
import pairro.di.IoDispatcher
import pairro.network.ConnectionSession
import pairro.network.NetworkConfig
import pairro.network.client.PairingClient
import pairro.network.client.PairingResult
import pairro.network.client.UDPClient
import pairro.network.security.CryptoUtils
import pairro.store.PairedDesktopStore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceController @Inject constructor(
  private val deviceInfo: DeviceInfo,
  private val pairedDesktopStore: PairedDesktopStore,
  private val networkConfig: NetworkConfig,
  private val session: ConnectionSession,
  private val tcpClientFactory: TCPClient.Factory,
  private val udpClientFactory: UDPClient.Factory,
  private val pairingClientFactory: PairingClient.Factory,
  @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

  suspend fun connectToDevice(
    desktopId: String,
    address: String,
    onPinGenerated: (String) -> Unit
  ): PairingResult = withContext(ioDispatcher) {
    try {
      val client = tcpClientFactory.create(address, networkConfig.tcpPort)
      client.connect()

      session.attachTcpClient(client)

      val pairingClient = pairingClientFactory.create(client)
      val stored = pairedDesktopStore.find(desktopId)

      if (stored != null) {
        val (storedName, token) = stored

        when (val authResult = pairingClient.authenticate(desktopId, token)) {
          is PairingResult.Accepted -> {
            val sessionKey = CryptoUtils.deriveKeyFromToken(token)
            openUdpChannel(address, sessionKey)
            session.markConnected(address, storedName)

            return@withContext authResult.copy(desktopName = storedName)
          }

          is PairingResult.Rejected -> {
            pairedDesktopStore.remove(desktopId)
          }

          else -> {
            session.closeTcpClient()

            return@withContext authResult
          }
        }
      }

      val result = pairingClient.requestPairing(
        deviceName = deviceInfo.getDeviceName(),
        onPinGenerated = onPinGenerated
      )

      when (result) {
        is PairingResult.Accepted -> {
          pairedDesktopStore.save(result.desktopId, result.desktopName, result.token)

          val sessionKey = CryptoUtils.deriveKeyFromToken(result.token)

          openUdpChannel(address, sessionKey)
          session.markConnected(address, result.desktopName)
        }

        else -> {
          session.closeTcpClient()
        }
      }

      result
    } catch (e: Exception) {
      System.err.println("Could not connect: ${e.message}")

      PairingResult.Error(e.message ?: "Connection failed")
    }
  }

  private fun openUdpChannel(address: String, sessionKey: ByteArray) {
    val client = udpClientFactory.create(address, networkConfig.udpPort)
    client.setSessionKey(sessionKey)

    session.attachUdpClient(client)
  }

  fun disconnect() {
    session.closeAll()
  }
}