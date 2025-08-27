
package io.block.goose.sunday.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import io.block.goose.sunday.services.NotificationService

class SunEventWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val title = inputData.getString(KEY_TITLE) ?: return Result.failure()
        val message = inputData.getString(KEY_MESSAGE) ?: return Result.failure()

        val notificationService = NotificationService(applicationContext)
        notificationService.createNotificationChannel() // Ensure channel is created
        notificationService.showNotification(title, message)

        return Result.success()
    }

    companion object {
        const val KEY_TITLE = "KEY_TITLE"
        const val KEY_MESSAGE = "KEY_MESSAGE"
    }
}
