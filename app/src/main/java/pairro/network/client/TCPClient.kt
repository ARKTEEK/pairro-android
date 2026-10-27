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
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
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

  private object Closed

  private var socket: SSLSocket? = null
  private var writer: BufferedWriter? = null
  private var reader: BufferedReader? = null
  private val queuedLines = LinkedBlockingQueue<Any>()

  @Volatile
  private var listening = false

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

  fun startListening(isPushMessage: (String) -> Boolean, onClosed: () -> Unit) {
    val activeReader = reader ?: throw IOException("Not connected")

    socket?.soTimeout = 0
    listening = true

    Thread({ readLines(activeReader, isPushMessage, onClosed) }, "tcp-listener").apply {
      isDaemon = true
      start()
    }
  }

  @Throws(IOException::class)
  fun receiveLine(timeoutMs: Int? = null): String? {
    if (listening) {
      return takeQueuedLine(timeoutMs)
    }

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
      socket?.close()
    } catch (_: Exception) {
    }
    try {
      writer?.close()
    } catch (_: Exception) {
    }
  }

  private fun readLines(
    activeReader: BufferedReader,
    isPushMessage: (String) -> Boolean,
    onClosed: () -> Unit
  ) {
    try {
      while (true) {
        val line = activeReader.readLine() ?: break

        if (!isPushMessage(line)) {
          queuedLines.offer(line)
        }
      }
    } catch (_: IOException) {
    } finally {
      queuedLines.offer(Closed)
      onClosed()
    }
  }

  private fun takeQueuedLine(timeoutMs: Int?): String? {
    val item = if (timeoutMs == null) {
      queuedLines.take()
    } else {
      queuedLines.poll(timeoutMs.toLong(), TimeUnit.MILLISECONDS) ?: throw SocketTimeoutException()
    }

    if (item === Closed) {
      queuedLines.offer(Closed)

      return null
    }

    return item as String
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