package cm.project.anitrack_compose.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.project.anitrack_compose.graphql.GetMediaDetailsQuery
import cm.project.anitrack_compose.repositories.GraphQLRepository
import cm.project.anitrack_compose.repositories.Result
import com.apollographql.apollo3.exception.ApolloHttpException
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

    private val _isBeingRateLimited = MutableStateFlow(false)
    val isBeingRateLimited = _isBeingRateLimited.asStateFlow()

    fun getMediaDetails(mediaId: Int) {
        _media.value = null
        _isBeingRateLimited.value = false
        viewModelScope.launch {
            when (val result = graphQLRepository.getMediaDetails(mediaId)) {
                is Result.Success -> {
                    _media.value = result.data
                }

                is Result.Error -> {
                    if (result.exception is ApolloHttpException && result.exception.statusCode == 429) {
                        _isBeingRateLimited.value = true
                    }
                }
            }
        }
    }

    fun setSelectedTab(tab: Int) {
        _selectedTab.value = tab
    }
}