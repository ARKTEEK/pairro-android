package pairro.ui.components.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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

private const val MAX_NAME_LENGTH = 40

@Composable
fun DeviceNameDialog(
  currentName: String,
  systemName: String,
  onDismiss: () -> Unit,
  onSave: (String) -> Unit
) {
  var name by remember { mutableStateOf(currentName) }

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
          Text(
            text = "Device name",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
              .weight(1f)
              .padding(start = 4.dp)
          )

          IconButton(
            onClick = { onSave(name) },
            colors = IconButtonDefaults.iconButtonColors(contentColor = PairroColors.Magenta)
          ) {
            Icon(Icons.Outlined.Save, contentDescription = "Save name")
          }

          IconButton(onClick = onDismiss) {
            Icon(Icons.Outlined.Close, contentDescription = "Close")
          }
        }

        OutlinedTextField(
          value = name,
          onValueChange = {
            if (it.length <= MAX_NAME_LENGTH) {
              name = it
            }
          },
          label = { Text("Name") },
          placeholder = { Text(systemName, color = PairroColors.TextMuted) },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        Text(
          text = "Shown to other devices. Leave empty to use the system name.",
          style = MaterialTheme.typography.bodySmall,
          color = PairroColors.TextSecondary,
          modifier = Modifier.padding(horizontal = 4.dp)
        )

        TextButton(
          onClick = { onSave("") },
          colors = ButtonDefaults.textButtonColors(contentColor = PairroColors.Magenta)
        ) {
          Text("Use system name")
        }
      }
    }
  }
}