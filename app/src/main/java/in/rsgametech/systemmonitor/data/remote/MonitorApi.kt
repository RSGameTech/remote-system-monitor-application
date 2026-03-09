package `in`.rsgametech.systemmonitor.data.remote

import `in`.rsgametech.systemmonitor.data.model.HealthResponse
import `in`.rsgametech.systemmonitor.data.model.MetricsResponse
import retrofit2.Response
import retrofit2.http.GET

interface MonitorApi {
    @GET("/health")
    suspend fun getHealth(): Response<HealthResponse>

    @GET("/metrics")
    suspend fun getMetrics(): Response<MetricsResponse>
}
