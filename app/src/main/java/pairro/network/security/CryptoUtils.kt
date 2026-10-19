package pairro.network.security

import android.annotation.SuppressLint
import java.nio.ByteBuffer
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.MessageDigest
import java.security.PrivateKey
import java.security.PublicKey
import java.security.SecureRandom
import java.security.cert.Certificate
import java.security.spec.ECGenParameterSpec
import java.security.spec.X509EncodedKeySpec
import java.util.Base64
import javax.crypto.KeyAgreement
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object CryptoUtils {
  const val ROLE_SERVER = "server"
  const val ROLE_CLIENT = "client"

  private const val AUTH_CONTEXT = "auth-v1"
  private const val HMAC_ALGORITHM = "HmacSHA256"
  private const val NONCE_BYTES = 32

  private val secureRandom = SecureRandom()

  fun generateNonce(): String {
    val nonce = ByteArray(NONCE_BYTES)
    secureRandom.nextBytes(nonce)

    return Base64
      .getEncoder()
      .encodeToString(nonce)
  }

  fun authProof(
    token: String,
    role: String,
    deviceId: String,
    clientNonce: String,
    serverNonce: String,
    certificateFingerprint: String
  ): String {
    val mac = Mac.getInstance(HMAC_ALGORITHM)
    mac.init(
      SecretKeySpec(
        token.toByteArray(Charsets.UTF_8),
        HMAC_ALGORITHM
      )
    )

    val transcript = listOf(
      AUTH_CONTEXT,
      role,
      deviceId,
      clientNonce,
      serverNonce,
      certificateFingerprint
    ).joinToString("|")

    return Base64
      .getEncoder()
      .encodeToString(mac.doFinal(transcript.toByteArray(Charsets.UTF_8)))
  }

  fun proofsMatch(
    expected: String,
    actual: String
  ): Boolean {
    return MessageDigest.isEqual(
      expected.toByteArray(Charsets.UTF_8),
      actual.toByteArray(Charsets.UTF_8)
    )
  }

  fun certificateFingerprint(certificate: Certificate): String {
    val digest = MessageDigest
      .getInstance("SHA-256")
      .digest(certificate.encoded)

    return digest.joinToString("") { "%02x".format(it) }
  }

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
    return Base64
      .getEncoder()
      .encodeToString(publicKey.encoded)
  }

  fun decodePublicKey(base64Key: String): PublicKey? {
    return try {
      val keyBytes = Base64
        .getDecoder()
        .decode(base64Key)
      val keyFactory = KeyFactory.getInstance("EC")

      keyFactory.generatePublic(X509EncodedKeySpec(keyBytes))
    } catch (e: Exception) {
      e.printStackTrace()
      null
    }
  }

  @SuppressLint("DefaultLocale")
  fun calculatePin(
    localPrivateKey: PrivateKey,
    remotePublicKey: PublicKey
  ): String {
    return try {
      val agreement = KeyAgreement.getInstance("ECDH")

      agreement.init(localPrivateKey)
      agreement.doPhase(
        remotePublicKey,
        true
      )

      val sharedSecret = agreement.generateSecret()

      val digest = MessageDigest.getInstance("SHA-256")
      val hash = digest.digest(sharedSecret)

      val positiveHash = ByteBuffer.wrap(hash).int and 0x7FFFFFFF
      val pinNumber = positiveHash % 1000000

      String.format(
        "%06d",
        pinNumber
      )
    } catch (e: Exception) {
      e.printStackTrace()
      "000000"
    }
  }
}