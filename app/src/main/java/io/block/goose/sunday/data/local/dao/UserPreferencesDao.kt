package io.block.goose.sunday.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import io.block.goose.sunday.data.local.entity.UserPreferences
import kotlinx.coroutines.flow.Flow

@Dao
interface UserPreferencesDao {
    @Query("SELECT * FROM user_preferences WHERE id = 1")
    fun getPreferences(): Flow<UserPreferences?>

    @Upsert
    suspend fun savePreferences(preferences: UserPreferences)
}
