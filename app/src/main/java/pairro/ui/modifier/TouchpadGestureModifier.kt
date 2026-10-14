package pairro.ui.modifier

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import kotlin.math.roundToInt

fun Modifier.touchpadInput(
  sensitivity: Float = 1f,
  onMouseMove: (Int, Int) -> Unit,
  onLeftClick: () -> Unit,
  onRightClick: () -> Unit,
  onMouseLeftDown: () -> Unit,
  onMouseLeftUp: () -> Unit
): Modifier {
  return this.pointerInput(sensitivity) {
    detectTouchpadGestures(
      sensitivity = sensitivity,
      onMouseMove = onMouseMove,
      onLeftClick = onLeftClick,
      onRightClick = onRightClick,
      onMouseLeftDown = onMouseLeftDown,
      onMouseLeftUp = onMouseLeftUp
    )
  }
}

private suspend fun PointerInputScope.detectTouchpadGestures(
  sensitivity: Float,
  onMouseMove: (Int, Int) -> Unit,
  onLeftClick: () -> Unit,
  onRightClick: () -> Unit,
  onMouseLeftDown: () -> Unit,
  onMouseLeftUp: () -> Unit
) {
  val longPressTimeoutMillis = viewConfiguration.longPressTimeoutMillis
  val touchSlop = viewConfiguration.touchSlop

  awaitEachGesture {
    val down = awaitFirstDown(requireUnconsumed = false)
    down.consume()

    val downTimeMillis = System.currentTimeMillis()
    var totalMovement = 0f
    var isPlainMove = false
    var isClickDrag = false
    var secondPointerSeen = false

    var remainderX = 0f
    var remainderY = 0f

    while (true) {
      val event = awaitPointerEvent()
      val changes = event.changes

      if (changes.size > 1) {
        secondPointerSeen = true
      }

      val primary = changes.firstOrNull { it.id == down.id } ?: changes.first()

      if (!primary.pressed) {
        if (isClickDrag) {
          onMouseLeftUp()
        } else if (secondPointerSeen) {
          onRightClick()
        } else if (!isPlainMove && totalMovement < touchSlop) {
          onLeftClick()
        }
        break
      }

      val delta = primary.positionChange()
      primary.consume()

      if (!isPlainMove && !isClickDrag) {
        totalMovement += delta.getDistance()
        val heldLongEnough = System.currentTimeMillis() - downTimeMillis >= longPressTimeoutMillis

        if (heldLongEnough && totalMovement < touchSlop) {
          isClickDrag = true
          onMouseLeftDown()
        } else if (totalMovement >= touchSlop) {
          isPlainMove = true
        }
      }

      if (isPlainMove || isClickDrag) {
        val scaledX = delta.x * sensitivity + remainderX
        val scaledY = delta.y * sensitivity + remainderY
        val moveX = scaledX.roundToInt()
        val moveY = scaledY.roundToInt()

        remainderX = scaledX - moveX
        remainderY = scaledY - moveY

        if (moveX != 0 || moveY != 0) {
          onMouseMove(moveX, moveY)
        }
      }
    }
  }
}