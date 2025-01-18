package cm.project.anitrack_compose.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import cm.project.anitrack_compose.ui.components.LoadingScreen
import cm.project.anitrack_compose.viewModels.MediaDetailsViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MediaDetailsScreen(mediaId: Int, navController: NavController) {
    val mediaDetailsViewModel: MediaDetailsViewModel = hiltViewModel()
    val media by mediaDetailsViewModel.media.collectAsState()
    val selectedTab by mediaDetailsViewModel.selectedTab.collectAsState()

    LaunchedEffect(Unit) {
        mediaDetailsViewModel.getMediaDetails(mediaId)
    }

    if (media != null) {
        Scaffold { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
            ) {
                item {
                    BannerComponent(navController, media!!.bannerImage)
                }
                item {
                    BasicInfoComponent()
                }
                stickyHeader {
                    val tabs = listOf(
                        "Details",
                        "Relations",
                        "Characters",
                        "Staff",
                        "Recommendations",
                        "Reviews"
                    )
                    ScrollableTabRow(selectedTabIndex = selectedTab) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = {
                                    if (selectedTab != index) mediaDetailsViewModel.setSelectedTab(
                                        index
                                    )
                                },
                                text = { Text(title) })
                        }
                    }
                }
                item {
                    when (selectedTab) {
                        0 -> ExtendedInfoComponent()
                        1 -> RelationsComponent()
                        2 -> CharactersComponent()
                        3 -> StaffComponent()
                        4 -> RecommendationsComponent()
                        5 -> ReviewsComponent()
                    }
                }
            }
        }
    } else {
        LoadingScreen()
    }
}

@Composable
private fun BannerComponent(navController: NavController, imageUrl: String?) {
    val gradient = Brush.verticalGradient(
        colors = listOf(
            Color.Black.copy(alpha = 0.5f),
            Color.Transparent,
        ),
        startY = 0f,
        endY = 100f
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(imageUrl).build(),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .background(gradient)
        ) {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
private fun BasicInfoComponent() {
}


@Composable
private fun ExtendedInfoComponent() {
}

@Composable
private fun RelationsComponent() {
}

@Composable
private fun CharactersComponent() {
}

@Composable
private fun StaffComponent() {
}


@Composable
private fun ReviewsComponent() {
}

@Composable
private fun RecommendationsComponent() {
}