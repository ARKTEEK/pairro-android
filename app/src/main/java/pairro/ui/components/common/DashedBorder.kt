package pairro.ui.components.common

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.dashedBorder(
  color: Color,
  cornerRadius: Dp,
  strokeWidth: Dp = 1.dp,
  dash: Dp = 5.dp,
  gap: Dp = 4.dp
): Modifier = drawBehind {
  val width = strokeWidth.toPx()

  drawRoundRect(
    color = color,
    topLeft = Offset(width / 2, width / 2),
    size = Size(size.width - width, size.height - width),
    cornerRadius = CornerRadius(cornerRadius.toPx()),
    style = Stroke(
      width = width,
      pathEffect = PathEffect.dashPathEffect(floatArrayOf(dash.toPx(), gap.toPx()))
    )
  )
}
