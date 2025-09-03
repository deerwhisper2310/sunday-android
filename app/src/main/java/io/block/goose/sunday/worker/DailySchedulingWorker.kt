
package io.block.goose.sunday.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

import io.block.goose.sunday.data.remote.UvResponse
import io.block.goose.sunday.data.repository.UvRepository
import io.block.goose.sunday.ui.UvDataState
import android.util.Log
import com.google.android.gms.location.LocationServices
import io.block.goose.sunday.services.NotificationScheduler
import kotlinx.coroutines.tasks.await

class DailySchedulingWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // This is a simplified way to get location in a background worker.
            // In a production app, we would inject a repository or use a more robust
            // background location fetching strategy.
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(applicationContext)
            val location = fusedLocationClient.lastLocation.await()

            if (location != null) {
                val uvRepository = UvRepository()

                val uvResult = uvRepository.getUvData(location.latitude, location.longitude)

                uvResult.fold(
                    onSuccess = { uvResponse ->
                        val timezoneId = uvResponse.timezone
                        val maxUv = uvResponse.daily.uvIndexMax.firstOrNull() ?: 0.0

                        NotificationScheduler.scheduleDailyNotifications(
                            context = applicationContext,
                            latitude = location.latitude,
                            longitude = location.longitude,
                            timezoneId = timezoneId,
                            maxUv = maxUv
                        )
                        Result.success()
                    },
                    onFailure = { error ->
                        Log.e("DailySchedulingWorker", "Failed to fetch UV data: ${error.message}", error)
                        Result.retry() // Retry if UV data fetch fails
                    }
                )
            } else {
                Log.w("DailySchedulingWorker", "Location not available for scheduling notifications, retrying.")
                Result.retry() // Retry if location is not available
            }
        } catch (e: SecurityException) {
            // This can happen if location permissions are revoked.
            Result.failure()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
