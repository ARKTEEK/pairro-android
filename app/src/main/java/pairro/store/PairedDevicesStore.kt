package pairro.store

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PairedDesktopStore @Inject constructor(@ApplicationContext context: Context) {
  private val prefs = context.getSharedPreferences("pairro_paired", Context.MODE_PRIVATE)

  fun save(desktopId: String, desktopName: String, token: String) {
    prefs.edit {
      putString("$desktopId.name", desktopName)
      putString("$desktopId.token", token)
    }
  }

  fun find(desktopId: String): Pair<String, String>? {
    val name = prefs.getString("$desktopId.name", null) ?: return null
    val token = prefs.getString("$desktopId.token", null) ?: return null

    return name to token
  }

  fun getAll(): List<Triple<String, String, String>> {
    return prefs.all.keys
      .filter { it.endsWith(".name") }
      .mapNotNull { nameKey ->
        val desktopId = nameKey.removeSuffix(".name")
        val name = prefs.getString(nameKey, null)
        val token = prefs.getString("$desktopId.token", null)

        if (name != null && token != null) Triple(desktopId, name, token) else null
      }
  }

  fun remove(desktopId: String) {
    prefs.edit {
      remove("$desktopId.name")
      remove("$desktopId.token")
    }
  }
}