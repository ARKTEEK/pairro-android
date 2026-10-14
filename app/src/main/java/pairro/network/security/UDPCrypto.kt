package pairro.network.security

import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicLong
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

class UDPCrypto(keyBytes: ByteArray) {
  private val keySpec = SecretKeySpec(keyBytes, "AES")
  private val outboundSequence = AtomicLong(1)

  fun encrypt(plainText: ByteArray): ByteArray {
    val seq = outboundSequence.getAndIncrement()
    val iv = ByteArray(12)

    ByteBuffer.wrap(iv).putLong(4, seq)

    val cipher = Cipher.getInstance("AES/GCM/NoPadding")
    val spec = GCMParameterSpec(128, iv)

    cipher.init(Cipher.ENCRYPT_MODE, keySpec, spec)

    val cipherText = cipher.doFinal(plainText)
    val packet = ByteBuffer.allocate(12 + cipherText.size)

    packet.put(iv)
    packet.put(cipherText)

    return packet.array()
  }
}