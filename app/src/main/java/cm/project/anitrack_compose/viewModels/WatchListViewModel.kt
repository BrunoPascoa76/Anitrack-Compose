package cm.project.anitrack_compose.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.project.anitrack_compose.graphql.GetMediaListQuery.Entry
import cm.project.anitrack_compose.graphql.type.MediaListStatus
import cm.project.anitrack_compose.repositories.GraphQLRepository
import cm.project.anitrack_compose.repositories.Result
import com.apollographql.apollo3.exception.ApolloHttpException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.max

@HiltViewModel
class WatchListViewModel @Inject constructor(private val graphQLRepository: GraphQLRepository) :
    ViewModel() {
    private var _isRefreshing = false
    private var refreshJob: Job? = null

    private var _userId: Int? = null

    private val _watchlist: MutableStateFlow<List<Entry?>> = MutableStateFlow(emptyList())
    val watchlist = _watchlist.asStateFlow()

    private val _selectedIndex = MutableStateFlow(0)
    val selectedStatus = _selectedIndex.asStateFlow()

    private val _isBeingRateLimited = MutableStateFlow(false)
    val isBeingRateLimited = _isBeingRateLimited.asStateFlow()

    fun startRefreshing(status: MediaListStatus) {
        _isRefreshing = true
        refreshJob = viewModelScope.launch {
            while (_isRefreshing) {
                refresh(status)
                delay(60_000)
            }
        }

    }

    fun setSelectedIndex(index: Int) {
        _watchlist.value = emptyList()
        _selectedIndex.value = index
    }

    fun stopRefreshing() {
        _isRefreshing = false
        refreshJob?.cancel()
    }

    private suspend fun refresh(status: MediaListStatus) {
        _isBeingRateLimited.value = false
        if (_userId == null) {
            when (val result = graphQLRepository.getUserId()) {
                is Result.Success -> {
                    _userId = result.data
                }

                is Result.Error -> {
                    if (result.exception is ApolloHttpException && result.exception.statusCode == 429) {
                        _isBeingRateLimited.value = true
                    }
                }
            }
        }
        when (val result = graphQLRepository.getMediaList(_userId!!, status)) {
            is Result.Success -> {
                if (status == MediaListStatus.CURRENT) {
                    orderByUnwatched(result.data.lists?.get(0)?.entries ?: emptyList())
                } else {
                    _watchlist.value = result.data.lists?.get(0)?.entries ?: emptyList()
                }
            }

            is Result.Error -> {
                if (result.exception is ApolloHttpException && result.exception.statusCode == 429) {
                    _isBeingRateLimited.value = true
                }
            }
        }
    }

    private fun orderByUnwatched(entries: List<Entry?>) {
        _watchlist.value =
            entries.sortedBy {
                -max(
                    0,
                    ((it?.media?.nextAiringEpisode?.episode ?: it?.media?.episodes
                    ?: 1) - (it?.progress ?: 0) - 1)
                )
            }
    }

}