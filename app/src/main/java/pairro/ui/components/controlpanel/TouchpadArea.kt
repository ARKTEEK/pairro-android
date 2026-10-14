package pairro.ui.components.controlpanel

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import pairro.ui.theme.PairroColors
import pairro.controller.MouseController
import pairro.ui.modifier.touchpadInput

@Composable
fun TouchpadArea(
  mouseController: MouseController,
  sensitivity: Float = 1f,
  hapticFeedback: Boolean = true,
  modifier: Modifier = Modifier
) {
  val shape = RoundedCornerShape(20.dp)
  val context = LocalContext.current
  val vibrator = remember(context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
      vibratorManager.defaultVibrator
    } else {
      @Suppress("DEPRECATION")
      context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }
  }
  val hapticsEnabled by rememberUpdatedState(hapticFeedback)

  fun vibrate() {
    if (hapticsEnabled && vibrator.hasVibrator()) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
      } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(20L)
      }
    }
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .border(1.5.dp, PairroColors.BorderStrong, shape)
      .clip(shape)
      .background(PairroColors.Card.copy(alpha = 0.72f))
      .touchpadInput(
        sensitivity = sensitivity,
        onMouseMove = mouseController::sendMouseMove,
        onLeftClick = {
          vibrate()
          mouseController.sendLeftClick()
        },
        onRightClick = {
          vibrate()
          mouseController.sendRightClick()
        },
        onMouseLeftDown = {
          vibrate()
          mouseController.sendLeftDown()
        },
        onMouseLeftUp = mouseController::sendLeftUp
      )
  ) {
    Text(
      text = "Touchpad",
      style = MaterialTheme.typography.labelSmall,
      color = PairroColors.Magenta,
      modifier = Modifier.padding(16.dp)
    )
  }
}