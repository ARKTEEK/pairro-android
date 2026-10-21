package pairro.network.client

import android.annotation.SuppressLint
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import pairro.di.IoDispatcher
import pairro.network.security.CryptoUtils
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.InetSocketAddress
import java.net.SocketTimeoutException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocket
import javax.net.ssl.X509TrustManager

class TCPClient @AssistedInject constructor(
  @Assisted private val host: String,
  @Assisted private val port: Int,
  @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

  @AssistedFactory
  interface Factory {
    fun create(
      host: String,
      port: Int
    ): TCPClient
  }

  private var socket: SSLSocket? = null
  private var writer: BufferedWriter? = null
  private var reader: BufferedReader? = null

  @Throws(IOException::class)
  fun connect(timeoutMs: Int = 5000) {
    val sslContext = SSLContext.getInstance("TLS")
    sslContext.init(
      null,
      arrayOf(trustAllManager),
      SecureRandom()
    )

    val sslSocket = sslContext.socketFactory.createSocket() as SSLSocket
    sslSocket.enabledProtocols = arrayOf(
      "TLSv1.2",
      "TLSv1.3"
    )
    sslSocket.keepAlive = true
    sslSocket.connect(
      InetSocketAddress(
        host,
        port
      ),
      timeoutMs
    )
    sslSocket.startHandshake()

    socket = sslSocket
    writer = BufferedWriter(OutputStreamWriter(sslSocket.getOutputStream()))
    reader = BufferedReader(InputStreamReader(sslSocket.getInputStream()))
  }

  suspend fun send(message: String) {
    withContext(ioDispatcher) {
      val w = writer ?: throw IOException("Not connected")

      synchronized(this@TCPClient) {
        w.write(message)
        w.write("\n")
        w.flush()
      }
    }
  }

  @Throws(IOException::class)
  fun serverCertificateFingerprint(): String? {
    return try {
      val certificate = socket?.session?.peerCertificates?.firstOrNull()

      certificate?.let { CryptoUtils.certificateFingerprint(it) }
    } catch (e: Exception) {
      null
    }
  }

  fun receiveLine(timeoutMs: Int? = null): String? {
    val s = socket ?: throw IOException("Not connected")

    if (timeoutMs != null) s.soTimeout = timeoutMs

    return try {
      reader?.readLine()
    } catch (e: SocketTimeoutException) {
      throw e
    }
  }

  fun close() {
    try {
      socket?.shutdownInput()
    } catch (_: Exception) {
    }
    try {
      reader?.close()
    } catch (_: Exception) {
    }
    try {
      writer?.close()
    } catch (_: Exception) {
    }
    try {
      socket?.close()
    } catch (_: Exception) {
    }
  }

  @SuppressLint("CustomX509TrustManager")
  private companion object {

    val trustAllManager = object : X509TrustManager {
      override fun checkClientTrusted(
        chain: Array<X509Certificate>,
        authType: String
      ) {
      }

      override fun checkServerTrusted(
        chain: Array<X509Certificate>,
        authType: String
      ) {
      }

      override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()
    }
  }
}