package cm.project.anitrack_compose.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.project.anitrack_compose.graphql.GetMediaDetailsQuery
import cm.project.anitrack_compose.repositories.GraphQLRepository
import cm.project.anitrack_compose.repositories.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MediaDetailsViewModel @Inject constructor(private val graphQLRepository: GraphQLRepository) :
    ViewModel() {
    private val _media = MutableStateFlow<GetMediaDetailsQuery.Media?>(null)
    val media = _media.asStateFlow()

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab = _selectedTab.asStateFlow()

    fun getMediaDetails(mediaId: Int) {
        _media.value = null
        viewModelScope.launch {
            when (val result = graphQLRepository.getMediaDetails(mediaId)) {
                is Result.Success -> {
                    _media.value = result.data
                }

                is Result.Error -> {}
            }
        }
    }

    fun setSelectedTab(tab: Int) {
        _selectedTab.value = tab
    }
}