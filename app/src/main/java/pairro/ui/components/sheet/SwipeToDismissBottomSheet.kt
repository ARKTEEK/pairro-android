package pairro.ui.components.sheet

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import pairro.ui.theme.PairroColors
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun SwipeToDismissBottomSheet(
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier,
  content: @Composable BoxScope.() -> Unit
) {
  val coroutineScope = rememberCoroutineScope()
  val offsetY = remember { Animatable(0f) }
  val dismissThreshold = 150f
  val noRippleSource = remember { MutableInteractionSource() }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0x66000000))
      .clickable(indication = null, interactionSource = noRippleSource, onClick = onDismiss)
  ) {
    Surface(
      color = PairroColors.Blush,
      shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .fillMaxWidth()
        .offset { IntOffset(0, offsetY.value.roundToInt()) }
        .clickable(indication = null, interactionSource = noRippleSource) {}
        .pointerInput(Unit) {
          detectVerticalDragGestures(
            onDragEnd = {
              if (offsetY.value > dismissThreshold) {
                onDismiss()
              } else {
                coroutineScope.launch {
                  offsetY.animateTo(0f)
                }
              }
            },
            onDragCancel = {
              coroutineScope.launch {
                offsetY.animateTo(0f)
              }
            },
            onVerticalDrag = { change, dragAmount ->
              change.consume()

              val newOffset = (offsetY.value + dragAmount).coerceAtLeast(0f)

              coroutineScope.launch {
                offsetY.snapTo(newOffset)
              }
            }
          )
        }
    ) {
      Box(modifier = Modifier.fillMaxWidth()) {
        Box(
          modifier = Modifier
            .align(Alignment.TopCenter)
            .padding(top = 10.dp)
            .width(36.dp)
            .height(4.dp)
            .background(PairroColors.AccentPrimary, RoundedCornerShape(999.dp))
        )

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
        ) {
          content()
        }
      }
    }
  }
}