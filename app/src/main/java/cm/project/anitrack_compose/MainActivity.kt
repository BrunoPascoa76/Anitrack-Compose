package cm.project.anitrack_compose

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import cm.project.anitrack_compose.ui.navigation.AppNavHost
import cm.project.anitrack_compose.ui.theme.AnitrackComposeTheme
import cm.project.anitrack_compose.viewModels.OAuthViewModel
import cm.project.anitrack_compose.viewModels.PreferencesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val preferencesViewModel by viewModels<PreferencesViewModel>()
    private val oAuthViewModel by viewModels<OAuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        lifecycleScope.launch {
            val clientId = preferencesViewModel.clientId.firstOrNull()
            val clientSecret = preferencesViewModel.clientSecret.firstOrNull()
            val accessToken = oAuthViewModel.accessToken.firstOrNull()

            if (clientId != null && clientSecret != null) {
                oAuthViewModel.cleanupExpiredAccessToken()
                if (accessToken == null) {
                    oAuthViewModel.startAuth(clientId)
                }
            }

            val initialRoute = if (clientId != null && clientSecret != null) "apiSetup" else "setup"
            setContent {
                AnitrackComposeTheme {
                    AppNavHost(
                        preferencesViewModel,
                        navController = rememberNavController(),
                        startDestination = initialRoute
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.data?.let { uri ->
            oAuthViewModel.handleAuthResponse(uri)
        }
    }
}