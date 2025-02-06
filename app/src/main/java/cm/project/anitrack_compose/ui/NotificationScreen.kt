package cm.project.anitrack_compose.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import cm.project.anitrack_compose.graphql.GetNotificationsQuery
import cm.project.anitrack_compose.viewModels.NotificationViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(navController: NavController) {
    var isRateLimited by remember { mutableStateOf(false) }
    val notificationViewModel: NotificationViewModel = hiltViewModel()
    val pager = notificationViewModel.pager.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(title = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            items(pager.itemCount) { item ->
                pager[item]?.let { notification ->
                    val title = notificationToText(notification)
                    val imageUrl = when (notification.__typename) {
                        "AiringNotification" -> notification.onAiringNotification?.media?.coverImage?.large
                        "RelatedMediaAdditionNotification" -> notification.onRelatedMediaAdditionNotification?.media?.coverImage?.large
                        "MediaDataChangeNotification" -> notification.onMediaDataChangeNotification?.media?.coverImage?.large
                        else -> null
                    }

                    val action = when (notification.__typename) {
                        "AiringNotification" -> {
                            { navController.navigate("media/${notification.onAiringNotification?.media?.id}") }
                        }

                        "RelatedMediaAdditionNotification" -> {
                            { navController.navigate("media/${notification.onRelatedMediaAdditionNotification?.media?.id}") }
                        }

                        "MediaDataChangeNotification" -> {
                            { navController.navigate("media/${notification.onMediaDataChangeNotification?.media?.id}") }
                        }

                        else -> {
                            {}
                        }
                    }

                    ListItem(text = title, imageUrl = imageUrl) { action() }
                }
            }
            pager.apply {
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
    }
}

private fun notificationToText(notification: GetNotificationsQuery.Notification): String {
    when (notification.__typename) {
        "AiringNotification" -> {
            val media = notification.onAiringNotification?.media
            val episode = notification.onAiringNotification?.episode ?: 1
            val title =
                media?.title?.english ?: media?.title?.native ?: media?.title?.userPreferred
                ?: ""
            return if (episode <= 1) {
                "$title has just premiered!<br>"
            } else {
                "Episode $episode of <b>$title</b> has just aired!<br>"
            }
        }

        "RelatedMediaAdditionNotification" -> {
            val media = notification.onRelatedMediaAdditionNotification?.media
            val title =
                media?.title?.english ?: media?.title?.native ?: media?.title?.userPreferred
                ?: ""
            return "<b>$title</b> has been added to the database!<br>"
        }

        "MediaDataChangeNotification" -> {
            val media = notification.onMediaDataChangeNotification?.media
            val title =
                media?.title?.english ?: media?.title?.native ?: media?.title?.userPreferred
                ?: ""
            return "<b>$title</b> has suffered data changes!<br>"
        }

        else -> {
            return "Unknown notification type<br>"
        }
    }
}

@Composable
fun ListItem(text: String, imageUrl: String? = null, action: () -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .padding(horizontal = 5.dp, vertical = 5.dp)
            .height(120.dp)
            .clickable { action() },
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (imageUrl != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current).data(imageUrl).build(),
                    contentDescription = null,
                    contentScale = ContentScale.FillHeight,
                    modifier = Modifier
                        .padding(5.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
            }
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    modifier = Modifier
                        .padding(start = 5.dp),
                    text = AnnotatedString.fromHtml(
                        htmlString = text
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}