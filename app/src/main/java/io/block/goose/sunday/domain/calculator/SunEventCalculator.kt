package io.block.goose.sunday.domain.calculator

import java.util.Calendar
import java.util.Date
import kotlin.math.*

// Simplified implementation of the sunrise/sunset algorithm.
// This provides good enough accuracy for the purpose of scheduling notifications.
object SunEventCalculator {

    data class SunEvents(val sunrise: Date, val solarNoon: Date, val sunset: Date)

    fun calculate(latitude: Double, longitude: Double, date: Date, timezoneId: String): SunEvents {
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
            sunrise = dateFromFractionalDay(calendar, sunriseTime, timezoneId),
            solarNoon = dateFromFractionalDay(calendar, solarNoonTime, timezoneId),
            sunset = dateFromFractionalDay(calendar, sunsetTime, timezoneId)
        )
    }

    private fun dateFromFractionalDay(calendar: Calendar, fractionalDay: Double, timezoneId: String): Date {
        val targetZone = java.util.TimeZone.getTimeZone(timezoneId)

        // Create a Calendar instance for the target timezone for the DATE part.
        // This 'cal' will represent midnight of the target day in the target timezone.
        val cal = Calendar.getInstance(targetZone).apply {
            set(Calendar.YEAR, calendar.get(Calendar.YEAR))
            set(Calendar.MONTH, calendar.get(Calendar.MONTH))
            set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // fractionalDay is typically minutes from UTC midnight.
        // Convert fractionalDay to total minutes from UTC midnight.
        val totalMinutesFromUTCMidnight = (fractionalDay * 24 * 60).toInt()

        // Get the offset of the target timezone from UTC *for this specific date/time*.
        // The offset is in milliseconds. For EDT (UTC-4), this will be -14400000 ms = -240 minutes.
        val offsetMillis = targetZone.getOffset(cal.timeInMillis)
        val offsetMinutes = offsetMillis / (1000 * 60)

        // To convert the UTC-relative 'totalMinutesFromUTCMidnight' into minutes relative
        // to the 'targetZone's midnight, we add the offset.
        val minutesFromTargetZoneMidnight = totalMinutesFromUTCMidnight + offsetMinutes

        cal.add(Calendar.MINUTE, minutesFromTargetZoneMidnight)
        return cal.time
    }
}
