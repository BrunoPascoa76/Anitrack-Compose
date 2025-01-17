package cm.project.anitrack_compose.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cm.project.anitrack_compose.LoadingScreen
import cm.project.anitrack_compose.viewModels.PreferencesViewModel

//initializes graphql only once we finish retrieving the access token
@Composable
fun GraphQLWrapper(preferencesViewModel: PreferencesViewModel, content: @Composable () -> Unit) {
    val accessToken by preferencesViewModel.accessToken.collectAsState(initial = "")

    if (!accessToken.isNullOrEmpty()) {
        content()
    } else {
        LoadingScreen()
    }
}