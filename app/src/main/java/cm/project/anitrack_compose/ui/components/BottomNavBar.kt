package cm.project.anitrack_compose.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun BottomNavBar(navController: NavController) {
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
    }
}