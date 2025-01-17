package cm.project.anitrack_compose.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import cm.project.anitrack_compose.ui.components.BottomNavBar
import cm.project.anitrack_compose.viewModels.CalendarViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(navController: NavController) {
    val calendarViewModel: CalendarViewModel = hiltViewModel()
    val calendarFilterWatchlist by calendarViewModel.calendarFilterWatchlist.collectAsState(initial = false)
    val selectedIndex by calendarViewModel.selectedIndex.collectAsState()

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Watchlist")
                            PillButton(calendarViewModel, calendarFilterWatchlist)
                        }
                    }
                )
                ScrollableTabRow(
                    selectedTabIndex = selectedIndex,
                    edgePadding = 0.dp
                ) {
                    for (i in 0..6) {
                        Tab(
                            text = {
                                Text(
                                    LocalDate.now()
                                        .plusDays(i.toLong()).dayOfWeek.name.lowercase()
                                        .replaceFirstChar { it.uppercase() })
                            },
                            selected = selectedIndex == i,
                            onClick = {
                                calendarViewModel.setSelectedIndex(i)
                            }
                        )
                    }
                }
            }
        },
        bottomBar = { BottomNavBar(navController) }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {

        }
    }
}

@Composable
private fun PillButton(calendarViewModel: CalendarViewModel, calendarFilterWatchlist: Boolean) {
    val optionsLabels = listOf("Watchlist", "All")
    val optionsValues = listOf(true, false)

    SingleChoiceSegmentedButtonRow {
        optionsLabels.forEachIndexed { index, label ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(index, optionsLabels.size),
                label = { Text(label) },
                onClick = { calendarViewModel.setCalendarFilterWatchlist(optionsValues[index]) },
                selected = calendarFilterWatchlist == optionsValues[index]
            )
        }
    }
}