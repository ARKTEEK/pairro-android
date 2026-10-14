package pairro.network.mdns

import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.net.wifi.WifiManager
import dagger.hilt.android.scopes.ViewModelScoped
import pairro.device.DeviceIdentity
import javax.inject.Inject

@ViewModelScoped
class DeviceScanner @Inject constructor(
  private val nsdManager: NsdManager,
  private val wifiManager: WifiManager,
  identity: DeviceIdentity
) {
  private val deviceId = identity.getId()
  private var multicastLock: WifiManager.MulticastLock? = null
  private val serviceType = "_pairro._tcp"
  private var listener: NsdManager.DiscoveryListener? = null

  fun start(onDeviceDiscovered: (String, String, String) -> Unit) {
    if (listener != null) {
      return
    }

    multicastLock = wifiManager.createMulticastLock("PairroMulticastLock").apply {
      setReferenceCounted(true)
      acquire()
    }

    listener = object : NsdManager.DiscoveryListener {
      override fun onDiscoveryStarted(serviceType: String) {}
      override fun onDiscoveryStopped(serviceType: String) {}

      override fun onServiceFound(service: NsdServiceInfo) {
        if (!service.serviceType.contains(serviceType)) {
          return
        }

        nsdManager.resolveService(
          service,
          object : NsdManager.ResolveListener {
            override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {}

            override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
              val remoteId = serviceInfo.attributes["device_id"]?.decodeToString()

              if (deviceId == remoteId) {
                return
              }

              if (remoteId == null) {
                return
              }

              val address = serviceInfo.host?.hostAddress ?: return

              onDeviceDiscovered(
                remoteId,
                serviceInfo.serviceName,
                address
              )
            }
          }
        )
      }

      override fun onServiceLost(service: NsdServiceInfo) {}
      override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
        stop()
      }

      override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
        stop()
      }
    }

    nsdManager.discoverServices(
      serviceType,
      NsdManager.PROTOCOL_DNS_SD,
      listener!!
    )
  }

  fun stop() {
    if (listener == null) {
      return
    }

    nsdManager.stopServiceDiscovery(listener)
    listener = null

    if (multicastLock != null) {
      multicastLock?.release()
      multicastLock = null
    }
  }
}