package io.block.goose.sunday.data.repository

import io.block.goose.sunday.data.local.dao.UserPreferencesDao
import io.block.goose.sunday.data.local.entity.UserPreferences
import kotlinx.coroutines.flow.Flow

class UserPreferencesRepository(private val userPreferencesDao: UserPreferencesDao) {

    fun getPreferences(): Flow<UserPreferences?> = userPreferencesDao.getPreferences()

    suspend fun savePreferences(preferences: UserPreferences) {
        userPreferencesDao.savePreferences(preferences)
    }
}
