package cm.project.anitrack_compose.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import cm.project.anitrack_compose.paging.NotificationsPage
import cm.project.anitrack_compose.repositories.GraphQLRepository
import cm.project.anitrack_compose.repositories.PreferencesRepository
import cm.project.anitrack_compose.repositories.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val graphQLRepository: GraphQLRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {
    val pager = Pager(PagingConfig(pageSize = 20)) {
        NotificationsPage(graphQLRepository)
    }.flow.cachedIn(viewModelScope)

    private val _notificationBadgeCount = MutableStateFlow(0)
    val notificationBadgeCount = _notificationBadgeCount.asStateFlow()

    fun incrementAlreadyDisplayedNotifications(amount: Int = 1) {
        viewModelScope.launch {
            preferencesRepository.incrementAlreadyDisplayedNotifications(amount)
        }
    }

    fun resetAlreadyDisplayedNotifications() {
        viewModelScope.launch {
            preferencesRepository.resetAlreadyDisplayedNotifications()
        }
    }

    fun updateNotificationBadgeCount() {
        viewModelScope.launch {
            val alreadyDisplayedNotifications =
                preferencesRepository.alreadyDisplayedNotifications.first()
            when (val result = graphQLRepository.getUnreadNotificationCount()) {
                is Result.Success -> {
                    _notificationBadgeCount.value = result.data + alreadyDisplayedNotifications
                }

                is Result.Error -> {
                    _notificationBadgeCount.value = 0
                }
            }
        }
    }
}