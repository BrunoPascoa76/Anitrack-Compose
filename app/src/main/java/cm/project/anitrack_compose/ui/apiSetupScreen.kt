package cm.project.anitrack_compose.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import cm.project.anitrack_compose.repositories.PreferencesRepository
import cm.project.anitrack_compose.viewModels.PreferencesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true)
fun APISetupScreen(
    modifier: Modifier = Modifier,
    preferencesViewModel: PreferencesViewModel = PreferencesViewModel(
        PreferencesRepository(LocalContext.current)
    ),
    navController: NavController = rememberNavController()
) {
    val clientId by preferencesViewModel.clientId.collectAsState(initial = null)
    val clientSecret by preferencesViewModel.clientSecret.collectAsState(initial = null)

    LaunchedEffect(clientId, clientSecret) {
        if (clientId != null && clientSecret != null) {
            navController.navigate("auth")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("API Setup") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Instructions()
            InputFields(preferencesViewModel)
        }
    }

}

@Composable
private fun Instructions() {
    Text("How to:", style = MaterialTheme.typography.headlineLarge)
    Text(buildAnnotatedString {
        append("1. Go to the ")
        withLink(
            LinkAnnotation.Url(
                "https://anilist.co/settings/developer",
                TextLinkStyles(style = SpanStyle(color = MaterialTheme.colorScheme.primary))
            )
        ) {
            append("AniList Developer Console")
        }
    })
    Text("2. Create a new client (or use a pre-existing one)")
    Text("3. Copy the Client ID and Client Secret")
    Text("4. Press submit")
}

@Composable
private fun InputFields(preferencesViewModel: PreferencesViewModel) {
    Column {

    }
}
