package cm.project.anitrack_compose.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import cm.project.anitrack_compose.getSeason
import cm.project.anitrack_compose.graphql.DiscoverMediaPageQuery
import cm.project.anitrack_compose.graphql.type.MediaSeason
import cm.project.anitrack_compose.graphql.type.MediaSort
import cm.project.anitrack_compose.paging.DiscoverMediaPage
import cm.project.anitrack_compose.repositories.GraphQLRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class DiscoverViewModel @Inject constructor(private val graphQLRepository: GraphQLRepository) :
    ViewModel() {
    private val _selectedIndex = MutableStateFlow(0)
    val selectedIndex = _selectedIndex.asStateFlow()

    private val pagers = List(4) { index ->
        createPager(getParamsForTab(index))
    }

    fun getPagerForCurrentTab() = pagers[selectedIndex.value]

    private fun getParamsForTab(index: Int): Triple<List<MediaSort>, MediaSeason?, Int?> {
        return when (index) {
            0 -> Triple(listOf(MediaSort.TRENDING_DESC), null, null)
            1 -> Triple(listOf(MediaSort.POPULARITY_DESC), null, null)
            2 -> {
                val (currentSeason, currentYear) = getSeason(0)
                Triple(listOf(MediaSort.POPULARITY_DESC), currentSeason, currentYear)
            }

            3 -> {
                val (nextSeason, nextYear) = getSeason(1)
                Triple(listOf(MediaSort.POPULARITY_DESC), nextSeason, nextYear)
            }

            else -> Triple(listOf(MediaSort.POPULARITY_DESC), null, null)
        }
    }

    private fun createPager(params: Triple<List<MediaSort>, MediaSeason?, Int?>): Flow<PagingData<DiscoverMediaPageQuery.Medium>> {
        val (sortCriteria, season, year) = params
        return Pager(PagingConfig(pageSize = 20)) {
            DiscoverMediaPage(graphQLRepository, sortCriteria, season, year)
        }.flow.cachedIn(viewModelScope)
    }

    fun updateSelectedIndex(index: Int) {
        _selectedIndex.value = index
    }

}