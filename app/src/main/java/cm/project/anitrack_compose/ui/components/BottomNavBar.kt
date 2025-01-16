package cm.project.anitrack_compose.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import cm.project.anitrack_compose.viewModels.ProfilePictureViewModel
import coil.compose.rememberAsyncImagePainter

@Composable
fun BottomNavBar(navController: NavController) {
    val currentRoute = navController.currentBackStackEntry?.destination?.route

    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Visibility, contentDescription = "Watchlist") },
            label = { Text("Watchlist") },
            selected = currentRoute == "watchlist",
            onClick = {
                navController.navigate("watchlist")
            }
        )
//        NavigationBarItem(
//            icon = { Icon(Icons.Filled.CalendarMonth, contentDescription = "Calendar") },
//            label = { Text("Calendar") },
//            selected = currentRoute == "calendar",
//            onClick = {
//                navController.navigate("calendar")
//            }
//        )
//        NavigationBarItem(
//            icon = { ProfileIcon() },
//            label = { Text("Profile") },
//            selected = currentRoute == "profile",
//            onClick = {
//                navController.navigate("profile")
//            }
//        )
    }
}

@Composable
fun ProfileIcon() {
    val profilePictureViewModel = hiltViewModel<ProfilePictureViewModel>()
    val profilePictureUrl by profilePictureViewModel.profilePictureUrl.collectAsState()

    Icon(
        painter = rememberAsyncImagePainter(
            model = profilePictureUrl,
            placeholder = rememberVectorPainter(Icons.Filled.Person),
        ),
        contentDescription = "Profile picture",
        modifier = Modifier.size(30.dp),
        tint = if (profilePictureUrl.isEmpty()) MaterialTheme.colorScheme.onSurface else Color.Unspecified
    )
}