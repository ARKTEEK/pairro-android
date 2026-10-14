package pairro.network.security

import android.annotation.SuppressLint
import java.nio.ByteBuffer
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.MessageDigest
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.ECGenParameterSpec
import java.security.spec.X509EncodedKeySpec
import java.util.Base64
import javax.crypto.KeyAgreement

object CryptoUtils {

  fun generateKeyPair(): KeyPair? {
    return try {
      val keyPairGenerator = KeyPairGenerator.getInstance("EC")
      val ecSpec = ECGenParameterSpec("secp256r1")

      keyPairGenerator.initialize(ecSpec)
      keyPairGenerator.generateKeyPair()
    } catch (e: Exception) {
      e.printStackTrace()
      null
    }
  }

  fun deriveKeyFromToken(token: String): ByteArray {
    val digest = MessageDigest.getInstance("SHA-256")

    return digest.digest(token.toByteArray(Charsets.UTF_8))
  }

  fun encodePublicKey(publicKey: PublicKey): String {
    return Base64.getEncoder().encodeToString(publicKey.encoded)
  }

  fun decodePublicKey(base64Key: String): PublicKey? {
    return try {
      val keyBytes = Base64.getDecoder().decode(base64Key)
      val keyFactory = KeyFactory.getInstance("EC")

      keyFactory.generatePublic(X509EncodedKeySpec(keyBytes))
    } catch (e: Exception) {
      e.printStackTrace()
      null
    }
  }

  @SuppressLint("DefaultLocale")
  fun calculatePin(localPrivateKey: PrivateKey, remotePublicKey: PublicKey): String {
    return try {
      val agreement = KeyAgreement.getInstance("ECDH")

      agreement.init(localPrivateKey)
      agreement.doPhase(remotePublicKey, true)

      val sharedSecret = agreement.generateSecret()

      val digest = MessageDigest.getInstance("SHA-256")
      val hash = digest.digest(sharedSecret)

      val positiveHash = ByteBuffer.wrap(hash).int and 0x7FFFFFFF
      val pinNumber = positiveHash % 1000000

      String.format("%06d", pinNumber)
    } catch (e: Exception) {
      e.printStackTrace()
      "000000"
    }
  }
}