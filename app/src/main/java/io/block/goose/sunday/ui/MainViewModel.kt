package io.block.goose.sunday.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.block.goose.sunday.data.local.entity.UserPreferences
import io.block.goose.sunday.data.remote.UvResponse
import io.block.goose.sunday.data.repository.UserPreferencesRepository
import io.block.goose.sunday.data.repository.UvRepository
import io.block.goose.sunday.domain.calculator.VitaminDCalculator
import io.block.goose.sunday.domain.model.ClothingLevel
import io.block.goose.sunday.domain.model.SkinType
import io.block.goose.sunday.domain.model.Sunscreen
import io.block.goose.sunday.services.LocationDetails
import io.block.goose.sunday.services.LocationService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.math.max

// Single state class for the UI
data class UiState(
    val uvDataState: UvDataState = UvDataState.Loading,
    val vitaminDRate: Double = 0.0,
    val userPreferences: UserPreferences = UserPreferences(),
    val burnTime: Int? = null,
    val maxUv: Double? = null,
    val sunrise: String? = null,
    val sunset: String? = null
)

// Events the UI can send to the ViewModel
sealed class UiEvent {
    data class SkinTypeChanged(val skinType: SkinType) : UiEvent()
    data class ClothingChanged(val clothingLevel: ClothingLevel) : UiEvent()
    data class SunscreenChanged(val sunscreen: Sunscreen) : UiEvent()
}

sealed class UvDataState {
    object Loading : UvDataState()
    data class Success(val uvResponse: UvResponse) : UvDataState()
    data class Error(val message: String) : UvDataState()
}

class MainViewModel(
    private val locationService: LocationService,
    private val uvRepository: UvRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val vitaminDCalculator = VitaminDCalculator()

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    // Private flows for intermediate data
    private val _location = MutableStateFlow<LocationDetails?>(null)
    private val _uvData = MutableStateFlow<UvDataState>(UvDataState.Loading)
    private val _userPreferences = MutableStateFlow(UserPreferences())

    init {
        // Load initial user preferences
        viewModelScope.launch {
            userPreferencesRepository.getPreferences().collect { prefs ->
                _userPreferences.value = prefs ?: UserPreferences()
            }
        }

        // Combine all data sources into the final UI state
        viewModelScope.launch {
            combine(_uvData, _userPreferences) { uvData, prefs ->
                val vitaminDRate = if (uvData is UvDataState.Success) {
                    calculateVitaminD(uvData.uvResponse, prefs)
                } else {
                    0.0
                }
                val burnTime = if (uvData is UvDataState.Success) {
                    calculateBurnTimeInMinutes(uvData.uvResponse, prefs.skinType)
                } else {
                    null
                }
                val maxUv = if (uvData is UvDataState.Success) {
                    uvData.uvResponse.daily.uvIndexMax.firstOrNull()
                } else {
                    null
                }
                val sunrise = if (uvData is UvDataState.Success) {
                    uvData.uvResponse.daily.sunrise.firstOrNull()
                } else {
                    null
                }
                val sunset = if (uvData is UvDataState.Success) {
                    uvData.uvResponse.daily.sunset.firstOrNull()
                } else {
                    null
                }
                UiState(
                    uvDataState = uvData,
                    userPreferences = prefs,
                    vitaminDRate = vitaminDRate,
                    burnTime = burnTime,
                    maxUv = maxUv,
                    sunrise = sunrise,
                    sunset = sunset
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun onEvent(event: UiEvent) {
        when (event) {
            is UiEvent.SkinTypeChanged -> {
                viewModelScope.launch {
                    val newPrefs = _userPreferences.value.copy(skinType = event.skinType)
                    userPreferencesRepository.savePreferences(newPrefs)
                }
            }
            is UiEvent.ClothingChanged -> {
                viewModelScope.launch {
                    val newPrefs = _userPreferences.value.copy(clothingLevel = event.clothingLevel)
                    userPreferencesRepository.savePreferences(newPrefs)
                }
            }
            is UiEvent.SunscreenChanged -> {
                viewModelScope.launch {
                    val newPrefs = _userPreferences.value.copy(sunscreen = event.sunscreen)
                    userPreferencesRepository.savePreferences(newPrefs)
                }
            }
        }
    }

    fun startLocationUpdates() {
        // Start collecting from the location service
        viewModelScope.launch {
            locationService.requestLocationUpdates().collect { locationDetails ->
                _location.value = locationDetails
                fetchUvData(locationDetails)
            }
        }
    }

    private fun fetchUvData(locationDetails: LocationDetails) {
        viewModelScope.launch {
            _uvData.update { UvDataState.Loading }
            val result = uvRepository.getUvData(locationDetails.latitude, locationDetails.longitude)
            _uvData.update {
                result.fold(
                    onSuccess = { UvDataState.Success(it) },
                    onFailure = { UvDataState.Error(it.message ?: "Unknown error") }
                )
            }
        }
    }

    private fun calculateVitaminD(uvResponse: UvResponse, preferences: UserPreferences): Double {
        val now = ZonedDateTime.now()
        val zoneId = ZoneId.of(uvResponse.timezone)

        // Find the index of the last time entry that is before or at the current time
        val currentTimeIndex = uvResponse.hourly.time.indexOfLast {
            val hourlyTime = LocalDateTime.parse(it).atZone(zoneId)
            hourlyTime.isBefore(now) || hourlyTime.isEqual(now)
        }.takeIf { it != -1 } ?: return 0.0

        val currentUv = uvResponse.hourly.uvIndex[currentTimeIndex]

        return vitaminDCalculator.calculateVitaminDRate(
            uvIndex = currentUv ?: 0.0,
            clothingLevel = preferences.clothingLevel,
            sunscreen = preferences.sunscreen,
            skinType = preferences.skinType,
            userAge = preferences.age,
            currentTime = now,
            averageDailyExposure = 1000.0 // Placeholder
        )
    }

    private fun calculateBurnTimeInMinutes(uvResponse: UvResponse, skinType: SkinType): Int? {
        val now = ZonedDateTime.now()
        val zoneId = ZoneId.of(uvResponse.timezone)

        val currentTimeIndex = uvResponse.hourly.time.indexOfLast {
            val hourlyTime = LocalDateTime.parse(it).atZone(zoneId)
            hourlyTime.isBefore(now) || hourlyTime.isEqual(now)
        }.takeIf { it != -1 } ?: return null

        val currentUv = uvResponse.hourly.uvIndex[currentTimeIndex] ?: return null

        // If UV index is 0, burn time is not applicable
        if (currentUv == 0.0) return null

        val medTimesAtUv1 = mapOf(
            SkinType.TYPE1 to 150.0,
            SkinType.TYPE2 to 250.0,
            SkinType.TYPE3 to 425.0,
            SkinType.TYPE4 to 600.0,
            SkinType.TYPE5 to 850.0,
            SkinType.TYPE6 to 1100.0
        )

        val medTime = medTimesAtUv1[skinType] ?: return null
        val uvToUse = max(currentUv, 0.1)
        val fullMed = medTime / uvToUse
        return max(1, fullMed.toInt())
    }
}