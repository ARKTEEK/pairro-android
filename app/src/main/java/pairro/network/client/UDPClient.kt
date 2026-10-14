package pairro.network.client

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import pairro.network.security.UDPCrypto
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class UDPClient @AssistedInject constructor(
  @Assisted serverAddressString: String,
  @Assisted private val port: Int
) {
  @AssistedFactory
  interface Factory {
    fun create(serverAddressString: String, port: Int): UDPClient
  }

  private val serverAddress: InetAddress = InetAddress.getByName(serverAddressString)
  private val socket: DatagramSocket = DatagramSocket()
  private var crypto: UDPCrypto? = null

  fun setSessionKey(keyBytes: ByteArray) {
    this.crypto = UDPCrypto(keyBytes)
  }

  fun send(command: String) {
    val cryptoInstance = crypto ?: return

    try {
      val plainBytes = command.toByteArray(Charsets.UTF_8)
      val encryptedBytes = cryptoInstance.encrypt(plainBytes)
      val packet = DatagramPacket(encryptedBytes, encryptedBytes.size, serverAddress, port)

      socket.send(packet)
    } catch (e: Exception) {
      System.err.println("Failed to send UDP packet: ${e.message}")
    }
  }

  fun close() {
    if (!socket.isClosed) {
      socket.close()
    }
  }
}