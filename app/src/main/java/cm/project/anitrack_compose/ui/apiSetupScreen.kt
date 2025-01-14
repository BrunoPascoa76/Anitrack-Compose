package cm.project.anitrack_compose.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import cm.project.anitrack_compose.repositories.PreferencesRepository
import cm.project.anitrack_compose.viewModels.PreferencesViewModel

@Composable
@Preview(showBackground = true)
fun APISetupScreen(
    modifier: Modifier = Modifier,
    preferencesViewModel: PreferencesViewModel = PreferencesViewModel(
        PreferencesRepository(LocalContext.current)
    ),
    navController: NavController = rememberNavController()
) {

}