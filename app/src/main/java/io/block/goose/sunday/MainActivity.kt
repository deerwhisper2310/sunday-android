package io.block.goose.sunday

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
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

import androidx.activity.enableEdgeToEdge


class MainActivity : ComponentActivity() {

    private val database by lazy { AppDatabase.getDatabase(this) }
    private val uvRepository by lazy { UvRepository() }
    private val userPreferencesRepository by lazy { UserPreferencesRepository(database.userPreferencesDao()) }
    private val locationService by lazy { LocationService(this) }

    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(this, uvRepository, userPreferencesRepository, locationService)
    }

    private var hasLocationPermission by mutableStateOf(false)

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            ) {
                hasLocationPermission = true
                viewModel.startLocationUpdates()
                scheduleDailyWorker()
            }
            if (permissions[Manifest.permission.POST_NOTIFICATIONS] == true) {
                // Notification permission granted.
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        checkPermissions()

        setContent {
            SundayTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (hasLocationPermission) {
                        val uiState by viewModel.uiState.collectAsState()
                        MainScreen(
                            uiState = uiState,
                            onEvent = viewModel::onEvent,
                            modifier = Modifier
                        )
                    } else {
                        PermissionRequestScreen {
                            requestPermissions()
                        }
                    }
                }
            }
        }
    }

    private fun checkPermissions() {
        val locationGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (locationGranted) {
            hasLocationPermission = true
            viewModel.startLocationUpdates()
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

@Composable
fun PermissionRequestScreen(onRequestPermission: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Location Permission Required",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        Text(
            text = "This app needs location access to provide personalized UV index and weather data.",
            modifier = Modifier.padding(vertical = 16.dp),
            textAlign = TextAlign.Center
        )
        Button(onClick = onRequestPermission) {
            Text("Grant Permission")
        }
    }
}