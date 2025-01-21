package cm.project.anitrack_compose.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import cm.project.anitrack_compose.fuzzyDateToString
import cm.project.anitrack_compose.graphql.type.MediaListStatus
import cm.project.anitrack_compose.reformatEnums
import cm.project.anitrack_compose.viewModels.MediaDetailsViewModel
import com.gmail.orlandroyd.composecalendar.DatePickerDlg
import java.time.LocalDate
import java.time.ZoneId


@Composable
fun MediaListEntryDisplay(
    mediaDetailsViewModel: MediaDetailsViewModel,
    onDismiss: () -> Unit,
    episodes: Int? = null
) {
    val mediaListEntry by mediaDetailsViewModel.mediaListEntry.collectAsState()

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Edit list entry")
                IconButton(onClick = { onDismiss() }) {
                    Icon(
                        Icons.Filled.Clear,
                        contentDescription = "Close"
                    )
                }
            }
            HorizontalDivider()
            StatusDisplay(
                modifier = Modifier.height(50.dp),
                value = mediaListEntry?.status,
                onStatusSelected = { mediaDetailsViewModel.setStatus(it) }
            )
            HorizontalDivider()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {

                VerticalDivider()
                NumberDisplay(
                    modifier = Modifier.weight(1f),
                    value = mediaListEntry?.progress ?: 0,
                    onNumberSelected = { mediaDetailsViewModel.setProgress(it) },
                    maxValue = episodes
                )
                VerticalDivider()
                NumberDisplay(
                    modifier = Modifier.weight(1f),
                    value = mediaListEntry?.score ?: 0,
                    onNumberSelected = { mediaDetailsViewModel.setScore(it) },
                    maxValue = 5
                )
            }
            HorizontalDivider()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DateDisplay(
                    modifier = Modifier.weight(1f),
                    value = fuzzyDateToString(
                        day = mediaListEntry?.startedAt?.day,
                        month = mediaListEntry?.startedAt?.month,
                        year = mediaListEntry?.startedAt?.year
                    ), onDateSelected = {
                        mediaDetailsViewModel.setStartedAt(it)
                    }
                )
                VerticalDivider()
                DateDisplay(
                    modifier = Modifier.weight(1f),
                    value = fuzzyDateToString(
                        day = mediaListEntry?.completedAt?.day,
                        month = mediaListEntry?.completedAt?.month,
                        year = mediaListEntry?.completedAt?.year
                    ), onDateSelected = {
                        mediaDetailsViewModel.setCompletedAt(it)
                    }
                )
            }
        }
    }
}

@Composable
fun DateDisplay(
    value: String,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDateDialog by remember { mutableStateOf(false) }
    Box(
        modifier = modifier.clickable {
            showDateDialog = true
        },
        contentAlignment = Alignment.Center
    ) {
        Text(value)
    }
    if (showDateDialog) {
        Popup(
            onDismissRequest = { showDateDialog = false },
            alignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .width(300.dp)
                    .height(400.dp),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp
            ) {
                DatePickerDlg(
                    visible = showDateDialog,
                    onClose = { showDateDialog = false },
                    onDateSelected = { date ->
                        showDateDialog = false
                        onDateSelected(
                            date.toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun NumberDisplay(
    value: Int,
    onNumberSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    maxValue: Int? = null
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        IconButton(onClick = { if (value > 0 && maxValue != 1) onNumberSelected(value - 1) }) {
            Icon(
                Icons.Filled.KeyboardArrowDown,
                contentDescription = "Decrease"
            )
        }
        Row {
            Text(value.toString())
            if (maxValue != null) Text("/$maxValue")
        }
        IconButton(onClick = { if (maxValue == null || value < maxValue) onNumberSelected(value + 1) }) {
            Icon(
                Icons.Filled.KeyboardArrowDown,
                contentDescription = "Increase"
            )
        }
    }
}

@Composable
fun StatusDisplay(
    value: MediaListStatus?,
    onStatusSelected: (MediaListStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .clickable { expanded = true },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Status:")
        Text(reformatEnums(value?.toString() ?: ""))
    }

    if (expanded) {
        Popup(
            onDismissRequest = { expanded = false },
            alignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .width(300.dp)
                    .height(400.dp),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp
            ) {
                Column {
                    MediaListStatus.entries.forEach { option ->
                        DropdownMenuItem(
                            onClick = {
                                onStatusSelected(option)
                                expanded = false
                            },
                            text = { Text(option.toString()) }
                        )
                    }
                }
            }
        }
    }

}