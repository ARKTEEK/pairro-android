package pairro.network.mdns

import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import dagger.hilt.android.scopes.ViewModelScoped
import pairro.device.DeviceIdentity
import javax.inject.Inject

@ViewModelScoped
class DeviceBroadcaster @Inject constructor(
  private val nsdManager: NsdManager,
  identity: DeviceIdentity
) {

  private val deviceId = identity.getId()

  private val serviceType = "_pairro._tcp."

  private var registrationListener: NsdManager.RegistrationListener? = null

  private var isRegistered = false

  fun start(deviceName: String, port: Int) {
    if (registrationListener != null) {
      return
    }

    val serviceInfo = NsdServiceInfo().apply {
      serviceName = deviceName
      serviceType = this@DeviceBroadcaster.serviceType
      this.port = port
      this.setAttribute("device_id", deviceId)
    }

    registrationListener =
      object : NsdManager.RegistrationListener {

        override fun onServiceRegistered(
          nsdServiceInfo: NsdServiceInfo
        ) {
          isRegistered = true
        }

        override fun onRegistrationFailed(
          nsdServiceInfo: NsdServiceInfo,
          errorCode: Int
        ) {
          isRegistered = false
          registrationListener = null
        }

        override fun onServiceUnregistered(
          nsdServiceInfo: NsdServiceInfo
        ) {
          isRegistered = false
          registrationListener = null
        }

        override fun onUnregistrationFailed(
          nsdServiceInfo: NsdServiceInfo,
          errorCode: Int
        ) {
          isRegistered = false
          registrationListener = null
        }
      }

    nsdManager.registerService(
      serviceInfo,
      NsdManager.PROTOCOL_DNS_SD,
      registrationListener!!
    )
  }

  fun stop() {
    val listener = registrationListener ?: return

    try {
      nsdManager.unregisterService(listener)
    } catch (_: IllegalArgumentException) {
    }

    isRegistered = false
    registrationListener = null
  }
}