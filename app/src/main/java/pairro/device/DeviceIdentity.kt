package pairro.device

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceIdentity @Inject constructor(@ApplicationContext context: Context) {

  private val prefs = context.getSharedPreferences("pairro", Context.MODE_PRIVATE)

  fun getId(): String {
    val existing = prefs.getString("device_id", null)

    if (existing != null) {
      return existing
    }

    val newId = UUID.randomUUID().toString()
    prefs.edit { putString("device_id", newId) }

    return newId
  }
}