package cm.project.anitrack_compose

import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import cm.project.anitrack_compose.ui.components.LoadingScreen
import cm.project.anitrack_compose.ui.navigation.AppNavHost
import cm.project.anitrack_compose.ui.theme.AnitrackComposeTheme
import cm.project.anitrack_compose.viewModels.OAuthViewModel
import cm.project.anitrack_compose.viewModels.PreferencesViewModel
import cm.project.anitrack_compose.workers.WorkerHelper
import com.kdroid.composenotification.builder.AndroidChannelConfig
import com.kdroid.composenotification.builder.ExperimentalNotificationsApi
import com.kdroid.composenotification.builder.NotificationInitializer.notificationInitializer
import com.kdroid.composenotification.builder.getNotificationProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val preferencesViewModel by viewModels<PreferencesViewModel>()
    private val oAuthViewModel by viewModels<OAuthViewModel>()

    @Inject
    lateinit var workerHelper: WorkerHelper

    @OptIn(ExperimentalNotificationsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        notificationInitializer(
            defaultChannelConfig = AndroidChannelConfig(
                channelId = "media_notification",
                channelName = "Anilist Media notifications",
                channelDescription = "My Channel Description",
                channelImportance = NotificationManager.IMPORTANCE_DEFAULT,
                smallIcon = R.drawable.white_notification_icon
            )
        )
        val notificationProvider = getNotificationProvider()
        val hasPermission by notificationProvider.hasPermissionState
        if (!hasPermission) {
            notificationProvider.requestPermission(
                onGranted = {
                    notificationProvider.updatePermissionState(true)
                },
                onDenied = {
                    notificationProvider.updatePermissionState(false)
                }
            )
        }


        var initialRoute: String? by mutableStateOf(null)
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
            initialRoute =
                if (clientId != null && clientSecret != null) "watchlist" else "apiSetup"
        }

        setContent {
            AnitrackComposeTheme {
                if (initialRoute != null) {
                    workerHelper.scheduleNotificationWorker()
                    AppNavHost(
                        preferencesViewModel,
                        navController = rememberNavController(),
                        startDestination = initialRoute!!
                    )
                } else {
                    LoadingScreen()
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

