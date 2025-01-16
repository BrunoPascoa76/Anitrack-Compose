package cm.project.anitrack_compose.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cm.project.anitrack_compose.repositories.GraphQLRepository
import cm.project.anitrack_compose.viewModels.ProfilePictureViewModel
import coil.compose.rememberAsyncImagePainter

@Composable
fun BottomNavBar(navController: NavController, graphQLRepository: GraphQLRepository) {
    val currentRoute = navController.currentBackStackEntry?.destination?.route

    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.CalendarMonth, contentDescription = "Watchlist") },
            label = { Text("Watchlist") },
            selected = currentRoute == "watchlist",
            onClick = {
                navController.navigate("watchlist")
            }
        )
        NavigationBarItem(
            icon = { ProfileIcon(graphQLRepository) },
            label = { Text("Profile") },
            selected = currentRoute == "profile",
            onClick = {
                navController.navigate("profile")
            }
        )
    }
}

@Composable
fun ProfileIcon(graphQLRepository: GraphQLRepository) {
    val profilePictureViewModel = ProfilePictureViewModel(graphQLRepository)
    val profilePictureUrl by profilePictureViewModel.profilePictureUrl.collectAsState()

    Icon(
        painter = rememberAsyncImagePainter(
            model = profilePictureUrl,
            error = rememberVectorPainter(Icons.Filled.Person),
            placeholder = rememberVectorPainter(Icons.Filled.Person)
        ),
        contentDescription = "Profile picture",
        modifier = Modifier.size(48.dp)
    )
}