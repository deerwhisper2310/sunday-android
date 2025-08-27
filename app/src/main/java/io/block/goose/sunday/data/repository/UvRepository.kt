package io.block.goose.sunday.data.repository

import io.block.goose.sunday.data.remote.UvResponse
import io.block.goose.sunday.network.RetrofitClient

class UvRepository {
    private val apiService = RetrofitClient.apiService

    suspend fun getUvData(latitude: Double, longitude: Double): Result<UvResponse> {
        return try {
            val response = apiService.getUvData(latitude = latitude, longitude = longitude)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
