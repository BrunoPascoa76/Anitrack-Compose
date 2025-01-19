package cm.project.anitrack_compose.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import cm.project.anitrack_compose.paging.SearchMediaPage
import cm.project.anitrack_compose.repositories.GraphQLRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val graphQLRepository: GraphQLRepository) :
    ViewModel() {
    var pager = Pager(PagingConfig(pageSize = 20)) {
        SearchMediaPage(graphQLRepository, "")
    }.flow.cachedIn(viewModelScope)

    fun updatePager(query: String) {
        pager = Pager(PagingConfig(pageSize = 20)) {
            SearchMediaPage(graphQLRepository, query)
        }.flow.cachedIn(viewModelScope)
    }
}