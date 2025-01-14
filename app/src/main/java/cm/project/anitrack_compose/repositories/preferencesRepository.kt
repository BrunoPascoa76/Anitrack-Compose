package cm.project.anitrack_compose.repositories

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PreferencesRepository(private val context: Context){
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name="settings")
        val CLIENT_ID= stringPreferencesKey("client_id")
        val CLIENT_SECRET= stringPreferencesKey("client_secret")
    }

    val clientId: Flow<String?> = context.dataStore.data.map{ preferences->
        preferences[CLIENT_ID]
    }

    val clientSecret: Flow<String?> = context.dataStore.data.map{ preferences->
        preferences[CLIENT_SECRET]
    }

    suspend fun saveClientId(clientId: String){
        context.dataStore.edit {
            preferences-> preferences[CLIENT_ID]=clientId
        }
    }

    suspend fun saveClientSecret(clientSecret: String){
        context.dataStore.edit {
            preferences-> preferences[CLIENT_SECRET]=clientSecret
        }
    }
}