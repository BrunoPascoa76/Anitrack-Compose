package cm.project.anitrack_compose.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import cm.project.anitrack_compose.graphql.GetNotificationsQuery
import cm.project.anitrack_compose.repositories.GraphQLRepository
import cm.project.anitrack_compose.repositories.PreferencesRepository
import com.kdroid.composenotification.builder.ExperimentalNotificationsApi
import com.kdroid.composenotification.builder.Notification
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import kotlin.Exception
import kotlin.OptIn
import kotlin.String
import cm.project.anitrack_compose.repositories.Result as Result_


@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val preferencesRepository: PreferencesRepository,
    private val graphQLRepository: GraphQLRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            withContext(Dispatchers.Main) {
                preferencesRepository.cleanupExpiredAccessToken()

                val accessToken = preferencesRepository.accessToken.firstOrNull()

                if (accessToken == null) {
                    Result.failure()
                }

                when (val result = graphQLRepository.getNotifications()) {
                    is Result_.Success -> {
                        if (result.data.isNotEmpty()) {
                            sendNotification(result.data)
                        }
                    }

                    is Result_.Error -> {}
                }

                Result.success()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }


    @OptIn(ExperimentalNotificationsApi::class)
    private fun sendNotification(
        notifications: List<GetNotificationsQuery.Notification>
    ) {
        val stringBuilder = StringBuilder()
        for (notification in notifications) {
            stringBuilder.appendLine(notificationToText(notification))
        }

        Notification(
            title = "${notifications.size} new notifications",
            message = stringBuilder.toString(),
        )
    }

    private fun notificationToText(notification: GetNotificationsQuery.Notification): String {
        when (notification.__typename) {
            "AiringNotification" -> {
                val media = notification.onAiringNotification?.media
                val episode = notification.onAiringNotification?.episode ?: 1
                val title =
                    media?.title?.english ?: media?.title?.native ?: media?.title?.userPreferred
                    ?: ""
                return if (episode <= 1) {
                    "$title has just premiered!"
                } else {
                    "episode $episode of $title has just aired!"
                }
            }

            "RelatedMediaAdditionNotification" -> {
                val media = notification.onRelatedMediaAdditionNotification?.media
                val title =
                    media?.title?.english ?: media?.title?.native ?: media?.title?.userPreferred
                    ?: ""
                return "$title has been added to the database!"
            }

            "MediaDataChangeNotification" -> {
                val media = notification.onMediaDataChangeNotification?.media
                val title =
                    media?.title?.english ?: media?.title?.native ?: media?.title?.userPreferred
                    ?: ""
                return "$title has suffered data changes!"
            }

            else -> {
                return "Unknown notification type"
            }
        }
    }
}