
package io.block.goose.sunday.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
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
                NotificationScheduler.scheduleDailyNotifications(
                    context = applicationContext,
                    latitude = location.latitude,
                    longitude = location.longitude
                )
                Result.success()
            } else {
                // If location is not available, retry later.
                Result.retry()
            }
        } catch (e: SecurityException) {
            // This can happen if location permissions are revoked.
            Result.failure()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
