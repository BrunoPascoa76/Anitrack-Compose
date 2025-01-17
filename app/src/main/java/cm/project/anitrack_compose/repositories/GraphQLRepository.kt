package cm.project.anitrack_compose.repositories

import cm.project.anitrack_compose.graphql.GetAiringAnimeCalendarQuery
import cm.project.anitrack_compose.graphql.GetMediaListQuery
import cm.project.anitrack_compose.graphql.GetMediaListsQuery
import cm.project.anitrack_compose.graphql.GetUserIdQuery
import cm.project.anitrack_compose.graphql.UserProfilePictureQuery
import cm.project.anitrack_compose.graphql.type.AiringSort
import cm.project.anitrack_compose.graphql.type.MediaListSort
import cm.project.anitrack_compose.graphql.type.MediaListStatus
import cm.project.anitrack_compose.graphql.type.MediaType
import cm.project.anitrack_compose.models.User
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
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

    suspend fun getUserId(): Result<Int> {
        return try {
            val response = apolloClient.query(GetUserIdQuery()).execute()
            val viewer = response.data?.Viewer
            Result.Success(viewer?.id ?: -1)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun getMediaList(
        userId: Int,
        status: MediaListStatus,
        type: MediaType = MediaType.ANIME,
        sort: List<MediaListSort> = listOf(MediaListSort.SCORE_DESC)
    ): Result<GetMediaListQuery.MediaListCollection> {
        return try {
            val response = apolloClient.query(
                GetMediaListQuery(
                    Optional.present(userId),
                    Optional.present(status),
                    Optional.present(type),
                    Optional.present(sort)
                )
            ).execute()
            val mediaListCollection = response.data?.MediaListCollection
            Result.Success(mediaListCollection!!)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun getMediaLists(
        userId: Int,
        status: List<MediaListStatus>,
        type: MediaType = MediaType.ANIME,
        sort: List<MediaListSort> = listOf(MediaListSort.SCORE_DESC)
    ): Result<GetMediaListsQuery.MediaListCollection> {
        return try {
            val response = apolloClient.query(
                GetMediaListsQuery(
                    Optional.present(userId),
                    Optional.present(type),
                    Optional.present(sort),
                    Optional.present(status)
                )
            ).execute()
            val mediaListCollection = response.data?.MediaListCollection
            Result.Success(mediaListCollection!!)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun getAiringAnimeCalendar(
        airingAtGreater: Int? = null,
        airingAtLesser: Int? = null,
        sort: List<AiringSort> = listOf(AiringSort.TIME),
        mediaIdIn: List<Int>? = null
    ): Result<GetAiringAnimeCalendarQuery.Page> {
        return try {
            val response = apolloClient.query(
                GetAiringAnimeCalendarQuery(
                    Optional.present(airingAtGreater),
                    Optional.present(airingAtLesser),
                    Optional.present(sort),
                    Optional.presentIfNotNull(mediaIdIn)
                )
            ).execute()
            val page = response.data?.Page
            Result.Success(page!!)
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