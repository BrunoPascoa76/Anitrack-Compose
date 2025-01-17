package cm.project.anitrack_compose.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import cm.project.anitrack_compose.ui.components.AnimeGridCard
import cm.project.anitrack_compose.ui.components.BottomNavBar
import cm.project.anitrack_compose.viewModels.CalendarViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(navController: NavController) {
    val calendarViewModel: CalendarViewModel = hiltViewModel()
    val calendarFilterWatchlist by calendarViewModel.calendarFilterWatchlist.collectAsState(initial = false)
    val selectedIndex by calendarViewModel.selectedIndex.collectAsState()

    LaunchedEffect(Unit) {
        calendarViewModel.startRefreshing()
    }

    DisposableEffect(Unit) {
        onDispose {
            calendarViewModel.stopRefreshing()
        }
    }

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
                                if (selectedIndex != i) calendarViewModel.setSelectedIndex(i)
                            }
                        )
                    }
                }
            }
        },
        bottomBar = { BottomNavBar(navController) }
    ) { innerPadding ->
        Calendar(navController, calendarViewModel, Modifier.padding(innerPadding))
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

@Composable
private fun Calendar(
    navController: NavController,
    calendarViewModel: CalendarViewModel,
    modifier: Modifier = Modifier
) {
    val calendar by calendarViewModel.calendar.collectAsState()

    if (calendar.isNotEmpty()) {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            contentPadding = PaddingValues(5.dp),
            modifier = modifier.padding(5.dp),
            verticalItemSpacing = 10.dp,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(calendar.size) { index ->
                val entry = calendar[index]
                if (entry != null) {
                    AnimeGridCard(
                        navController = navController,
                        title = entry.media?.title?.english ?: entry.media?.title?.native ?: "",
                        id = entry.media?.id ?: 0,
                        imageUrl = entry.media?.coverImage?.large,
                        airingAt = entry.airingAt
                    )
                }
            }
        }
    } else {
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("(╥﹏╥)", style = MaterialTheme.typography.headlineLarge)
            Text("No anime today", style = MaterialTheme.typography.bodyLarge)
        }
    }
}