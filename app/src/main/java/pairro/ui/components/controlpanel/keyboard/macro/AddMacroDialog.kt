package pairro.ui.components.controlpanel.keyboard.macro

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import pairro.ui.theme.PairroColors
import pairro.model.KeyCodes
import pairro.model.Macro
import java.util.UUID

private data class KeyOption(val label: String, val vkCode: Int)

private val MACRO_KEY_OPTIONS: List<KeyOption> = buildList {
  add(KeyOption("Enter", KeyCodes.VK_ENTER))
  add(KeyOption("Esc", KeyCodes.VK_ESCAPE))
  add(KeyOption("Tab", KeyCodes.VK_TAB))
  add(KeyOption("Space", KeyCodes.VK_SPACE))
  add(KeyOption("Backspace", KeyCodes.VK_BACK_SPACE))
  add(KeyOption("Delete", KeyCodes.VK_DELETE))
  add(KeyOption("↑", KeyCodes.VK_UP))
  add(KeyOption("↓", KeyCodes.VK_DOWN))
  add(KeyOption("←", KeyCodes.VK_LEFT))
  add(KeyOption("→", KeyCodes.VK_RIGHT))

  for (c in 'A'..'Z') {
    add(KeyOption(c.toString(), KeyCodes.vkForLetter(c)))
  }

  for (d in 0..9) {
    add(KeyOption(d.toString(), KeyCodes.vkForDigit(d)))
  }

  for (f in 1..12) {
    add(KeyOption("F$f", KeyCodes.vkForFunctionKey(f)))
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddMacroDialog(
  onDismiss: () -> Unit,
  onSave: (Macro) -> Unit
) {
  var name by remember { mutableStateOf("") }
  var useCtrl by remember { mutableStateOf(true) }
  var useAlt by remember { mutableStateOf(false) }
  var useShift by remember { mutableStateOf(false) }
  var useMeta by remember { mutableStateOf(false) }
  var selectedKey by remember { mutableStateOf(MACRO_KEY_OPTIONS.first { it.label == "C" }) }
  var expanded by remember { mutableStateOf(false) }

  val save: () -> Unit = {
    val codes = buildList {
      if (useCtrl) {
        add(KeyCodes.VK_CONTROL)
      }

      if (useAlt) {
        add(KeyCodes.VK_ALT)
      }

      if (useShift) {
        add(KeyCodes.VK_SHIFT)
      }

      if (useMeta) {
        add(KeyCodes.VK_WINDOWS)
      }

      add(selectedKey.vkCode)
    }

    onSave(Macro(id = UUID.randomUUID().toString(), name = name.trim(), keyCodes = codes))
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      shape = RoundedCornerShape(16.dp),
      color = PairroColors.Card.copy(alpha = 0.97f),
      contentColor = PairroColors.TextPrimary,
      border = BorderStroke(1.dp, PairroColors.BorderSubtle),
      modifier = Modifier
        .padding(horizontal = 16.dp)
        .widthIn(max = 520.dp)
    ) {
      Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(12.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
              .weight(1f)
              .padding(start = 4.dp)
          ) {
            Icon(Icons.Outlined.Widgets, contentDescription = null)

            Text(
              text = "New Macro",
              style = MaterialTheme.typography.titleMedium
            )
          }

          IconButton(
            enabled = name.isNotBlank(),
            onClick = save,
            colors = IconButtonDefaults.iconButtonColors(
              contentColor = PairroColors.Magenta,
              disabledContentColor = PairroColors.TextMuted
            )
          ) {
            Icon(Icons.Outlined.Save, contentDescription = "Save macro")
          }

          IconButton(onClick = onDismiss) {
            Icon(Icons.Outlined.Close, contentDescription = "Close")
          }
        }

        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Name") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        Row(
          horizontalArrangement = Arrangement.spacedBy(16.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Modifiers",
              style = MaterialTheme.typography.labelMedium,
              color = PairroColors.TextSecondary
            )

            FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
              FilterChip(
                selected = useCtrl,
                onClick = { useCtrl = !useCtrl },
                label = { Text("Ctrl") })
              FilterChip(selected = useAlt, onClick = { useAlt = !useAlt }, label = { Text("Alt") })
              FilterChip(
                selected = useShift,
                onClick = { useShift = !useShift },
                label = { Text("Shift") })
              FilterChip(
                selected = useMeta,
                onClick = { useMeta = !useMeta },
                label = { Text("Win") })
            }
          }

          Column(modifier = Modifier.width(120.dp)) {
            Text(
              text = "Key",
              style = MaterialTheme.typography.labelMedium,
              color = PairroColors.TextSecondary
            )

            Box(modifier = Modifier.fillMaxWidth()) {
              OutlinedButton(
                onClick = { expanded = true },
                shape = FilterChipDefaults.shape,
                modifier = Modifier.fillMaxWidth()
              ) {
                Text(selectedKey.label, maxLines = 1)
              }

              DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                MACRO_KEY_OPTIONS.forEach { option ->
                  DropdownMenuItem(
                    text = { Text(option.label) },
                    onClick = {
                      selectedKey = option
                      expanded = false
                    }
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}