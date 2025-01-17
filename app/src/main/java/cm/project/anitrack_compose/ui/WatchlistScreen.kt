package cm.project.anitrack_compose.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.navigation.compose.rememberNavController
import cm.project.anitrack_compose.graphql.type.MediaListStatus
import cm.project.anitrack_compose.ui.components.AnimeGridCard
import cm.project.anitrack_compose.ui.components.BottomNavBar
import cm.project.anitrack_compose.viewModels.WatchListViewModel
import kotlin.math.max

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
                                if (selectedIndex != index) watchListViewModel.setSelectedIndex(
                                    index
                                )
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
                0 -> WatchListScreen(navController, possibleStatuses[0])
                1 -> WatchListScreen(navController, possibleStatuses[1])
                2 -> WatchListScreen(navController, possibleStatuses[2])
            }
        }
    }
}

@Composable
fun WatchListScreen(
    navController: NavController,
    mediaListStatus: MediaListStatus,
    watchListViewModel: WatchListViewModel = hiltViewModel()
) {
    val watchlist by watchListViewModel.watchlist.collectAsState()

    LaunchedEffect(Unit) {
        watchListViewModel.startRefreshing(mediaListStatus)
    }

    DisposableEffect(Unit) {
        onDispose {
            watchListViewModel.stopRefreshing()
        }
    }

    if (watchlist.isNotEmpty()) {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            contentPadding = PaddingValues(5.dp),
            modifier = Modifier.padding(5.dp),
            verticalItemSpacing = 10.dp,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(watchlist.size) { index ->
                val entry = watchlist[index]
                if (entry != null) {
                    AnimeGridCard(
                        navController = navController,
                        unwatchedEpisodes = if (mediaListStatus == MediaListStatus.CURRENT)
                            max(
                                0,
                                ((entry.media?.nextAiringEpisode?.episode ?: entry.media?.episodes
                                ?: 1) - (entry.progress
                                    ?: 0) - 1)
                            ) else 0,
                        title = entry.media?.title?.english ?: entry.media?.title?.native ?: "",
                        id = entry.media?.id ?: 0,
                        imageUrl = entry.media?.coverImage?.large
                    )
                }
            }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("(╥﹏╥)", style = MaterialTheme.typography.headlineLarge)
            Text("No anime in your watchlist", style = MaterialTheme.typography.bodyLarge)
        }
    }
}