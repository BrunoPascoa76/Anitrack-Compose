package cm.project.anitrack_compose.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cm.project.anitrack_compose.ui.APISetupScreen
import cm.project.anitrack_compose.ui.CalendarScreen
import cm.project.anitrack_compose.ui.DiscoverScreen
import cm.project.anitrack_compose.ui.MediaDetailsScreen
import cm.project.anitrack_compose.ui.WatchlistScreen
import cm.project.anitrack_compose.ui.components.GraphQLWrapper
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
            GraphQLWrapper(preferencesViewModel) {
                WatchlistScreen(
                    navController = navController
                )
            }
        }
        composable("profile") {

        }
        composable("calendar") {
            GraphQLWrapper(preferencesViewModel) {
                CalendarScreen(navController = navController)
            }
        }
        composable("media/{id}") {
            GraphQLWrapper(preferencesViewModel) {
                val id = it.arguments?.getString("id")?.toIntOrNull()
                if (id == null || id == 0) navController.navigateUp()
                MediaDetailsScreen(navController = navController, mediaId = id!!)
            }
        }
        composable("explore") {
            GraphQLWrapper(preferencesViewModel) {
                DiscoverScreen(navController = navController)
            }
        }
        composable("search/{query}") {
            val query = it.arguments?.getString("query")
            GraphQLWrapper(preferencesViewModel) {

            }
        }


        composable("apiSetup") {
            APISetupScreen(
                preferencesViewModel = preferencesViewModel,
                navController = navController
            )
        }
    }
}