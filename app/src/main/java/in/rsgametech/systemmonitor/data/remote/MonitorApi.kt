package `in`.rsgametech.systemmonitor.data.remote

import `in`.rsgametech.systemmonitor.data.model.CpuResponse
import `in`.rsgametech.systemmonitor.data.model.DiskResponse
import `in`.rsgametech.systemmonitor.data.model.GpuResponse
import `in`.rsgametech.systemmonitor.data.model.HealthResponse
import `in`.rsgametech.systemmonitor.data.model.KillResponse
import `in`.rsgametech.systemmonitor.data.model.MemoryResponse
import `in`.rsgametech.systemmonitor.data.model.MetricsResponse
import `in`.rsgametech.systemmonitor.data.model.NetworkResponse
import `in`.rsgametech.systemmonitor.data.model.ProcessListResponse
import `in`.rsgametech.systemmonitor.data.model.RootResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MonitorApi {
    @GET("/")
    suspend fun getRoot(): Response<RootResponse>

    @GET("/health")
    suspend fun getHealth(): Response<HealthResponse>

    @GET("/metrics")
    suspend fun getMetrics(): Response<MetricsResponse>

    @GET("/metrics/cpu")
    suspend fun getCpu(): Response<CpuResponse>

    @GET("/metrics/memory")
    suspend fun getMemory(): Response<MemoryResponse>

    @GET("/metrics/gpu")
    suspend fun getGpu(): Response<GpuResponse>

    @GET("/metrics/disk")
    suspend fun getDisk(): Response<DiskResponse>

    @GET("/metrics/network")
    suspend fun getNetwork(): Response<NetworkResponse>

    @GET("/processes")
    suspend fun getProcesses(): Response<ProcessListResponse>

    @POST("/process/{pid}/kill")
    suspend fun killProcess(@Path("pid") pid: Long): Response<KillResponse>
}
