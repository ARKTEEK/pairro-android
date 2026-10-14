package pairro.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.pairro.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pairro.controller.DeviceController
import pairro.di.IoDispatcher
import pairro.network.ConnectionSession
import pairro.protocol.Protocol
import pairro.ui.MainActivity
import javax.inject.Inject

@AndroidEntryPoint
class ConnectionForegroundService : Service() {

  @Inject
  lateinit var session: ConnectionSession

  @Inject
  lateinit var deviceController: DeviceController

  @Inject
  @IoDispatcher
  lateinit var ioDispatcher: CoroutineDispatcher

  private val serviceScope = CoroutineScope(SupervisorJob())
  private var keepAliveJob: Job? = null

  override fun onBind(intent: Intent?): IBinder? = null

  override fun onCreate() {
    super.onCreate()
    Log.d(TAG, "onCreate")
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    Log.d(TAG, "onStartCommand action=${intent?.action}")

    if (intent?.action == ACTION_DISCONNECT) {
      deviceController.disconnect()
      stopSelf()

      return START_NOT_STICKY
    }

    val deviceName = intent?.getStringExtra(EXTRA_DEVICE_NAME)
      ?: getString(R.string.connection_notification_fallback_device)

    if (!startForegroundWithNotification(deviceName)) {
      stopSelf()

      return START_NOT_STICKY
    }

    startKeepAlive()

    return START_STICKY
  }

  private fun startForegroundWithNotification(deviceName: String): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = NotificationChannel(
        CHANNEL_ID,
        getString(R.string.connection_channel_name),
        NotificationManager.IMPORTANCE_LOW
      )

      getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    val openAppIntent = PendingIntent.getActivity(
      this,
      0,
      Intent(this, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
      },
      PendingIntent.FLAG_IMMUTABLE
    )

    val disconnectIntent = Intent(this, ConnectionForegroundService::class.java).apply {
      action = ACTION_DISCONNECT
    }
    val disconnectPendingIntent = PendingIntent.getService(
      this,
      0,
      disconnectIntent,
      PendingIntent.FLAG_IMMUTABLE
    )

    val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
      .setContentTitle(getString(R.string.connection_notification_title))
      .setContentText(getString(R.string.connection_notification_text, deviceName))
      .setSmallIcon(R.mipmap.ic_launcher)
      .setContentIntent(openAppIntent)
      .setOngoing(true)
      .setOnlyAlertOnce(true)
      .addAction(
        android.R.drawable.ic_menu_close_clear_cancel,
        getString(R.string.connection_notification_disconnect_action),
        disconnectPendingIntent
      )
      .build()

    return try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        startForeground(
          NOTIFICATION_ID,
          notification,
          ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE
        )
      } else {
        startForeground(NOTIFICATION_ID, notification)
      }

      Log.d(TAG, "startForeground succeeded")
      true
    } catch (e: Exception) {
      Log.e(TAG, "startForeground failed", e)
      false
    }
  }

  private fun startKeepAlive() {
    if (keepAliveJob?.isActive == true) {
      return
    }

    keepAliveJob = serviceScope.launch(ioDispatcher) {
      while (true) {
        val tcpClient = session.tcpClient

        if (tcpClient == null) {
          Log.d(TAG, "No active TCP client; stopping keep-alive")
          stopSelf()
          return@launch
        }

        try {
          tcpClient.send(Protocol.encode("PING"))
        } catch (e: Exception) {
          Log.w(TAG, "PING send failed: ${e.message}")
        }

        session.udpClient?.send(Protocol.encode("PING"))

        delay(KEEP_ALIVE_INTERVAL_MS)
      }
    }
  }

  override fun onDestroy() {
    Log.d(TAG, "onDestroy")

    keepAliveJob?.cancel()
    serviceScope.cancel()

    super.onDestroy()
  }

  companion object {
    private const val TAG = "ConnectionFGS"
    private const val CHANNEL_ID = "connection_status"
    private const val NOTIFICATION_ID = 42
    private const val KEEP_ALIVE_INTERVAL_MS = 15_000L
    private const val EXTRA_DEVICE_NAME = "device_name"
    private const val ACTION_DISCONNECT = "pairro.service.action.DISCONNECT"

    fun start(context: Context, deviceName: String) {
      val intent = Intent(context, ConnectionForegroundService::class.java)
        .putExtra(EXTRA_DEVICE_NAME, deviceName)

      context.startForegroundService(intent)
    }

    fun stop(context: Context) {
      context.stopService(Intent(context, ConnectionForegroundService::class.java))
    }
  }
}