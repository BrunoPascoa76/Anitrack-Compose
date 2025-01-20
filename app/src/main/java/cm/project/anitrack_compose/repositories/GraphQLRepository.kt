package cm.project.anitrack_compose.repositories

import cm.project.anitrack_compose.graphql.DiscoverMediaPageQuery
import cm.project.anitrack_compose.graphql.GetAiringAnimeCalendarQuery
import cm.project.anitrack_compose.graphql.GetMediaDetailsQuery
import cm.project.anitrack_compose.graphql.GetMediaListEntryQuery
import cm.project.anitrack_compose.graphql.GetMediaListQuery
import cm.project.anitrack_compose.graphql.GetMediaListsQuery
import cm.project.anitrack_compose.graphql.GetUserIdQuery
import cm.project.anitrack_compose.graphql.SaveMediaDetailsMutation
import cm.project.anitrack_compose.graphql.SearchMediaPageQuery
import cm.project.anitrack_compose.graphql.UserProfilePictureQuery
import cm.project.anitrack_compose.graphql.type.AiringSort
import cm.project.anitrack_compose.graphql.type.FuzzyDateInput
import cm.project.anitrack_compose.graphql.type.MediaListSort
import cm.project.anitrack_compose.graphql.type.MediaListStatus
import cm.project.anitrack_compose.graphql.type.MediaSeason
import cm.project.anitrack_compose.graphql.type.MediaSort
import cm.project.anitrack_compose.graphql.type.MediaType
import cm.project.anitrack_compose.models.User
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import java.time.LocalDate
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
        sort: List<MediaListSort> = listOf(MediaListSort.ADDED_TIME_DESC)
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

    suspend fun getMediaDetails(mediaId: Int): Result<GetMediaDetailsQuery.Media> {
        return try {
            val response = apolloClient.query(
                GetMediaDetailsQuery(Optional.present(mediaId))
            ).execute()
            val media = response.data?.Media
            Result.Success(media!!)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun getDiscoverPage(
        page: Int,
        sort: List<MediaSort>,
        seasonYear: Int? = null,
        season: MediaSeason? = null
    ): Result<DiscoverMediaPageQuery.Page> {
        return try {
            val response = apolloClient.query(
                DiscoverMediaPageQuery(
                    Optional.present(page),
                    Optional.present(sort),
                    Optional.presentIfNotNull(seasonYear),
                    Optional.presentIfNotNull(season)
                )
            ).execute()
            val pageData = response.data?.Page
            Result.Success(pageData!!)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun searchMedia(
        page: Int,
        query: String,
    ): Result<SearchMediaPageQuery.Page> {
        return try {
            val response = apolloClient.query(
                SearchMediaPageQuery(
                    Optional.present(page),
                    Optional.present(query)
                )
            ).execute()
            val pageData = response.data?.Page
            Result.Success(pageData!!)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun getMediaListEntry(
        userId: Int,
        mediaId: Int
    ): Result<GetMediaListEntryQuery.MediaList?> {
        return try {
            val response = apolloClient.query(
                GetMediaListEntryQuery(
                    Optional.present(userId),
                    Optional.present(mediaId)
                )
            ).execute()
            val mediaList = response.data?.MediaList
            Result.Success(mediaList)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun saveMediaListEntry(
        mediaId: Int,
        mediaListEntryId: Int? = null,
        startedAt: LocalDate? = null,
        completedAt: LocalDate? = null,
        score: Double? = null,
        progress: Int? = null,
        progressVolumes: Int? = null,
        status: MediaListStatus? = null
    ): Result<Unit> {
        return try {
            apolloClient.mutation(
                SaveMediaDetailsMutation(
                    mediaId = Optional.present(mediaId),
                    mediaListEntryId = Optional.presentIfNotNull(mediaListEntryId),
                    startedAt = Optional.presentIfNotNull(
                        FuzzyDateInput(
                            Optional.presentIfNotNull(startedAt?.year),
                            Optional.presentIfNotNull(startedAt?.monthValue),
                            Optional.presentIfNotNull(startedAt?.dayOfMonth)
                        )
                    ),
                    completedAt = Optional.presentIfNotNull(
                        FuzzyDateInput(
                            Optional.presentIfNotNull(completedAt?.year),
                            Optional.presentIfNotNull(completedAt?.monthValue),
                            Optional.presentIfNotNull(completedAt?.dayOfMonth)
                        ),
                    ),
                    score = Optional.presentIfNotNull(score),
                    progress = Optional.presentIfNotNull(progress),
                    progressVolumes = Optional.presentIfNotNull(progressVolumes),
                    status = Optional.presentIfNotNull(status)
                )
            ).execute()
            Result.Success(Unit)
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