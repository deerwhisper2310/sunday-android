package io.block.goose.sunday.network

import io.block.goose.sunday.data.remote.UvResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("v1/forecast")
    suspend fun getUvData(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("elevation") elevation: Double = 0.0,
        @Query("daily") daily: String = "uv_index_max,uv_index_clear_sky_max,sunrise,sunset",
        @Query("hourly") hourly: String = "uv_index,cloud_cover",
        @Query("timezone") timezone: String = "auto",
        @Query("forecast_days") forecastDays: Int = 2
    ): UvResponse
}
