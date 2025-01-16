package cm.project.anitrack_compose.repositories

import cm.project.anitrack_compose.graphql.UserProfilePictureQuery
import cm.project.anitrack_compose.models.User
import com.apollographql.apollo3.ApolloClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class GraphQLRepository @Inject constructor(private val apolloClient: ApolloClient) {
    suspend fun getProfilePicture(): Result<User> {
        return try {
            val response = apolloClient.query(UserProfilePictureQuery()).execute()
            val viewer = response.data?.Viewer
            Result.Success(User(viewer?.avatar?.medium ?: ""))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

class AuthorizationInterceptor(private val accessTokenFlow: Flow<String?>) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = runBlocking { accessTokenFlow.first() }
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $accessToken")
            .build()
        return chain.proceed(request)
    }
}