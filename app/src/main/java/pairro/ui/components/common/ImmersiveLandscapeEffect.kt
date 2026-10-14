package pairro.ui.components.common

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

@Composable
fun ImmersiveLandscapeEffect() {
  val context = LocalContext.current
  val activity = context as? Activity

  DisposableEffect(Unit) {
    val previousOrientation = activity?.requestedOrientation
    val window = activity?.window

    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

    if (window != null) {
      val insetsController = WindowCompat.getInsetsController(window, window.decorView)

      insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
      insetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

    onDispose {
      activity?.requestedOrientation = previousOrientation ?: ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

      if (window != null) {
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)

        insetsController.show(WindowInsetsCompat.Type.systemBars())
      }
    }
  }
}