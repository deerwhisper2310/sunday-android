
package io.block.goose.sunday.services

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import io.block.goose.sunday.domain.calculator.SunEventCalculator
import io.block.goose.sunday.worker.SunEventWorker
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit

object NotificationScheduler {

    private const val SUNRISE_TAG = "SUNRISE_NOTIFICATION"
    private const val NOON_TAG = "NOON_NOTIFICATION"
    private const val SUNSET_TAG = "SUNSET_NOTIFICATION"

    fun scheduleDailyNotifications(context: Context, latitude: Double, longitude: Double) {
        val workManager = WorkManager.getInstance(context)

        // Cancel any previously scheduled notifications to avoid duplicates
        workManager.cancelAllWorkByTag(SUNRISE_TAG)
        workManager.cancelAllWorkByTag(NOON_TAG)
        workManager.cancelAllWorkByTag(SUNSET_TAG)

        val now = Calendar.getInstance().time
        val sunEvents = SunEventCalculator.calculate(latitude, longitude, now)

        scheduleNotification(workManager, sunEvents.sunrise, "Sunrise", "Good morning! The sun is rising.", SUNRISE_TAG)
        scheduleNotification(workManager, sunEvents.solarNoon, "Peak UV", "UV index is at its peak. Stay safe!", NOON_TAG)
        scheduleNotification(workManager, sunEvents.sunset, "Sunset", "The sun is setting. Enjoy the evening!", SUNSET_TAG)
    }

    private fun scheduleNotification(workManager: WorkManager, eventTime: Date, title: String, message: String, tag: String) {
        val now = System.currentTimeMillis()
        val delay = eventTime.time - now

        // Only schedule if the event is in the future
        if (delay > 0) {
            val data = Data.Builder()
                .putString(SunEventWorker.KEY_TITLE, title)
                .putString(SunEventWorker.KEY_MESSAGE, message)
                .build()

            val workRequest = OneTimeWorkRequestBuilder<SunEventWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .addTag(tag)
                .build()

            workManager.enqueueUniqueWork(tag, ExistingWorkPolicy.REPLACE, workRequest)
        }
    }
}
