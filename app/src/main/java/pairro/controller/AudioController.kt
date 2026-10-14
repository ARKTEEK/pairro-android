package pairro.controller

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import pairro.di.ApplicationScope
import pairro.di.IoDispatcher
import pairro.network.ConnectionSession
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.milliseconds

data class AudioState(val volume: Int, val isMuted: Boolean)

@Singleton
class AudioController @Inject constructor(
  private val session: ConnectionSession,
  @ApplicationScope private val scope: CoroutineScope,
  @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
  private val fetchMutex = Mutex()
  private val pendingVolumes = Channel<Int>(Channel.CONFLATED)

  init {
    scope.launch(ioDispatcher) {
      for (volume in pendingVolumes) {
        sendTcpCommand("VOLUME_SET:$volume")

        delay(VOLUME_SEND_INTERVAL_MS.milliseconds)
      }
    }
  }

  suspend fun fetchState(): AudioState? = withContext(ioDispatcher) {
    val client = session.tcpClient ?: return@withContext null

    fetchMutex.withLock {
      try {
        client.send("VOLUME_GET")

        repeat(MAX_LINES_PER_FETCH) {
          val line = client.receiveLine(FETCH_TIMEOUT_MS) ?: return@withContext null

          if (line.startsWith(STATE_PREFIX)) {
            return@withContext parseState(line.removePrefix(STATE_PREFIX))
          }
        }

        null
      } catch (e: IOException) {
        System.err.println("Failed to fetch audio state: ${e.message}")

        null
      }
    }
  }

  fun setVolume(volume: Int) {
    pendingVolumes.trySend(volume.coerceIn(MIN_VOLUME, MAX_VOLUME))
  }

  fun setMuted(isMuted: Boolean) {
    val flag = if (isMuted) 1 else 0

    scope.launch(ioDispatcher) {
      sendTcpCommand("VOLUME_MUTE:$flag")
    }
  }

  private suspend fun sendTcpCommand(command: String) {
    val client = session.tcpClient ?: return

    try {
      client.send(command)
    } catch (e: IOException) {
      System.err.println("Failed to send command: ${e.message}")
    }
  }

  private fun parseState(payload: String): AudioState? {
    val parts = payload.split(",")
    val volume = parts.getOrNull(0)?.trim()?.toIntOrNull() ?: return null
    val isMuted = parts.getOrNull(1)?.trim() == "1"

    return AudioState(volume.coerceIn(MIN_VOLUME, MAX_VOLUME), isMuted)
  }

  private companion object {
    const val STATE_PREFIX = "VOLUME_STATE:"
    const val MIN_VOLUME = 0
    const val MAX_VOLUME = 100
    const val FETCH_TIMEOUT_MS = 3000
    const val MAX_LINES_PER_FETCH = 20
    const val VOLUME_SEND_INTERVAL_MS = 50L
  }
}