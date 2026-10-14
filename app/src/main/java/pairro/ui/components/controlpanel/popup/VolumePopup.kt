package pairro.ui.components.controlpanel.popup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.VolumeOff
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pairro.ui.theme.PairroColors
import pairro.ui.components.common.IconButtonTone
import pairro.ui.components.common.IconRoundButton
import kotlin.math.roundToInt

private const val MIN_VOLUME = 0
private const val MAX_VOLUME = 100
private const val VOLUME_STEP = 2
private const val MAX_INPUT_LENGTH = 3

@Composable
fun VolumePopup(
  volume: Int,
  isMuted: Boolean,
  onVolumeChange: (Int) -> Unit,
  onMuteChange: (Boolean) -> Unit,
  modifier: Modifier = Modifier
) {
  val focusManager = LocalFocusManager.current
  var inputText by remember(volume) { mutableStateOf(volume.toString()) }

  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    modifier = modifier
      .widthIn(min = 220.dp)
      .clip(RoundedCornerShape(16.dp))
      .background(PairroColors.Card.copy(alpha = 0.97f))
      .border(1.dp, PairroColors.BorderSubtle, RoundedCornerShape(16.dp))
      .padding(horizontal = 14.dp, vertical = 8.dp)
  ) {
    IconRoundButton(
      icon = if (isMuted) {
        Icons.AutoMirrored.Outlined.VolumeOff
      } else {
        Icons.AutoMirrored.Outlined.VolumeUp
      },
      contentDescription = if (isMuted) "Unmute" else "Mute",
      onClick = { onMuteChange(!isMuted) },
      active = isMuted,
      tone = IconButtonTone.Danger
    )

    IconRoundButton(
      icon = Icons.Outlined.Remove,
      contentDescription = "Decrease volume",
      onClick = { onVolumeChange((volume - VOLUME_STEP).coerceAtLeast(MIN_VOLUME)) }
    )

    Slider(
      value = volume.toFloat(),
      onValueChange = { newValue -> onVolumeChange(newValue.roundToInt()) },
      valueRange = MIN_VOLUME.toFloat()..MAX_VOLUME.toFloat(),
      colors = SliderDefaults.colors(
        thumbColor = PairroColors.AccentPrimary,
        activeTrackColor = PairroColors.AccentPrimary,
        inactiveTrackColor = PairroColors.PinkSoft
      ),
      modifier = Modifier.width(140.dp)
    )

    IconRoundButton(
      icon = Icons.Outlined.Add,
      contentDescription = "Increase volume",
      onClick = { onVolumeChange((volume + VOLUME_STEP).coerceAtMost(MAX_VOLUME)) }
    )

    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier
        .width(56.dp)
        .height(40.dp)
        .clip(RoundedCornerShape(8.dp))
        .background(PairroColors.Blush)
        .border(1.dp, PairroColors.BorderSubtle, RoundedCornerShape(8.dp))
    ) {
      BasicTextField(
        value = inputText,
        onValueChange = { newText ->
          val digits = newText.filter { character -> character.isDigit() }.take(MAX_INPUT_LENGTH)
          val parsedVolume = digits.toIntOrNull()

          if (parsedVolume == null) {
            inputText = digits
          } else {
            val clampedVolume = parsedVolume.coerceIn(MIN_VOLUME, MAX_VOLUME)

            inputText = clampedVolume.toString()

            if (clampedVolume != volume) {
              onVolumeChange(clampedVolume)
            }
          }
        },
        singleLine = true,
        textStyle = TextStyle(
          color = PairroColors.TextPrimary,
          fontSize = 14.sp,
          textAlign = TextAlign.Center
        ),
        keyboardOptions = KeyboardOptions(
          keyboardType = KeyboardType.Number,
          imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        cursorBrush = SolidColor(PairroColors.AccentPrimary),
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 6.dp)
          .onFocusChanged { focusState ->
            if (!focusState.isFocused && inputText.isEmpty()) {
              inputText = volume.toString()
            }
          }
      )
    }
  }
}