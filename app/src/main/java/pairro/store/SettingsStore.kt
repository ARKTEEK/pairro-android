package pairro.store

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private const val PREFS_NAME = "pairro_settings"
private const val KEY_AUTO_START_DISCOVERABILITY = "auto_start_discoverability"
private const val KEY_DEVICE_NAME = "device_name"
private const val KEY_TOUCHPAD_SENSITIVITY = "touchpad_sensitivity"
private const val KEY_HAPTIC_FEEDBACK = "haptic_feedback"
private const val KEY_KEEP_SCREEN_ON = "keep_screen_on"
private const val KEY_CONFIRM_UNPAIR = "confirm_unpair"

@Singleton
class SettingsStore @Inject constructor(@ApplicationContext context: Context) {
  private val prefs =
    context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

  var autoStartDiscoverability: Boolean
    get() = prefs.getBoolean(KEY_AUTO_START_DISCOVERABILITY, false)
    set(value) = prefs.edit { putBoolean(KEY_AUTO_START_DISCOVERABILITY, value) }

  var deviceName: String
    get() = prefs.getString(KEY_DEVICE_NAME, "") ?: ""
    set(value) = prefs.edit { putString(KEY_DEVICE_NAME, value) }

  var touchpadSensitivity: Float
    get() = prefs.getFloat(KEY_TOUCHPAD_SENSITIVITY, 1.0f)
    set(value) = prefs.edit { putFloat(KEY_TOUCHPAD_SENSITIVITY, value) }

  var keepScreenOn: Boolean
    get() = prefs.getBoolean(KEY_KEEP_SCREEN_ON, true)
    set(value) = prefs.edit { putBoolean(KEY_KEEP_SCREEN_ON, value) }

  var confirmUnpair: Boolean
    get() = prefs.getBoolean(KEY_CONFIRM_UNPAIR, true)
    set(value) = prefs.edit { putBoolean(KEY_CONFIRM_UNPAIR, value) }
}
