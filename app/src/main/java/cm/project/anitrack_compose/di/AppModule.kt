package cm.project.anitrack_compose.di

import android.content.Context
import cm.project.anitrack_compose.repositories.AuthorizationInterceptor
import cm.project.anitrack_compose.repositories.GraphQLRepository
import cm.project.anitrack_compose.repositories.PreferencesRepository
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun providePreferencesRepository(@ApplicationContext context: Context): PreferencesRepository {
        return PreferencesRepository(context)
    }

    @Provides
    @Singleton
    fun provideGraphQlRepository(apolloClient: ApolloClient): GraphQLRepository {
        return GraphQLRepository(apolloClient)
    }

    @Provides
    @Singleton
    fun provideAccessTokenFlow(preferencesRepository: PreferencesRepository): Flow<String?> {
        return preferencesRepository.accessToken
    }

    @Provides
    @Singleton
    fun provideApolloClient(accessTokenFlow: Flow<String?>): ApolloClient {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthorizationInterceptor(accessTokenFlow))
            .build()

        return ApolloClient.Builder()
            .serverUrl("https://graphql.anilist.co")
            .okHttpClient(okHttpClient)
            .build()
    }
}