package io.block.goose.sunday.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.block.goose.sunday.data.repository.UserPreferencesRepository
import io.block.goose.sunday.data.repository.UvRepository
import io.block.goose.sunday.services.LocationService

class MainViewModelFactory(
    private val context: Context,
    private val uvRepository: UvRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val locationService: LocationService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(locationService, uvRepository, userPreferencesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}