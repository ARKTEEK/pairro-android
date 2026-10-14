package pairro.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import pairro.model.Macro
import pairro.store.MacroStore
import javax.inject.Inject

@HiltViewModel
class MacroViewModel @Inject constructor(
  private val macroStore: MacroStore
) : ViewModel() {
  private val _macros = mutableStateOf<List<Macro>>(emptyList())
  val macros: State<List<Macro>> = _macros

  init {
    _macros.value = macroStore.getAll()
  }

  fun addMacro(macro: Macro) {
    macroStore.add(macro)
    _macros.value = macroStore.getAll()
  }

  fun deleteMacro(macroId: String) {
    macroStore.remove(macroId)
    _macros.value = macroStore.getAll()
  }
}
