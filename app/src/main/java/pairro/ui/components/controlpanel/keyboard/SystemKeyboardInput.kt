package pairro.ui.components.controlpanel.keyboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import pairro.ui.theme.PairroColors
import kotlinx.coroutines.delay
import pairro.controller.KeyboardController
import pairro.model.KeyCodes
import kotlin.time.Duration.Companion.milliseconds

private const val KEYBOARD_OPEN_DELAY_MS = 150L
private const val ANCHOR = "\u200B"
private val ANCHOR_VALUE = TextFieldValue(ANCHOR, selection = TextRange(ANCHOR.length))

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SystemKeyboardInput(keyboardController: KeyboardController) {
  var value by remember { mutableStateOf(ANCHOR_VALUE) }
  val focusRequester = remember { FocusRequester() }
  val softKeyboard = LocalSoftwareKeyboardController.current
  val isImeVisible = WindowInsets.isImeVisible

  LaunchedEffect(Unit) {
    focusRequester.requestFocus()
    delay(KEYBOARD_OPEN_DELAY_MS.milliseconds)
    softKeyboard?.show()
  }

  DisposableEffect(Unit) {
    onDispose {
      softKeyboard?.hide()
    }
  }

  Box(modifier = Modifier.fillMaxWidth()) {
    if (!isImeVisible) {
      Button(
        onClick = {
          focusRequester.requestFocus()
          softKeyboard?.show()
        },
        modifier = Modifier.fillMaxWidth()
      ) {
        Text("Open Native Keyboard")
      }
    }

    BasicTextField(
      value = value,
      onValueChange = { newValue ->
        val diff = newValue.text.length - value.text.length

        if (diff > 0) {
          newValue.text.takeLast(diff).forEach { c ->
            sendChar(keyboardController, c)
          }
        } else if (diff < 0) {
          repeat(-diff) {
            keyboardController.sendKey(KeyCodes.VK_BACK_SPACE)
          }
        }

        value = ANCHOR_VALUE
      },
      keyboardOptions = KeyboardOptions(imeAction = ImeAction.None),
      cursorBrush = SolidColor(PairroColors.AccentPrimary),
      modifier = Modifier
        .fillMaxWidth()
        .height(1.dp)
        .alpha(0f)
        .focusRequester(focusRequester)
    )
  }
}

internal fun sendChar(keyboardController: KeyboardController, c: Char) {
  val result = KeyCodes.forChar(c) ?: return

  val (vkCode, needsShift) = result

  if (needsShift) {
    keyboardController.sendCombo(listOf(KeyCodes.VK_SHIFT, vkCode))
  } else {
    keyboardController.sendKey(vkCode)
  }
}