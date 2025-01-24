package cm.project.anitrack_compose.workers

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkerHelper @Inject constructor(private val workManager: WorkManager) {

    fun scheduleNotificationWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setConstraints(constraints)
            .build()

        val periodicWorkRequest =
            PeriodicWorkRequestBuilder<NotificationWorker>(30, TimeUnit.MINUTES)

        workManager.enqueue(workRequest)

        workManager.enqueueUniquePeriodicWork(
            "notificationWorker",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest.build()
        )
    }
}