package cm.project.anitrack_compose.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import cm.project.anitrack_compose.fuzzyDateToString
import cm.project.anitrack_compose.graphql.GetMediaDetailsQuery
import cm.project.anitrack_compose.ui.components.LoadingScreen
import cm.project.anitrack_compose.viewModels.MediaDetailsViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarStyle

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
                    BasicInfoComponent(media!!)
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
                        0 -> ExtendedInfoComponent(media!!)
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
            .padding(bottom = 5.dp)
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
private fun BasicInfoComponent(media: GetMediaDetailsQuery.Media) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Row {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(media.coverImage?.large)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.FillHeight,
                modifier = Modifier.height(150.dp)
            )

            Column(
                modifier = Modifier
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    media.title?.english ?: media.title?.native ?: "",
                    fontWeight = FontWeight.Bold
                )
                HorizontalDivider(thickness = 3.dp, color = MaterialTheme.colorScheme.primary)
                if (media.title?.native != null) {
                    Text("Native Title:")
                    Text(media.title.native)
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        "Status: " + media.status.toString().lowercase()
                            .replaceFirstChar { it.uppercase() }.replace("_", " "),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}


@Composable
private fun ExtendedInfoComponent(media: GetMediaDetailsQuery.Media) {
    ElevatedCard(modifier = Modifier.padding(10.dp)) {
        Column(modifier = Modifier.padding(vertical = 5.dp, horizontal = 10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Start: " + fuzzyDateToString(
                        media.startDate?.day,
                        media.startDate?.month,
                        media.startDate?.year
                    )
                )
                if (media.averageScore != null) RatingBar(
                    value = media.averageScore.toFloat() / 20,
                    style = RatingBarStyle.Default,
                    onValueChange = {},
                    onRatingChanged = {},
                    size = 15.dp
                )
                Text(
                    "End: " + fuzzyDateToString(
                        media.endDate?.day,
                        media.endDate?.month,
                        media.endDate?.year
                    )
                )
            }
        }
    }
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