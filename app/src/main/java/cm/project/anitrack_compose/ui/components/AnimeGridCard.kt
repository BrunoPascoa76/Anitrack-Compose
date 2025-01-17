package cm.project.anitrack_compose.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import java.time.Instant
import java.time.format.DateTimeFormatter

@Composable
fun AnimeGridCard(
    navController: NavController,
    title: String,
    imageUrl: String?,
    modifier: Modifier = Modifier,
    id: Int,
    airingAt: Int? = null,
    unwatchedEpisodes: Int = 0,
) {
    ElevatedCard(modifier = modifier
        .fillMaxWidth()
        .clickable { navController.navigate("anime/$id") }) {
        BadgedBox(
            badge = {
                if (unwatchedEpisodes > 0) Card(
                    modifier = Modifier
                        .padding(top = 5.dp, end = 5.dp)
                        .width(30.dp)
                        .height(30.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Red)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            unwatchedEpisodes.toString(),
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                        )
                    }
                } else if (airingAt != null) {
                    val instant = Instant.ofEpochSecond(airingAt.toLong())
                    val zonedDateTime = instant.atZone(java.time.ZoneId.systemDefault())
                    val localTime = zonedDateTime.format(
                        DateTimeFormatter.ofPattern("HH:mm")
                    )
                    Card(
                        modifier = Modifier
                            .padding(top = 5.dp, end = 5.dp)
                            .width(80.dp)
                            .height(30.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Red)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 3.dp)
                        ) {
                            Icon(Icons.Filled.Schedule, contentDescription = null)
                            Text(
                                localTime,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
                if (imageUrl != null) {
                    ImageComponent(imageUrl)
                }
                TitleComponent(title)
            }
        }
    }
}

@Composable
private fun ImageComponent(imageUrl: String?) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current).data(imageUrl).build(),
        contentDescription = null,
        contentScale = ContentScale.FillWidth,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun TitleComponent(title: String) {
    val gradient = Brush.verticalGradient(
        colors = listOf(
            Color.Transparent,
            Color.Black
        ),
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(gradient)
            .height(80.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Text(
            text = title,
            color = Color.White,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}