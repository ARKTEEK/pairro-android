package pairro.ui.components.controlpanel.popup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Computer
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pairro.ui.theme.PairroColors
import pairro.model.ClipboardEntry
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private enum class HistoryTab(
  val label: String,
  val icon: ImageVector,
  val hint: String,
  val emptyText: String
) {
  PC(
    label = "PC",
    icon = Icons.Outlined.Computer,
    hint = "Tap an item to copy it to your phone",
    emptyText = "Copy something on your PC and it shows up here"
  ),
  PHONE(
    label = "Phone",
    icon = Icons.Outlined.PhoneAndroid,
    hint = "Tap an item to send it to your PC",
    emptyText = "Copy something on your phone and it shows up here"
  )
}

@Composable
fun ClipboardPopup(
  draft: String,
  onDraftChange: (String) -> Unit,
  desktopHistory: List<ClipboardEntry>,
  phoneHistory: List<ClipboardEntry>,
  onCopyToPhone: (ClipboardEntry) -> Unit,
  onSendToDesktop: (ClipboardEntry) -> Unit,
  onSaveDraftToPhone: () -> Unit,
  onSaveDraftToDesktop: () -> Unit,
  onClearDesktopHistory: () -> Unit,
  onClearPhoneHistory: () -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableStateOf(HistoryTab.PC) }

  val isPcTab = selectedTab == HistoryTab.PC
  val entries = if (isPcTab) desktopHistory else phoneHistory
  val canSave = draft.isNotBlank()

  Column(
    verticalArrangement = Arrangement.spacedBy(8.dp),
    modifier = modifier
      .width(300.dp)
      .clip(RoundedCornerShape(16.dp))
      .background(PairroColors.Card.copy(alpha = 0.97f))
      .border(1.dp, PairroColors.BorderSubtle, RoundedCornerShape(16.dp))
      .verticalScroll(rememberScrollState())
      .padding(12.dp)
  ) {
    Row(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .fillMaxWidth()
        .height(36.dp)
    ) {
      Row(
        modifier = Modifier
          .weight(1f)
          .fillMaxHeight()
          .clip(RoundedCornerShape(10.dp))
          .background(PairroColors.Blush)
          .border(1.dp, PairroColors.BorderSubtle, RoundedCornerShape(10.dp))
          .padding(2.dp)
      ) {
        HistoryTab.entries.forEach { tab ->
          TabSegment(
            tab = tab,
            selected = tab == selectedTab,
            onClick = { selectedTab = tab },
            modifier = Modifier.weight(1f)
          )
        }
      }

      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .size(36.dp)
          .clip(RoundedCornerShape(10.dp))
          .clickable(onClick = if (isPcTab) onClearDesktopHistory else onClearPhoneHistory)
      ) {
        Icon(
          imageVector = Icons.Outlined.DeleteSweep,
          contentDescription = "Clear history",
          tint = PairroColors.Magenta,
          modifier = Modifier.size(20.dp)
        )
      }
    }

    Text(
      text = selectedTab.hint,
      fontSize = 11.sp,
      color = PairroColors.TextSecondary
    )

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(120.dp)
        .clip(RoundedCornerShape(10.dp))
        .background(PairroColors.Blush)
        .border(1.dp, PairroColors.BorderSubtle, RoundedCornerShape(10.dp))
    ) {
      if (entries.isEmpty()) {
        Text(
          text = selectedTab.emptyText,
          fontSize = 12.sp,
          color = PairroColors.TextSecondary,
          modifier = Modifier
            .align(Alignment.Center)
            .padding(16.dp)
        )
      } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
          items(entries, key = { it.id }) { entry ->
            Column {
              HistoryRow(
                entry = entry,
                onClick = { if (isPcTab) onCopyToPhone(entry) else onSendToDesktop(entry) }
              )

              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .height(1.dp)
                  .background(PairroColors.BorderSubtle)
              )
            }
          }
        }
      }
    }

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .heightIn(min = 44.dp, max = 72.dp)
        .clip(RoundedCornerShape(10.dp))
        .background(PairroColors.Blush)
        .border(1.dp, PairroColors.BorderSubtle, RoundedCornerShape(10.dp))
        .padding(horizontal = 10.dp, vertical = 12.dp)
    ) {
      if (draft.isEmpty()) {
        Text(
          text = "Write something to save…",
          fontSize = 13.sp,
          color = PairroColors.TextSecondary
        )
      }

      BasicTextField(
        value = draft,
        onValueChange = onDraftChange,
        textStyle = TextStyle(color = PairroColors.TextPrimary, fontSize = 13.sp),
        cursorBrush = SolidColor(PairroColors.AccentPrimary),
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
      )
    }

    Row(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      SaveButton(
        icon = Icons.Outlined.PhoneAndroid,
        label = "Save to phone",
        enabled = canSave,
        onClick = onSaveDraftToPhone,
        modifier = Modifier.weight(1f)
      )

      SaveButton(
        icon = Icons.Outlined.Computer,
        label = "Save to PC",
        enabled = canSave,
        onClick = onSaveDraftToDesktop,
        modifier = Modifier.weight(1f)
      )
    }
  }
}

@Composable
private fun TabSegment(
  tab: HistoryTab,
  selected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
      .fillMaxHeight()
      .clip(RoundedCornerShape(8.dp))
      .background(if (selected) PairroColors.AccentPrimary else PairroColors.Blush)
      .clickable(onClick = onClick)
  ) {
    Icon(
      imageVector = tab.icon,
      contentDescription = null,
      tint = if (selected) PairroColors.TextOnAccent else PairroColors.TextSecondary,
      modifier = Modifier.size(16.dp)
    )

    Text(
      text = tab.label,
      fontSize = 13.sp,
      maxLines = 1,
      color = if (selected) PairroColors.TextOnAccent else PairroColors.TextSecondary
    )
  }
}

@Composable
private fun SaveButton(
  icon: ImageVector,
  label: String,
  enabled: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
      .height(36.dp)
      .alpha(if (enabled) 1f else 0.4f)
      .clip(RoundedCornerShape(10.dp))
      .background(PairroColors.TealSoft)
      .clickable(enabled = enabled, onClick = onClick)
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = PairroColors.TealDeep,
      modifier = Modifier.size(16.dp)
    )

    Text(
      text = label,
      fontSize = 12.sp,
      maxLines = 1,
      color = PairroColors.TealDeep
    )
  }
}

@Composable
private fun HistoryRow(
  entry: ClipboardEntry,
  onClick: () -> Unit
) {
  val time = remember(entry.timestamp) {
    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(entry.timestamp))
  }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .heightIn(min = 48.dp)
      .clickable(onClick = onClick)
      .padding(horizontal = 10.dp, vertical = 8.dp)
  ) {
    Text(
      text = entry.text.replace('\n', ' ').trim(),
      fontSize = 13.sp,
      lineHeight = 17.sp,
      maxLines = 3,
      overflow = TextOverflow.Ellipsis,
      color = PairroColors.TextPrimary
    )

    Text(
      text = time,
      fontSize = 10.sp,
      color = PairroColors.TextSecondary
    )
  }
}