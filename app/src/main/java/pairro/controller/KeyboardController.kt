package pairro.controller

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import pairro.di.ApplicationScope
import pairro.di.IoDispatcher
import pairro.network.ConnectionSession
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KeyboardController @Inject constructor(
  private val session: ConnectionSession,
  @ApplicationScope private val scope: CoroutineScope,
  @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
  private val pendingKeyCombos = Channel<List<Int>>(Channel.UNLIMITED)

  init {
    scope.launch(ioDispatcher) {
      for (virtualKeyCodes in pendingKeyCombos) {
        sendTcpCommand("KEY_COMBO:${virtualKeyCodes.joinToString(",")}")
      }
    }
  }

  fun sendKey(virtualKeyCode: Int) {
    sendCombo(listOf(virtualKeyCode))
  }

  fun sendCombo(virtualKeyCodes: List<Int>) {
    if (virtualKeyCodes.isEmpty()) {
      return
    }

    pendingKeyCombos.trySend(virtualKeyCodes)
  }

  private suspend fun sendTcpCommand(command: String) {
    val client = session.tcpClient ?: return

    try {
      client.send(command)
    } catch (exception: IOException) {
      System.err.println("Failed to send key combo: ${exception.message}")
    }
  }
}