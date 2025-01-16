package cm.project.anitrack_compose.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.project.anitrack_compose.graphql.GetMediaListQuery.Entry
import cm.project.anitrack_compose.graphql.type.MediaListStatus
import cm.project.anitrack_compose.repositories.GraphQLRepository
import cm.project.anitrack_compose.repositories.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

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

    fun startRefreshing(status: MediaListStatus) {
        viewModelScope.launch {
            _isRefreshing = true
            refresh(status)
            delay(5000)
        }
    }

    fun setSelectedIndex(index: Int) {
        _selectedIndex.value = index
    }

    fun stopRefreshing() {
        _isRefreshing = false
        refreshJob?.cancel()
    }

    private suspend fun refresh(status: MediaListStatus) {
        if (_userId == null) {
            when (val result = graphQLRepository.getUserId()) {
                is Result.Success -> {
                    _userId = result.data
                }

                is Result.Error -> {
                    return
                }
            }
        }
        when (val result = graphQLRepository.getMediaList(_userId!!, status)) {
            is Result.Success -> {
                _watchlist.value = result.data.lists?.get(0)?.entries ?: emptyList()
            }

            is Result.Error -> {}
        }
    }

}