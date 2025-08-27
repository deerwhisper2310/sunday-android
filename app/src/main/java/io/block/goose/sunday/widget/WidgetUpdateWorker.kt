
package io.block.goose.sunday.widget

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import io.block.goose.sunday.data.repository.UvRepository
import io.block.goose.sunday.data.repository.UserPreferencesRepository

class WidgetUpdateWorker(
    appContext: Context,
    workerParams: WorkerParameters,
    private val uvRepository: UvRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // In a real app, we would get the last known location.
            // For this example, we'll use a fixed location.
            val lat = 37.7749
            val lon = -122.4194

            uvRepository.getUvData(lat, lon)
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
