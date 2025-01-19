package cm.project.anitrack_compose.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import cm.project.anitrack_compose.ui.components.AnimeGridCard
import cm.project.anitrack_compose.ui.components.BottomNavBar
import cm.project.anitrack_compose.ui.components.Searchbar
import cm.project.anitrack_compose.viewModels.SearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavController, query: String) {
    val searchViewModel = hiltViewModel<SearchViewModel>()
    val lazyPagingItems = searchViewModel.pager.collectAsLazyPagingItems()

    LaunchedEffect(Unit) {
        searchViewModel.updatePager(query)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Searchbar(
                    navController,
                    title = { Text("Search") },
                    initialQuery = query
                )
            })
        },
        bottomBar = { BottomNavBar(navController) }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Row(modifier = Modifier.padding(10.dp)) {
                Text("Search results for ", style = MaterialTheme.typography.titleLarge)
                Text(
                    query,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            }

            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                contentPadding = PaddingValues(5.dp),
                modifier = Modifier.padding(5.dp),
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
                            item { } // Show error view if initial load fails
                        }
                    }
                }
            }
        }
    }
}