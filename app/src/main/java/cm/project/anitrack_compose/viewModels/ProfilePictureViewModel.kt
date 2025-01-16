package cm.project.anitrack_compose.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.project.anitrack_compose.repositories.GraphQLRepository
import cm.project.anitrack_compose.repositories.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfilePictureViewModel(private val repository: GraphQLRepository) : ViewModel() {
    private val _profilePictureUrl = MutableStateFlow<String>("")
    val profilePictureUrl = _profilePictureUrl.asStateFlow()

    init {
        getProfilePicture()
    }

    private fun getProfilePicture() {
        viewModelScope.launch {
            when (val result = repository.getProfilePicture()) {
                is Result.Success -> {
                    _profilePictureUrl.value = result.data.avatarMedium
                }

                is Result.Error -> {}
            }
        }
    }
}