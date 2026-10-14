package pairro.network.client

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import pairro.device.DeviceIdentity
import pairro.di.IoDispatcher
import pairro.di.MainDispatcher
import pairro.network.security.CryptoUtils
import pairro.protocol.Protocol

import java.io.IOException
import java.net.SocketTimeoutException

sealed class PairingResult {
  data class Accepted(val desktopId: String, val desktopName: String, val pin: String, val token: String) :
    PairingResult()

  data class Rejected(val reason: String?) : PairingResult()
  object TimedOut : PairingResult()
  data class Error(val message: String) : PairingResult()
}

class PairingClient @AssistedInject constructor(
  @Assisted private val tcpClient: TCPClient,
  private val identity: DeviceIdentity,
  @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
  @MainDispatcher private val mainDispatcher: CoroutineDispatcher
) {
  @AssistedFactory
  interface Factory {
    fun create(tcpClient: TCPClient): PairingClient
  }

  suspend fun authenticate(
    desktopId: String,
    token: String,
    timeoutMs: Int = 15000
  ): PairingResult = withContext(ioDispatcher) {
    try {
      val payload = Protocol.encode(
        "AUTH",
        mapOf("deviceId" to identity.getId(), "token" to token)
      )
      tcpClient.send(payload)

      val resultLine = tcpClient.receiveLine(timeoutMs)
        ?: return@withContext PairingResult.TimedOut

      val (resultType, resultFields) = Protocol.decode(resultLine)

      when (resultType) {
        "AUTH_OK" -> PairingResult.Accepted(desktopId, "", "", token)
        "AUTH_FAIL" -> PairingResult.Rejected(resultFields["reason"])
        else -> PairingResult.Error("Unexpected auth response: $resultType")
      }
    } catch (e: SocketTimeoutException) {
      PairingResult.TimedOut
    } catch (e: IOException) {
      PairingResult.Error(e.message ?: "IO error")
    }
  }

  suspend fun requestPairing(
    deviceName: String,
    onPinGenerated: (String) -> Unit,
    timeoutMs: Int = 15000
  ): PairingResult = withContext(ioDispatcher) {
    try {
      val keyPair = CryptoUtils.generateKeyPair()
        ?: return@withContext PairingResult.Error("Failed to generate key pair")

      val publicKeyString = CryptoUtils.encodePublicKey(keyPair.public)

      val payload = Protocol.encode(
        "PAIR_REQUEST",
        mapOf(
          "deviceId" to identity.getId(),
          "deviceName" to deviceName,
          "publicKey" to publicKeyString
        )
      )

      tcpClient.send(payload)

      val ackLine = tcpClient.receiveLine(timeoutMs)
        ?: return@withContext PairingResult.TimedOut

      val (ackType, ackFields) = Protocol.decode(ackLine)

      if (ackType != "PAIR_CHALLENGE") {
        return@withContext PairingResult.Error("Unexpected response: $ackType")
      }

      val remotePublicKeyStr = ackFields["publicKey"].orEmpty()
      val remotePublicKey = CryptoUtils.decodePublicKey(remotePublicKeyStr)
        ?: return@withContext PairingResult.Error("Invalid public key from desktop")

      val pin = CryptoUtils.calculatePin(keyPair.private, remotePublicKey)

      withContext(mainDispatcher) {
        onPinGenerated(pin)
      }

      val decisionLine = tcpClient.receiveLine(timeoutMs)
        ?: return@withContext PairingResult.TimedOut

      val (decisionType, decisionFields) = Protocol.decode(decisionLine)

      when (decisionType) {
        "PAIR_SUCCESS" -> PairingResult.Accepted(
          decisionFields["deviceId"].orEmpty(),
          decisionFields["deviceName"].orEmpty(),
          pin,
          decisionFields["token"].orEmpty()
        )
        "PAIR_DECISION" -> PairingResult.Rejected(decisionFields["reason"])
        else -> PairingResult.Error("Unexpected decision: $decisionType")
      }
    } catch (e: SocketTimeoutException) {
      PairingResult.TimedOut
    } catch (e: IOException) {
      PairingResult.Error(e.message ?: "IO error")
    }
  }
}