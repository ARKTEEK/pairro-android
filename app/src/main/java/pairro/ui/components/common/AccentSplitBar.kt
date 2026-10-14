package pairro.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import pairro.ui.theme.PairroColors

@Composable
fun AccentSplitBar(
  modifier: Modifier = Modifier,
  height: Dp = 8.dp,
  color: Color = PairroColors.AccentPrimary
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .then(modifier)
      .height(height)
      .clip(RoundedCornerShape(999.dp))
      .background(color)
  )
}