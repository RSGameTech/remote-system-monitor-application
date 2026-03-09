package `in`.rsgametech.systemmonitor.data.repository

import `in`.rsgametech.systemmonitor.data.model.MetricsResponse
import `in`.rsgametech.systemmonitor.data.remote.MonitorApi

class MonitorRepository(private val api: MonitorApi) {

    suspend fun fetchMetrics(): Result<MetricsResponse> = runCatching {
        val response = api.getMetrics()
        when {
            response.isSuccessful -> response.body() ?: throw Exception("Empty response")
            response.code() == 401 -> throw AuthException("Invalid API key")
            else -> throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }
}

class AuthException(message: String) : Exception(message)
