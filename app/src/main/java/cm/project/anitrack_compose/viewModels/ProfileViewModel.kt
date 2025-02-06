package cm.project.anitrack_compose.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.project.anitrack_compose.repositories.GraphQLRepository
import cm.project.anitrack_compose.repositories.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val graphQLRepository: GraphQLRepository) :
    ViewModel() {
    private val _unreadNotificationCount = MutableStateFlow(0)
    val unreadNotificationCount = _unreadNotificationCount.asStateFlow()

    fun fetchUnreadNotificationCount() {
        viewModelScope.launch {
            when (val result = graphQLRepository.getUnreadNotificationCount()) {
                is Result.Success -> {
                    _unreadNotificationCount.value = result.data
                }

                is Result.Error -> {}
            }
        }
    }
}