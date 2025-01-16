package cm.project.anitrack_compose.repositories

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PreferencesRepository @Inject constructor(@ApplicationContext private val context: Context) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
        val CLIENT_ID = stringPreferencesKey("client_id")
        val CLIENT_SECRET = stringPreferencesKey("client_secret")
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val ACCESS_TOKEN_EXPIRATION = longPreferencesKey("access_token_expiration")
    }

    val clientId: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[CLIENT_ID]
    }

    val clientSecret: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[CLIENT_SECRET]
    }

    val accessToken: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[ACCESS_TOKEN]
    }

    val accessTokenExpiration: Flow<Long?> = context.dataStore.data.map { preferences ->
        preferences[ACCESS_TOKEN_EXPIRATION]
    }

    suspend fun saveClientId(clientId: String) {
        context.dataStore.edit { preferences ->
            preferences[CLIENT_ID] = clientId
        }
    }

    suspend fun saveClientSecret(clientSecret: String) {
        context.dataStore.edit { preferences ->
            preferences[CLIENT_SECRET] = clientSecret
        }
    }

    suspend fun saveAccessToken(
        accessToken: String,
        expiresIn: Long = (365L * 24 * 60 * 60 * 1000)
    ) {
        context.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = accessToken
        }
        context.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_EXPIRATION] =
                System.currentTimeMillis() + expiresIn
        }
    }

    suspend fun cleanupExpiredAccessToken() {
        context.dataStore.edit { preferences ->
            val accessTokenExpiration = preferences[ACCESS_TOKEN_EXPIRATION]
            if (accessTokenExpiration != null && accessTokenExpiration < System.currentTimeMillis()) {
                preferences.remove(ACCESS_TOKEN)
                preferences.remove(ACCESS_TOKEN_EXPIRATION)
            }
        }
    }
}