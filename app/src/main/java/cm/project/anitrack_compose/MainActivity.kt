package cm.project.anitrack_compose

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import cm.project.anitrack_compose.repositories.PreferencesRepository
import cm.project.anitrack_compose.ui.navigation.AppNavHost
import cm.project.anitrack_compose.ui.theme.AnitrackComposeTheme
import cm.project.anitrack_compose.viewModels.PreferencesViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context: Context = LocalContext.current
            val preferencesRepository = PreferencesRepository(context)
            val preferencesViewModel = PreferencesViewModel(preferencesRepository)

            val clientId by preferencesViewModel.clientId.collectAsState(initial = null)
            val clientSecret by preferencesViewModel.clientSecret.collectAsState(initial = null)

            AnitrackComposeTheme {
                val initialRoute: String = if (clientId == null || clientSecret == null) {
                    "apiSetup"
                } else {
                    "watchlist"
                }

                AppNavHost(
                    preferencesViewModel,
                    navController = rememberNavController(),
                    startDestination = initialRoute
                )
            }
        }
    }
}