package cm.project.anitrack_compose.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import cm.project.anitrack_compose.graphql.type.MediaListStatus
import cm.project.anitrack_compose.ui.components.BottomNavBar
import cm.project.anitrack_compose.viewModels.WatchListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchlistScreen(
    navController: NavController = rememberNavController(),
    watchListViewModel: WatchListViewModel = hiltViewModel()
) {
    val selectedIndex by watchListViewModel.selectedStatus.collectAsState()
    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Watchlist") }
                )
                TabRow(selectedTabIndex = selectedIndex) {
                    val tabs = listOf("Watching", "Plan to Watch", "Completed")
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            text = { Text(title) },
                            selected = selectedIndex == index,
                            onClick = {
                                watchListViewModel.setSelectedIndex(index)
                            }
                        )
                    }
                }
            }
        },
        bottomBar = { BottomNavBar(navController) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            val possibleStatuses =
                listOf(MediaListStatus.CURRENT, MediaListStatus.PLANNING, MediaListStatus.COMPLETED)
            when (selectedIndex) {
                0 -> WatchListScreen(possibleStatuses[0])
                1 -> WatchListScreen(possibleStatuses[1])
                2 -> WatchListScreen(possibleStatuses[2])
            }
        }
    }
}

@Composable
fun WatchListScreen(
    mediaListStatus: MediaListStatus,
    watchListViewModel: WatchListViewModel = hiltViewModel()
) {

}