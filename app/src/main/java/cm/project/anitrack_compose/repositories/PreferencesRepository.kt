package cm.project.anitrack_compose.repositories

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
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
        private val CLIENT_ID = stringPreferencesKey("client_id")
        private val CLIENT_SECRET = stringPreferencesKey("client_secret")
        private val ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val ACCESS_TOKEN_EXPIRATION = longPreferencesKey("access_token_expiration")
        private val CALENDAR_FILTER_WATCHLIST = booleanPreferencesKey("calendar_filter_watchlist")
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

    val calendarFilterWatchlist: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[CALENDAR_FILTER_WATCHLIST] ?: false
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
        expiresIn: Long = (365L * 24 * 60 * 60)
    ) {
        context.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = accessToken
        }
        context.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_EXPIRATION] =
                System.currentTimeMillis() + (expiresIn * 1000) - 360_000 // minus 1h so that it doesn't expire mid-session
        }
    }

    suspend fun saveCalendarFilterWatchlist(filter: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[CALENDAR_FILTER_WATCHLIST] = filter
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