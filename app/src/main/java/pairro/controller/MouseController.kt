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

data class MousePosition(val deltaX: Int, val deltaY: Int)

@Singleton
class MouseController @Inject constructor(
  private val session: ConnectionSession,
  @ApplicationScope private val scope: CoroutineScope,
  @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
  private val pendingMoves = Channel<MousePosition>(Channel.CONFLATED)
  private val pendingTcpCommands = Channel<String>(Channel.UNLIMITED)

  init {
    scope.launch(ioDispatcher) {
      for (position in pendingMoves) {
        sendUdpCommand("MOUSE_MOVE:${position.deltaX},${position.deltaY}")
      }
    }

    scope.launch(ioDispatcher) {
      for (command in pendingTcpCommands) {
        sendTcpCommand(command)
      }
    }
  }

  fun sendMouseMove(deltaX: Int, deltaY: Int) {
    pendingMoves.trySend(MousePosition(deltaX, deltaY))
  }

  fun sendLeftClick() {
    pendingTcpCommands.trySend("MOUSE_LEFT_CLICK")
  }

  fun sendRightClick() {
    pendingTcpCommands.trySend("MOUSE_RIGHT_CLICK")
  }

  fun sendLeftDown() {
    pendingTcpCommands.trySend("MOUSE_LEFT_DOWN")
  }

  fun sendLeftUp() {
    pendingTcpCommands.trySend("MOUSE_LEFT_UP")
  }

  private suspend fun sendTcpCommand(command: String) {
    val client = session.tcpClient ?: return

    try {
      client.send(command)
    } catch (exception: IOException) {
      System.err.println("Failed to send command: ${exception.message}")
    }
  }

  private fun sendUdpCommand(command: String) {
    val client = session.udpClient ?: return

    client.send(command)
  }
}