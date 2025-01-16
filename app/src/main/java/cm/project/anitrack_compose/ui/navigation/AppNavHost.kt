package cm.project.anitrack_compose.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cm.project.anitrack_compose.ui.APISetupScreen
import cm.project.anitrack_compose.ui.WatchlistScreen
import cm.project.anitrack_compose.viewModels.PreferencesViewModel

@Composable
fun AppNavHost(
    preferencesViewModel: PreferencesViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = "watchlist"
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable("watchlist") {
            WatchlistScreen(navController = navController)
        }
        composable("apiSetup") {
            APISetupScreen(
                preferencesViewModel = preferencesViewModel,
                navController = navController
            )
        }
    }
}