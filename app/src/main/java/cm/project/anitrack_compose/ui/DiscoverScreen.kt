package cm.project.anitrack_compose.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import cm.project.anitrack_compose.getSeason
import cm.project.anitrack_compose.graphql.type.MediaSeason
import cm.project.anitrack_compose.graphql.type.MediaSort
import cm.project.anitrack_compose.ui.components.AnimeGridCard
import cm.project.anitrack_compose.ui.components.BottomNavBar
import cm.project.anitrack_compose.ui.components.RateLimitWarning
import cm.project.anitrack_compose.ui.components.Searchbar
import cm.project.anitrack_compose.viewModels.DiscoverViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen(navController: NavController) {
    val discoverViewModel = hiltViewModel<DiscoverViewModel>()
    val selectedIndex by discoverViewModel.selectedIndex.collectAsState()

    Scaffold(
        topBar = {
            Column {
                TopAppBar(title = { Searchbar(navController, title = { Text("Discover") }) })
                TabRow(
                    selectedTabIndex = selectedIndex,
                ) {
                    val tabs = listOf(
                        "Trending",
                        "Popular",
                        "Airing This Season",
                        "Airing Next Season"
                    )
                    tabs.forEachIndexed { index, title ->
                        Tab(text = { Text(title) },
                            selected = selectedIndex == index,
                            onClick = { discoverViewModel.updateSelectedIndex(index) }
                        )
                    }
                }
            }
        },
        bottomBar = { BottomNavBar(navController) }
    ) { innerPadding ->
        when (selectedIndex) {
            0 -> DiscoverContent(
                listOf(MediaSort.TRENDING_DESC),
                modifier = Modifier.padding(innerPadding),
                navController = navController
            )

            1 -> DiscoverContent(
                listOf(MediaSort.POPULARITY_DESC),
                modifier = Modifier.padding(innerPadding),
                navController = navController
            )

            2 -> {
                val (season, year) = getSeason(0)
                DiscoverContent(
                    listOf(MediaSort.POPULARITY_DESC),
                    modifier = Modifier.padding(innerPadding),
                    season = season,
                    year = year,
                    navController = navController
                )
            }

            3 -> {
                val (season, year) = getSeason(1)
                DiscoverContent(
                    listOf(MediaSort.POPULARITY_DESC),
                    modifier = Modifier.padding(innerPadding),
                    season = season,
                    year = year,
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun DiscoverContent(
    sortCriteria: List<MediaSort>,
    modifier: Modifier = Modifier,
    season: MediaSeason? = null,
    year: Int? = null,
    navController: NavController
) {
    val discoverViewModel = hiltViewModel<DiscoverViewModel>()

    LaunchedEffect(Unit) {
        discoverViewModel.updatePager(sortCriteria, season, year)
    }

    var isRateLimited by remember { mutableStateOf(false) }

    val lazyPagingItems = discoverViewModel.pager.collectAsLazyPagingItems()

    Box(modifier = modifier.padding(5.dp)) {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            contentPadding = PaddingValues(5.dp),
            verticalItemSpacing = 10.dp,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(lazyPagingItems.itemCount) { item ->
                lazyPagingItems[item]?.let { media ->
                    AnimeGridCard(
                        navController = navController,
                        title = media.title?.english ?: media.title?.native
                        ?: media.title?.userPreferred ?: "",
                        imageUrl = media.coverImage?.large,
                        id = media.id
                    )
                }
            }

            lazyPagingItems.apply {
                when {
                    loadState.refresh is LoadState.Loading -> {
                        item { CircularProgressIndicator() } // Show loading indicator for initial load
                    }

                    loadState.append is LoadState.Loading -> {
                        item { CircularProgressIndicator() } // Show loading indicator for appending items
                    }

                    loadState.refresh is LoadState.Error -> {
                        isRateLimited = true
                        item { } // Show error view if initial load fails
                    }
                }
            }
        }
        RateLimitWarning(isRateLimited)
    }
}