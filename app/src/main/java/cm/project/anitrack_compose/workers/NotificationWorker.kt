package cm.project.anitrack_compose.workers

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.text.HtmlCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import cm.project.anitrack_compose.MainActivity
import cm.project.anitrack_compose.R
import cm.project.anitrack_compose.graphql.GetNotificationsQuery
import cm.project.anitrack_compose.repositories.GraphQLRepository
import cm.project.anitrack_compose.repositories.PreferencesRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import kotlin.Exception
import kotlin.String
import kotlin.run
import cm.project.anitrack_compose.repositories.Result as Result_


@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted private val context: Context,
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

                when (val result = graphQLRepository.getUnreadNotifications()) {
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


    private fun sendNotification(
        notifications: List<GetNotificationsQuery.Notification>
    ) {

        val deepLinkIntent = Intent(
            Intent.ACTION_VIEW,
            "myapp://notificationRoute".toUri(),
            context,
            MainActivity::class.java
        )

        val deepLinkPendingIntent: PendingIntent? = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(deepLinkIntent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }


        val stringBuilder = StringBuilder()
        for (notification in notifications) {
            stringBuilder.appendLine(notificationToText(notification))
            stringBuilder.appendLine()
        }

        val bigTextStyle = NotificationCompat.BigTextStyle().bigText(
            HtmlCompat.fromHtml(
                stringBuilder.toString(),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        )

        val builder = NotificationCompat.Builder(applicationContext, "media_notification")
            .setContentTitle(if (notifications.size == 1) "1 new notification" else "${notifications.size} new notifications")
            .setContentText(stringBuilder.toString().stripBoldTags())
            .setSmallIcon(R.drawable.white_notification_icon)
            .setStyle(bigTextStyle)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(deepLinkPendingIntent)

        val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager

        notificationManager.notify(1, builder.build())
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
                    "$title has just premiered!<br>"
                } else {
                    "Episode $episode of <b>$title</b> has just aired!<br>"
                }
            }

            "RelatedMediaAdditionNotification" -> {
                val media = notification.onRelatedMediaAdditionNotification?.media
                val title =
                    media?.title?.english ?: media?.title?.native ?: media?.title?.userPreferred
                    ?: ""
                return "<b>$title</b> has been added to the database!<br>"
            }

            "MediaDataChangeNotification" -> {
                val media = notification.onMediaDataChangeNotification?.media
                val title =
                    media?.title?.english ?: media?.title?.native ?: media?.title?.userPreferred
                    ?: ""
                return "<b>$title</b> has suffered data changes!<br>"
            }

            else -> {
                return "Unknown notification type<br>"
            }
        }
    }
}

fun String.stripBoldTags(): String {
    return this.replace("(</?b>)|(<br>)".toRegex(), "")
}