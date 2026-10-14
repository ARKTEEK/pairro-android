package pairro.controller

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import pairro.di.ApplicationScope
import pairro.di.IoDispatcher
import pairro.model.ClipboardEntry
import pairro.network.ConnectionSession
import java.io.IOException
import java.util.Base64
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClipboardController @Inject constructor(
  private val session: ConnectionSession,
  @ApplicationContext private val context: Context,
  @ApplicationScope private val scope: CoroutineScope,
  @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
  private val fetchMutex = Mutex()
  private val pendingTcpCommands = Channel<String>(Channel.UNLIMITED)

  private val _localHistory = MutableStateFlow<List<ClipboardEntry>>(emptyList())

  val localHistory: StateFlow<List<ClipboardEntry>> = _localHistory.asStateFlow()

  private val clipboardManager: ClipboardManager
    get() = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

  init {
    scope.launch(ioDispatcher) {
      for (command in pendingTcpCommands) {
        sendTcpCommand(command)
      }
    }

    clipboardManager.addPrimaryClipChangedListener { readLocalClipboard() }
  }

  suspend fun fetchDesktopHistory(): List<ClipboardEntry>? = withContext(ioDispatcher) {
    val client = session.tcpClient ?: return@withContext null

    fetchMutex.withLock {
      try {
        client.send("CLIPBOARD_HISTORY_GET")

        val entries = mutableListOf<ClipboardEntry>()

        repeat(MAX_LINES_PER_HISTORY_FETCH) {
          val line = client.receiveLine(FETCH_TIMEOUT_MS) ?: return@withContext null

          if (line == HISTORY_END) {
            return@withContext entries
          }

          if (line.startsWith(ENTRY_PREFIX)) {
            parseEntry(line.removePrefix(ENTRY_PREFIX))?.let { entries.add(it) }
          }
        }

        null
      } catch (exception: IOException) {
        System.err.println("Failed to fetch clipboard history: ${exception.message}")

        null
      }
    }
  }

  fun sendToDesktop(text: String) {
    pendingTcpCommands.trySend("CLIPBOARD_SET:${encode(text)}")
  }

  fun appendToDesktop(text: String) {
    pendingTcpCommands.trySend("CLIPBOARD_APPEND:${encode(text)}")
  }

  fun clearDesktopHistory() {
    pendingTcpCommands.trySend("CLIPBOARD_HISTORY_CLEAR")
  }

  fun clearLocalHistory() {
    _localHistory.value = emptyList()
  }

  fun readLocalClipboard(): String? {
    val text = runCatching {
      val clipData = clipboardManager.primaryClip

      if (clipData == null || clipData.itemCount == 0) {
        null
      } else {
        clipData.getItemAt(0).coerceToText(context)?.toString()
      }
    }.getOrNull() ?: return null

    recordLocal(text)

    return text
  }

  fun writeLocalClipboard(text: String) {
    clipboardManager.setPrimaryClip(ClipData.newPlainText(CLIP_LABEL, text))
    recordLocal(text)
  }

  private fun recordLocal(text: String) {
    if (text.isBlank() || text.length > MAX_ENTRY_LENGTH) {
      return
    }

    _localHistory.update { current ->
      if (current.firstOrNull()?.text == text) {
        current
      } else {
        val entry = ClipboardEntry(UUID.randomUUID().toString(), text, System.currentTimeMillis())

        (listOf(entry) + current.filterNot { it.text == text }).take(MAX_HISTORY_ENTRIES)
      }
    }
  }

  private fun parseEntry(payload: String): ClipboardEntry? {
    val parts = payload.split(",", limit = 3)

    if (parts.size != 3) {
      return null
    }

    val timestamp = parts[1].toLongOrNull() ?: return null
    val text = runCatching { decode(parts[2]) }.getOrNull() ?: return null

    return ClipboardEntry(parts[0], text, timestamp)
  }

  private suspend fun sendTcpCommand(command: String) {
    val client = session.tcpClient ?: return

    try {
      client.send(command)
    } catch (exception: IOException) {
      System.err.println("Failed to send command: ${exception.message}")
    }
  }

  private fun encode(text: String): String =
    Base64.getEncoder().encodeToString(text.toByteArray(Charsets.UTF_8))

  private fun decode(payload: String): String =
    String(Base64.getDecoder().decode(payload), Charsets.UTF_8)

  private companion object {
    const val CLIP_LABEL = "Pairro"
    const val STATE_PREFIX = "CLIPBOARD_STATE:"
    const val ENTRY_PREFIX = "CLIPBOARD_ENTRY:"
    const val HISTORY_END = "CLIPBOARD_HISTORY_END"
    const val FETCH_TIMEOUT_MS = 3000
    const val MAX_LINES_PER_FETCH = 20
    const val MAX_LINES_PER_HISTORY_FETCH = 100
    const val MAX_HISTORY_ENTRIES = 30
    const val MAX_ENTRY_LENGTH = 20_000
  }
}