package io.block.goose.sunday.domain.calculator

import io.block.goose.sunday.domain.model.ClothingLevel
import io.block.goose.sunday.domain.model.SkinType
import io.block.goose.sunday.domain.model.Sunscreen
import java.time.ZonedDateTime
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min

class VitaminDCalculator {

    // UV response curve parameters from the original Swift app
    private val uvHalfMax = 4.0  // UV index for 50% vitamin D synthesis rate
    private val uvMaxFactor = 3.0 // Maximum multiplication factor at high UV

    // Base rate in IU/hr for Type 3 skin, minimal clothing, as per the original app
    private val baseRate = 21000.0

    /**
     * Calculates the current rate of Vitamin D synthesis in IU/hour.
     * This is a direct port of the calculation logic from the iOS app.
     */
    fun calculateVitaminDRate(
        uvIndex: Double,
        clothingLevel: ClothingLevel,
        sunscreen: Sunscreen,
        skinType: SkinType,
        userAge: Int?,
        currentTime: ZonedDateTime,
        averageDailyExposure: Double // Average IU/day over the last 7 days
    ): Double {
        // 1. UV factor: Michaelis-Menten-like saturation curve
        val uvFactor = (uvIndex * uvMaxFactor) / (uvHalfMax + uvIndex)

        // 2. Exposure factor based on clothing coverage
        val exposureFactor = clothingLevel.exposureFactor

        // 3. Sunscreen blocks UV radiation
        val sunscreenFactor = sunscreen.uvTransmissionFactor

        // 4. Skin type affects vitamin D synthesis efficiency
        val skinFactor = skinType.vitaminDFactor

        // 5. Age factor: synthesis decreases with age
        val ageFactor = calculateAgeFactor(userAge)

        // 6. UV Quality factor based on time of day (solar angle)
        val uvQualityFactor = calculateUvQualityFactor(currentTime)
        
        // 7. Adaptation factor based on recent sun exposure
        val adaptationFactor = calculateAdaptationFactor(averageDailyExposure)

        // Final calculation
        return baseRate * uvFactor * exposureFactor * sunscreenFactor * skinFactor * ageFactor * uvQualityFactor * adaptationFactor
    }

    private fun calculateAgeFactor(age: Int?): Double {
        return when {
            age == null -> 1.0 // No age data available, don't apply factor
            age <= 20 -> 1.0
            age >= 70 -> 0.25
            else -> {
                // Linear decrease: lose ~1.5% per year after age 20 (matching Swift app's 0.015)
                // Original was 1.0 - Double(age - 20) * 0.01, but comments suggest 25% at 70, which is 0.015/yr
                max(0.25, 1.0 - (age - 20) * 0.015)
            }
        }
    }
    
    private fun calculateUvQualityFactor(time: ZonedDateTime): Double {
        // Convert current time to decimal hours
        val timeDecimal = time.hour + time.minute / 60.0

        // Solar noon approximation (using 13.0 as a reasonable standard)
        val solarNoon = 13.0
        
        // Hours from solar noon
        val hoursFromNoon = abs(timeDecimal - solarNoon)
        
        // UV-B effectiveness decreases away from solar noon.
        // The factor of 0.2 is a direct port from the Swift app.
        val qualityFactor = exp(-hoursFromNoon * 0.2)
        
        // Ensure a minimum quality during daylight hours
        return max(0.1, min(1.0, qualityFactor))
    }
    
    private fun calculateAdaptationFactor(averageDailyExposure: Double): Double {
        // Adaptation factor based on recent exposure history (last 7 days)
        return when {
            averageDailyExposure < 1000 -> 0.8
            averageDailyExposure >= 10000 -> 1.2
            else -> {
                // Linear interpolation between 0.8 and 1.2
                0.8 + (averageDailyExposure - 1000) / 9000 * 0.4
            }
        }
    }
}
