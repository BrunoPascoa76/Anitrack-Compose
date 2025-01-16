package cm.project.anitrack_compose.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.project.anitrack_compose.repositories.PreferencesRepository
import kotlinx.coroutines.launch

class PreferencesViewModel(private val preferencesRepository: PreferencesRepository) : ViewModel() {
    val clientId = preferencesRepository.clientId
    val clientSecret = preferencesRepository.clientSecret
    val accessToken = preferencesRepository.accessToken

    fun saveClientIdAndSecret(clientId: String, clientSecret: String) {
        viewModelScope.launch {
            preferencesRepository.saveClientId(clientId)
            preferencesRepository.saveClientSecret(clientSecret)
        }
    }
}