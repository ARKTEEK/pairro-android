package pairro.ui.components.controlpanel.keyboard.macro

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pairro.ui.theme.PairroColors
import pairro.model.Macro

@Composable
fun MacrosTab(
  macros: List<Macro>,
  onRun: (Macro) -> Unit,
  onDelete: (String) -> Unit,
  onAddClick: () -> Unit
) {
  LazyVerticalGrid(
    columns = GridCells.Adaptive(minSize = 110.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp),
    modifier = Modifier
      .fillMaxWidth()
      .heightIn(max = 200.dp)
  ) {
    items(macros, key = { it.id }) { macro ->
      MacroChip(macro = macro, onRun = { onRun(macro) }, onDelete = { onDelete(macro.id) })
    }

    item {
      OutlinedButton(
        onClick = onAddClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.outlinedButtonColors(
          containerColor = PairroColors.TealSoft,
          contentColor = PairroColors.TealDeep
        )
      ) {
        Icon(Icons.Outlined.Add, contentDescription = null)
        Spacer(modifier = Modifier.width(4.dp))
        Text("New")
      }
    }
  }
}