package pairro.device

import android.content.Context
import android.os.Build
import android.provider.Settings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import pairro.store.SettingsStore
import javax.inject.Inject
import javax.inject.Singleton

private const val MAX_SERVICE_NAME_BYTES = 63

@Singleton
class DeviceInfo @Inject constructor(
  @ApplicationContext private val context: Context,
  private val settingsStore: SettingsStore
) {
  private val _deviceName = MutableStateFlow(resolveDeviceName())

  val deviceName: StateFlow<String> = _deviceName.asStateFlow()

  fun getDeviceName(): String = _deviceName.value

  fun setCustomDeviceName(name: String) {
    settingsStore.deviceName = name.trim()
    _deviceName.value = resolveDeviceName()
  }

  fun getSystemDeviceName(): String {
    var deviceName = Settings.Global.getString(context.contentResolver, Settings.Global.DEVICE_NAME)

    if (deviceName.isNullOrBlank()) {
      deviceName = Settings.Secure.getString(context.contentResolver, "bluetooth_name")
    }

    if (deviceName.isNullOrBlank()) {
      val manufacturer = Build.MANUFACTURER
      val model = Build.MODEL

      deviceName = if (model.lowercase().startsWith(manufacturer.lowercase())) {
        model
      } else {
        "$manufacturer $model"
      }
    }

    return deviceName
  }

  private fun resolveDeviceName(): String {
    val custom = settingsStore.deviceName.trim()

    return if (custom.isBlank()) {
      getSystemDeviceName()
    } else {
      custom.truncateUtf8(MAX_SERVICE_NAME_BYTES)
    }
  }

  private fun String.truncateUtf8(maxBytes: Int): String {
    var result = this

    while (result.toByteArray(Charsets.UTF_8).size > maxBytes) {
      result = result.dropLast(1)
    }

    return result
  }
}
