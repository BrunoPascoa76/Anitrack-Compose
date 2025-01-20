package cm.project.anitrack_compose.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cm.project.anitrack_compose.fuzzyDateToString
import cm.project.anitrack_compose.reformatEnums
import cm.project.anitrack_compose.viewModels.MediaDetailsViewModel


@Composable
fun MediaListEntryDisplay(mediaDetailsViewModel: MediaDetailsViewModel, onDismiss: () -> Unit) {
    val mediaListEntry by mediaDetailsViewModel.mediaListEntry.collectAsState()


    ElevatedCard(
        modifier = Modifier
            .width(500.dp)
            .height(200.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(reformatEnums(mediaListEntry?.status?.toString() ?: ""))
                }
                VerticalDivider()
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                ) {
                    TextField(
                        value = mediaListEntry?.score?.toString() ?: "",
                        onValueChange = {},
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                }
                VerticalDivider()
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(mediaListEntry?.score?.toString() ?: "")
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(
                        fuzzyDateToString(
                            mediaListEntry?.startedAt?.year,
                            mediaListEntry?.startedAt?.month,
                            mediaListEntry?.startedAt?.day
                        )
                    )
                }
                VerticalDivider()
                Box(contentAlignment = Alignment.Center, modifier = Modifier.weight(1f)) {
                    Text(
                        fuzzyDateToString(
                            mediaListEntry?.completedAt?.year,
                            mediaListEntry?.completedAt?.month,
                            mediaListEntry?.completedAt?.day
                        )
                    )
                }
                VerticalDivider()
            }
        }
    }
}