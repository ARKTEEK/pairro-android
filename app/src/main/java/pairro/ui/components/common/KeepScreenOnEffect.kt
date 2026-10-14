package pairro.ui.components.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView

@Composable
fun KeepScreenOnEffect(enabled: Boolean) {
  val view = LocalView.current

  DisposableEffect(view, enabled) {
    val previousKeepScreenOn = view.keepScreenOn
    view.keepScreenOn = enabled

    onDispose {
      view.keepScreenOn = previousKeepScreenOn
    }
  }
}