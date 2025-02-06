package cm.project.anitrack_compose.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import cm.project.anitrack_compose.graphql.GetNotificationsQuery
import cm.project.anitrack_compose.viewModels.NotificationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(navController: NavController) {
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
                    val action = when (notification.__typename) {
                        "AiringNotification" -> {
                            { navController.navigate("anime/${notification.onAiringNotification?.media?.id}") }
                        }

                        "RelatedMediaAdditionNotification" -> {
                            { navController.navigate("anime/${notification.onRelatedMediaAdditionNotification?.media?.id}") }
                        }

                        "MediaDataChangeNotification" -> {
                            { navController.navigate("anime/${notification.onMediaDataChangeNotification?.media?.id}") }
                        }

                        else -> {
                            {}
                        }
                    }

                    ListItem(text = title) { action() }
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
fun ListItem(text: String, action: () -> Unit) {
    Row(modifier = Modifier
        .padding(horizontal = 5.dp, vertical = 10.dp)
        .clickable { action() }) {

    }
}