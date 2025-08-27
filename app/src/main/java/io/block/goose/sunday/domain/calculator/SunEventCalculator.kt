
package io.block.goose.sunday.domain.calculator

import java.util.Calendar
import java.util.Date
import kotlin.math.*

// Simplified implementation of the sunrise/sunset algorithm.
// This provides good enough accuracy for the purpose of scheduling notifications.
object SunEventCalculator {

    data class SunEvents(val sunrise: Date, val solarNoon: Date, val sunset: Date)

    fun calculate(latitude: Double, longitude: Double, date: Date): SunEvents {
        val calendar = Calendar.getInstance().apply { time = date }
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)

        // 1. First calculate the day angle
        val dayAngle = (2 * PI / 365.0) * (dayOfYear - 1)

        // 2. Calculate the equation of time
        val equationOfTime = 229.18 * (0.000075 + 0.001868 * cos(dayAngle) - 0.032077 * sin(dayAngle) - 0.014615 * cos(2 * dayAngle) - 0.040849 * sin(2 * dayAngle))

        // 3. Calculate solar declination
        val solarDeclination = 0.006918 - 0.399912 * cos(dayAngle) + 0.070257 * sin(dayAngle) - 0.006758 * cos(2 * dayAngle) + 0.000907 * sin(2 * dayAngle) - 0.002697 * cos(3 * dayAngle) + 0.00148 * sin(3 * dayAngle)

        // 4. Calculate hour angle
        val hourAngle = acos(cos(Math.toRadians(90.833)) / (cos(Math.toRadians(latitude)) * cos(solarDeclination)) - tan(Math.toRadians(latitude)) * tan(solarDeclination))

        // 5. Calculate sunrise and sunset
        val solarNoonTime = (720 - 4 * longitude - equationOfTime) / 1440.0
        val sunriseTime = solarNoonTime - Math.toDegrees(hourAngle) * 4 / 1440.0
        val sunsetTime = solarNoonTime + Math.toDegrees(hourAngle) * 4 / 1440.0

        return SunEvents(
            sunrise = dateFromFractionalDay(calendar, sunriseTime),
            solarNoon = dateFromFractionalDay(calendar, solarNoonTime),
            sunset = dateFromFractionalDay(calendar, sunsetTime)
        )
    }

    private fun dateFromFractionalDay(calendar: Calendar, fractionalDay: Double): Date {
        val cal = calendar.clone() as Calendar
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)

        val totalMinutes = (fractionalDay * 24 * 60).toInt()
        cal.add(Calendar.MINUTE, totalMinutes)
        return cal.time
    }
}
