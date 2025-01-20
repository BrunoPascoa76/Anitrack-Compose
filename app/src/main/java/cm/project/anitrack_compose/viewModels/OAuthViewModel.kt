package cm.project.anitrack_compose.viewModels

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.project.anitrack_compose.repositories.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import okhttp3.internal.wait
import javax.inject.Inject

@HiltViewModel
class OAuthViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {
    val accessToken = preferencesRepository.accessToken

    fun startAuth(clientId: String) {
        val url =
            "https://anilist.co/api/v2/oauth/authorize?client_id=$clientId&response_type=token"

        val authIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        // Start the activity
        context.startActivity(authIntent)
    }

    fun handleAuthResponse(uri: Uri) {
        if (uri.toString().startsWith("myapp://auth")) {
            val fragment = uri.fragment
            if (fragment != null) {
                val fragmentUri = Uri.parse("myapp://auth?$fragment")
                val accessToken = fragmentUri.getQueryParameter("access_token")
                val expiresIn = fragmentUri.getQueryParameter("expires_in")?.toLong()

                if (accessToken != null) {
                    viewModelScope.launch {
                        preferencesRepository.saveAccessToken(
                            accessToken,
                            expiresIn ?: (365L * 24 * 60 * 60 * 1000)
                        )
                    }
                }
            }
        }
    }

    fun cleanupExpiredAccessToken() {
        viewModelScope.launch {
            preferencesRepository.cleanupExpiredAccessToken().wait()
            if (accessToken.firstOrNull() == null) {
                startAuth(preferencesRepository.clientId.firstOrNull()!!)
            }
        }
    }
}