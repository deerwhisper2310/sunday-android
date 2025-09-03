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


    fun scheduleDailyNotifications(context: Context, latitude: Double, longitude: Double, timezoneId: String, maxUv: Double) {
        val workManager = WorkManager.getInstance(context)

        // Cancel any previously scheduled notifications to avoid duplicates
        workManager.cancelAllWorkByTag(SUNRISE_TAG)
        workManager.cancelAllWorkByTag(NOON_TAG)


        val now = Calendar.getInstance().time
        val sunEvents = SunEventCalculator.calculate(latitude, longitude, now, timezoneId)

        scheduleNotification(workManager, sunEvents.sunrise, "🌅 The sun is up!", "Today\'s max UV index: %.1f.".format(maxUv), SUNRISE_TAG)

        // Calculate solar noon notification time (30 minutes before actual solar noon)
        val solarNoonTimeMillis = (sunEvents.sunrise.time + sunEvents.sunset.time) / 2
        val notificationTimeMillis = solarNoonTimeMillis - (30 * 60 * 1000) // 30 minutes before
        val solarNoonNotificationTime = Date(notificationTimeMillis)

        scheduleNotification(workManager, solarNoonNotificationTime, "☀️ Solar noon approaching!", "Peak UV in 30 minutes (UV %.1f).".format(maxUv), NOON_TAG)

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
