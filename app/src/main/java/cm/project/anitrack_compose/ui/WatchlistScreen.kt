package cm.project.anitrack_compose.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import cm.project.anitrack_compose.repositories.GraphQLRepository
import cm.project.anitrack_compose.ui.components.BottomNavBar

@Composable
fun WatchlistScreen(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
    graphQLRepository: GraphQLRepository
) {
    Scaffold(
        bottomBar = { BottomNavBar(navController, graphQLRepository) }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Text("abc")
        }
    }
}