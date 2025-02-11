package cm.project.anitrack_compose.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import cm.project.anitrack_compose.paging.NotificationsPage
import cm.project.anitrack_compose.repositories.GraphQLRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(private val graphQLRepository: GraphQLRepository) :
    ViewModel() {
    val pager = Pager(PagingConfig(pageSize = 20)) {
        NotificationsPage(graphQLRepository)
    }.flow.cachedIn(viewModelScope)
}