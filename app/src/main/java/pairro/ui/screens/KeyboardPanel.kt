package pairro.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.KeyboardAlt
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LeadingIconTab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pairro.ui.theme.PairroColors
import pairro.controller.KeyboardController
import pairro.model.Macro
import pairro.ui.components.controlpanel.keyboard.SystemKeyboardInput
import pairro.ui.components.controlpanel.keyboard.macro.AddMacroDialog
import pairro.ui.components.controlpanel.keyboard.macro.MacrosTab

private enum class KeyboardTab { MACROS, TYPE }

@Composable
fun KeyboardPanel(
  keyboardController: KeyboardController,
  macros: List<Macro>,
  onAddMacro: (Macro) -> Unit,
  onDeleteMacro: (String) -> Unit,
  onClose: () -> Unit,
  modifier: Modifier = Modifier
) {
  var tab by remember { mutableStateOf(KeyboardTab.TYPE) }
  var showAddMacroDialog by remember { mutableStateOf(false) }

  Column(
    modifier = modifier
      .clip(RoundedCornerShape(16.dp))
      .background(PairroColors.Card.copy(alpha = 0.97f))
      .border(1.dp, PairroColors.BorderSubtle, RoundedCornerShape(16.dp))
      .padding(12.dp)
  ) {
    KeyboardPanelHeader(
      selectedTab = tab,
      onSelectTab = { tab = it },
      onClose = onClose
    )

    Spacer(modifier = Modifier.height(8.dp))

    if (tab == KeyboardTab.MACROS) {
      MacrosTab(
        macros = macros,
        onRun = { keyboardController.sendCombo(it.keyCodes) },
        onDelete = onDeleteMacro,
        onAddClick = { showAddMacroDialog = true }
      )
    } else {
      SystemKeyboardInput(keyboardController = keyboardController)
    }
  }

  if (showAddMacroDialog) {
    AddMacroDialog(
      onDismiss = { showAddMacroDialog = false },
      onSave = { macro ->
        onAddMacro(macro)
        showAddMacroDialog = false
      }
    )
  }
}

@Composable
private fun KeyboardPanelHeader(
  selectedTab: KeyboardTab,
  onSelectTab: (KeyboardTab) -> Unit,
  onClose: () -> Unit
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
    modifier = Modifier.fillMaxWidth()
  ) {
    TabRow(
      selectedTabIndex = selectedTab.ordinal,
      modifier = Modifier.weight(1f),
      containerColor = Color.Transparent,
      contentColor = PairroColors.Magenta,
      indicator = { tabPositions ->
        TabRowDefaults.SecondaryIndicator(
          modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab.ordinal]),
          color = PairroColors.AccentSecondary
        )
      }
    ) {
      LeadingIconTab(
        selected = selectedTab == KeyboardTab.MACROS,
        onClick = { onSelectTab(KeyboardTab.MACROS) },
        text = { Text("Macros") },
        icon = { Icon(Icons.Outlined.Widgets, contentDescription = null) }
      )
      LeadingIconTab(
        selected = selectedTab == KeyboardTab.TYPE,
        onClick = { onSelectTab(KeyboardTab.TYPE) },
        text = { Text("Type") },
        icon = { Icon(Icons.Outlined.KeyboardAlt, contentDescription = null) }
      )
    }

    IconButton(onClick = onClose) {
      Icon(Icons.Outlined.Close, contentDescription = "Close keyboard")
    }
  }
}