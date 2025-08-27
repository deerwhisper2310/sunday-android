package io.block.goose.sunday

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import io.block.goose.sunday.data.local.AppDatabase
import io.block.goose.sunday.data.repository.UserPreferencesRepository
import io.block.goose.sunday.data.repository.UvRepository
import io.block.goose.sunday.services.LocationService
import io.block.goose.sunday.ui.MainViewModel
import io.block.goose.sunday.ui.MainViewModelFactory
import io.block.goose.sunday.ui.screen.MainScreen
import io.block.goose.sunday.ui.theme.SundayTheme
import io.block.goose.sunday.worker.DailySchedulingWorker
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    private val database by lazy { AppDatabase.getDatabase(this) }
    private val uvRepository by lazy { UvRepository() }
    private val userPreferencesRepository by lazy { UserPreferencesRepository(database.userPreferencesDao()) }
    private val locationService by lazy { LocationService(this) }

    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(this, uvRepository, userPreferencesRepository, locationService)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            ) {
                // Location permission granted. ViewModel will handle updates.
                // Now we can schedule our daily worker.
                scheduleDailyWorker()
            }
            if (permissions[Manifest.permission.POST_NOTIFICATIONS] == true) {
                // Notification permission granted.
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissions()

        setContent {
            SundayTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val uiState by viewModel.uiState.collectAsState()

                    MainScreen(
                        uiState = uiState,
                        onEvent = viewModel::onEvent
                    )
                }
            }
        }
    }

    private fun requestPermissions() {
        val permissionsToRequest = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
    }

    private fun scheduleDailyWorker() {
        val workRequest = PeriodicWorkRequestBuilder<DailySchedulingWorker>(24, TimeUnit.HOURS)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "DailyNotificationScheduler",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}

