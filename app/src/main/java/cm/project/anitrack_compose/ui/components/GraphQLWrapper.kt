package cm.project.anitrack_compose.ui.components

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cm.project.anitrack_compose.repositories.GraphQLRepository
import cm.project.anitrack_compose.viewModels.PreferencesViewModel

//initializes graphql only once we finish retrieving the access token
@Composable
fun GraphQLWrapper(preferencesViewModel: PreferencesViewModel, content: @Composable (graphQLRepository: GraphQLRepository) -> Unit) {
    val accessToken by preferencesViewModel.accessToken.collectAsState(initial="")

    if(!accessToken.isNullOrEmpty()) {
        val graphQLRepository = GraphQLRepository(accessToken!!)
        content(graphQLRepository)
    } else {
        CircularProgressIndicator()
    }
}