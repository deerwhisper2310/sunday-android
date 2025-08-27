package io.block.goose.sunday.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UvResponse(
    val latitude: Double,
    val longitude: Double,
    @SerialName("generationtime_ms")
    val generationTimeMs: Double,
    @SerialName("utc_offset_seconds")
    val utcOffsetSeconds: Int,
    val timezone: String,
    @SerialName("timezone_abbreviation")
    val timezoneAbbreviation: String,
    val elevation: Double,
    @SerialName("daily_units")
    val dailyUnits: DailyUnits,
    val daily: Daily,
    @SerialName("hourly_units")
    val hourlyUnits: HourlyUnits,
    val hourly: Hourly
)

@Serializable
data class Daily(
    val time: List<String>,
    @SerialName("uv_index_max")
    val uvIndexMax: List<Double?>,
    @SerialName("uv_index_clear_sky_max")
    val uvIndexClearSkyMax: List<Double?>,
    val sunrise: List<String?>,
    val sunset: List<String?>,
)

@Serializable
data class DailyUnits(
    val time: String,
    @SerialName("uv_index_max")
    val uvIndexMax: String,
    @SerialName("uv_index_clear_sky_max")
    val uvIndexClearSkyMax: String,
    val sunrise: String,
    val sunset: String,
)

@Serializable
data class Hourly(
    val time: List<String>,
    @SerialName("uv_index")
    val uvIndex: List<Double?>,
    @SerialName("cloud_cover")
    val cloudCover: List<Int?>,
)

@Serializable
data class HourlyUnits(
    val time: String,
    @SerialName("uv_index")
    val uvIndex: String,
    @SerialName("cloud_cover")
    val cloudCover: String,
)
