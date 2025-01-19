package cm.project.anitrack_compose.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import cm.project.anitrack_compose.graphql.type.MediaSeason
import cm.project.anitrack_compose.graphql.type.MediaSort
import cm.project.anitrack_compose.paging.DiscoverMediaPage
import cm.project.anitrack_compose.repositories.GraphQLRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class DiscoverViewModel @Inject constructor(private val graphQLRepository: GraphQLRepository) :
    ViewModel() {
    private val _selectedIndex = MutableStateFlow(0)
    val selectedIndex = _selectedIndex.asStateFlow()

    var pager = Pager(PagingConfig(pageSize = 20)) {
        DiscoverMediaPage(
            graphQLRepository,
            listOf(MediaSort.POPULARITY_DESC),
        )
    }.flow.cachedIn(viewModelScope)

    fun updatePager(sortCriteria: List<MediaSort>, season: MediaSeason? = null, year: Int? = null) {
        pager = Pager(PagingConfig(pageSize = 20)) {
            DiscoverMediaPage(
                graphQLRepository,
                sortCriteria,
                season,
                year
            )
        }.flow.cachedIn(viewModelScope)
    }

    fun updateSelectedIndex(index: Int) {
        _selectedIndex.value = index
    }

}