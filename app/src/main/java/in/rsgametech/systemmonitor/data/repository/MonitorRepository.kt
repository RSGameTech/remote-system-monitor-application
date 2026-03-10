package `in`.rsgametech.systemmonitor.data.repository

import `in`.rsgametech.systemmonitor.data.model.HealthResponse
import `in`.rsgametech.systemmonitor.data.model.KillResponse
import `in`.rsgametech.systemmonitor.data.model.MetricsResponse
import `in`.rsgametech.systemmonitor.data.model.ProcessListResponse
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

    suspend fun fetchHealth(): Result<HealthResponse> = runCatching {
        val response = api.getHealth()
        if (response.isSuccessful) response.body() ?: throw Exception("Empty response")
        else throw Exception("HTTP ${response.code()}")
    }

    suspend fun fetchProcesses(): Result<ProcessListResponse> = runCatching {
        val response = api.getProcesses()
        when {
            response.isSuccessful -> response.body() ?: throw Exception("Empty response")
            response.code() == 401 -> throw AuthException("Invalid API key")
            else -> throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }

    suspend fun killProcess(pid: Long): Result<KillResponse> = runCatching {
        val response = api.killProcess(pid)
        when {
            response.isSuccessful -> response.body() ?: throw Exception("Empty response")
            response.code() == 401 -> throw AuthException("Invalid API key")
            else -> throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
    }
}

class AuthException(message: String) : Exception(message)
