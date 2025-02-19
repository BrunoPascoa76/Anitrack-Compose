package cm.project.anitrack_compose.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import cm.project.anitrack_compose.viewModels.NotificationViewModel

@Composable
fun BottomNavBar(navController: NavController) {
    val currentRoute = navController.currentBackStackEntry?.destination?.route

    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Visibility, contentDescription = "Watchlist") },
            label = { Text("Watchlist") },
            selected = currentRoute == "watchlist",
            onClick = {
                if (currentRoute != "watchlist") navController.navigate("watchlist")
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.CalendarMonth, contentDescription = "Calendar") },
            label = { Text("Calendar") },
            selected = currentRoute == "calendar",
            onClick = {
                if (currentRoute != "calendar") navController.navigate("calendar")
            }
        )
        NavigationBarItem(
            icon = { NotificationBell() },
            label = { Text("Notifications") },
            selected = currentRoute == "notifications",
            onClick = {
                if (currentRoute != "notifications") navController.navigate("notifications")
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Explore, contentDescription = "Explore") },
            label = { Text("Explore") },
            selected = currentRoute == "explore",
            onClick = {
                if (currentRoute != "explore") navController.navigate("explore")
            }
        )
    }
}

@Composable
fun NotificationBell() {
    val notificationViewModel: NotificationViewModel = hiltViewModel()
    val notificationBadgeCount by notificationViewModel.notificationBadgeCount.collectAsState()

    notificationViewModel.updateNotificationBadgeCount()

    BadgedBox(
        badge = {
            if (notificationBadgeCount > 0) {
                Badge {
                    if (notificationBadgeCount > 99) {
                        Text("99+")
                    } else {
                        Text(notificationBadgeCount.toString())
                    }
                }
            }
        }
    ) {
        Icon(Icons.Filled.Notifications, contentDescription = "Notifications")
    }
}