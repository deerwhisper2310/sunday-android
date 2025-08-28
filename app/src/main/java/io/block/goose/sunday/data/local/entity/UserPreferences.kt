package io.block.goose.sunday.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import io.block.goose.sunday.domain.model.ClothingLevel
import io.block.goose.sunday.domain.model.SkinType
import io.block.goose.sunday.domain.model.Sunscreen

@Entity(tableName = "user_preferences")
data class UserPreferences(
    @PrimaryKey val id: Int = 1,
    val skinType: SkinType = SkinType.TYPE2,
    val clothingLevel: ClothingLevel = ClothingLevel.LIGHT,
    val sunscreen: Sunscreen = Sunscreen.NONE,
    val age: Int = 30
)
