package pairro.ui.components.controlpanel.keyboard.macro

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import pairro.ui.theme.PairroColors
import pairro.model.Macro

@Composable
fun MacroChip(
  macro: Macro,
  onRun: () -> Unit,
  onDelete: () -> Unit
) {
  var showDelete by remember { mutableStateOf(false) }

  Box {
    OutlinedButton(
      onClick = onRun,
      modifier = Modifier.fillMaxWidth(),
      colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
        containerColor = PairroColors.PinkSoft,
        contentColor = PairroColors.Wine
      ),
      border = BorderStroke(1.dp, PairroColors.AccentPrimary)
    ) {
      Text(
        text = macro.name,
        maxLines = 1,
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier.padding(end = 24.dp)
      )
    }

    IconButton(
      onClick = { showDelete = true },
      modifier = Modifier
        .align(Alignment.CenterEnd)
        .size(40.dp)
    ) {
      Icon(
        imageVector = Icons.Outlined.Delete,
        contentDescription = "Delete macro",
        tint = PairroColors.Magenta,
        modifier = Modifier.size(20.dp)
      )
    }
  }

  if (showDelete) {
    AlertDialog(
      onDismissRequest = { showDelete = false },
      title = { Text("Delete \"${macro.name}\"?") },
      confirmButton = {
        TextButton(onClick = {
          onDelete()
          showDelete = false
        }) { Text("Delete", color = PairroColors.Danger) }
      },
      dismissButton = {
        TextButton(onClick = { showDelete = false }) { Text("Cancel") }
      }
    )
  }
}
