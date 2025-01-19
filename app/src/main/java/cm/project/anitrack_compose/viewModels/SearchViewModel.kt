package cm.project.anitrack_compose.viewModels

import androidx.lifecycle.ViewModel
import cm.project.anitrack_compose.repositories.GraphQLRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val graphQLRepository: GraphQLRepository) :
    ViewModel() {

}