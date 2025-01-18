package cm.project.anitrack_compose.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.outlined.PlayCircle
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import cm.project.anitrack_compose.fuzzyDateToString
import cm.project.anitrack_compose.graphql.GetMediaDetailsQuery
import cm.project.anitrack_compose.ui.components.AnimeGridCard
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
            Column(
                modifier = Modifier
                    .padding(innerPadding)
            ) {
                BannerComponent(navController, media!!.bannerImage)
                BasicInfoComponent(media!!)
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
                when (selectedTab) {
                    0 -> ExtendedInfoComponent(media!!)
                    1 -> RelationsComponent(media!!.relations!!, navController)
                    2 -> CharactersComponent()
                    3 -> StaffComponent()
                    4 -> RecommendationsComponent(media!!.recommendations!!, navController)
                    5 -> ReviewsComponent(media!!.reviews!!)
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
    var isExpanded by remember { mutableStateOf(false) }

    ElevatedCard(modifier = Modifier
        .padding(10.dp)
        .clickable { isExpanded = !isExpanded }) {
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
            Spacer(modifier = Modifier.height(5.dp))

            media.description?.let { description ->
                Text("Description:")
                if (isExpanded) Text(AnnotatedString.fromHtml(description)) else Text(
                    AnnotatedString.fromHtml(description),
                    maxLines = 3,
                    softWrap = true,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
    val url = when (media.trailer?.site) {
        "youtube" -> "vnd.youtube:${media.trailer.id}"
        "dailymotion" -> "https://www.dailymotion.com/video/${media.trailer.id}"
        else -> null
    }
    url?.let {
        TrailerComponent(thumbnail = media.trailer?.thumbnail, url = url)
    }
    Spacer(modifier = Modifier.height(5.dp))

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier.padding(horizontal = 10.dp)
    ) {
        items(media.genres?.size ?: 0) { index ->
            media.genres!![index]?.let { genre ->
                ElevatedCard(
                    modifier = Modifier
                        .padding(4.dp) // Padding around the card
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .defaultMinSize(minWidth = 100.dp, minHeight = 50.dp)
                            .padding(horizontal = 10.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Label, contentDescription = null)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(genre)
                    }
                }
            }
        }
    }
}

@Composable
private fun TrailerComponent(thumbnail: String?, url: String) {
    val context = LocalContext.current

    Box(modifier = Modifier.padding(horizontal = 10.dp)) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(thumbnail).build(),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .size(200.dp)
        )
        Box(
            modifier = Modifier
                .padding(10.dp)
                .align(Alignment.Center)
                .clip(CircleShape)
                .size(90.dp)
                .clickable {
                    try {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    } catch (e: ActivityNotFoundException) {
                        context.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://youtube.com/watch?v=${url.split("=")[1]}")
                            )
                        )
                    }
                }
                .background(Color.Black.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Outlined.PlayCircle,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(70.dp)
            )
        }
    }
}

@Composable
private fun RelationsComponent(
    relations: GetMediaDetailsQuery.Relations,
    navController: NavController
) {
    val nodes = relations.nodes ?: emptyList()
    val edges = relations.edges ?: emptyList()

    val groupedRelations = nodes.zip(edges).groupBy { it.second?.relationType ?: "OTHER" }

    Column(modifier = Modifier.padding(16.dp)) {
        groupedRelations.forEach { (relationType, relationNodes) ->
            Text(relationType.toString().lowercase().replaceFirstChar { it.uppercase() }
                .replace("_", " "), style = MaterialTheme.typography.titleMedium)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(relationNodes.size) { index ->
                    val relationNode = relationNodes[index]
                    relationNode.first?.let { node ->
                        AnimeGridCard(
                            modifier = Modifier.width(150.dp),
                            navController = navController,
                            imageUrl = node.coverImage?.large,
                            title = node.title?.english ?: node.title?.native ?: "",
                            id = node.id
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CharactersComponent() {
}

@Composable
private fun StaffComponent() {
}


@Composable
private fun RecommendationsComponent(
    recommendations: GetMediaDetailsQuery.Recommendations,
    navController: NavController
) {
    val nodes = recommendations.nodes ?: emptyList()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            contentPadding = PaddingValues(5.dp),
            modifier = Modifier
                .padding(5.dp)
                .pointerInput(Unit) {
                    detectVerticalDragGestures { _, _ -> }
                },
            verticalItemSpacing = 10.dp,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(nodes.size) { index ->
                val node = nodes[index]
                node?.mediaRecommendation?.let {
                    AnimeGridCard(
                        navController = navController,
                        imageUrl = it.coverImage?.large,
                        title = it.title?.english ?: it.title?.native ?: "",
                        id = it.id
                    )
                }
            }
        }
    }
}

@Composable
private fun ReviewsComponent(reviews: GetMediaDetailsQuery.Reviews) {
    val nodes = reviews.nodes ?: emptyList()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn {
            items(nodes.size) { index ->
                val node = nodes[index]
                node?.let {
                    var expanded by remember { mutableStateOf(false) }

                    ElevatedCard(
                        modifier = Modifier
                            .padding(10.dp)
                            .clickable { expanded = !expanded }) {
                        Column(
                            modifier = Modifier
                                .padding(10.dp)
                        ) {
                            Column {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(it.user?.name ?: "")
                                    RatingBar(
                                        value = it.score?.toFloat() ?: 0f,
                                        style = RatingBarStyle.Default,
                                        onValueChange = {},
                                        onRatingChanged = {},
                                        size = 15.dp,
                                    )
                                }
                                Spacer(modifier = Modifier.height(5.dp))
                                Text(it.summary ?: "")
                            }
                            if (expanded) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 5.dp),
                                    thickness = 3.dp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(it.body ?: "")
                            }
                        }
                    }
                }
            }
        }
    }
}