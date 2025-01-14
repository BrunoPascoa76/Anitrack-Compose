package cm.project.anitrack_compose.viewModels

import androidx.lifecycle.ViewModel
import cm.project.anitrack_compose.repositories.PreferencesRepository

class PreferencesViewModel(private val preferencesRepository: PreferencesRepository) : ViewModel() {
    val clientId = preferencesRepository.clientId
    val clientSecret = preferencesRepository.clientSecret

    suspend fun saveClientId(clientId: String) {
        preferencesRepository.saveClientId(clientId)
    }

    suspend fun saveClientSecret(clientSecret: String) {
        preferencesRepository.saveClientSecret(clientSecret)
    }
}