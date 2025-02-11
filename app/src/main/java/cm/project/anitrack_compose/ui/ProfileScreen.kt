package cm.project.anitrack_compose.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import cm.project.anitrack_compose.ui.components.BottomNavBar
import cm.project.anitrack_compose.viewModels.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController = rememberNavController()) {
    val profileViewModel: ProfileViewModel = hiltViewModel()
    val unreadNotificationCount = profileViewModel.unreadNotificationCount.collectAsState()

    LaunchedEffect(Unit) {
        profileViewModel.fetchUnreadNotificationCount()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Profile")
                        NotificationIcon(unreadNotificationCount.value, navController)
                    }
                }
            )
        },
        bottomBar = { BottomNavBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {}
    }
}

@Composable
fun NotificationIcon(unreadNotificationCount: Int, navController: NavController) {
    BadgedBox(
        badge = {
            if (unreadNotificationCount > 0) {
                Badge { Text(text = unreadNotificationCount.toString()) }
            }
        }
    ) {
        IconButton(
            onClick = { navController.navigate("notifications") }
        ) {
            Icon(Icons.Default.Notifications, contentDescription = "Notifications")
        }
    }
}