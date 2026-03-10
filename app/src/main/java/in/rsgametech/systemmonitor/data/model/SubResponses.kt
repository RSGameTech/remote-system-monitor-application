package `in`.rsgametech.systemmonitor.data.model

import com.google.gson.annotations.SerializedName

data class CpuResponse(val timestamp: String, val cpu: CpuInfo)
data class MemoryResponse(val timestamp: String, val memory: MemoryInfo)
data class GpuResponse(val timestamp: String, val gpu: List<GpuInfo>)
data class DiskResponse(val timestamp: String, val disk: List<DiskInfo>)
data class NetworkResponse(val timestamp: String, val network: NetworkInfo)

data class RootResponse(
    val status: String,
    val message: String,
    val version: String,
    val endpoints: List<String>
)

data class ErrorResponse(val error: String)

data class ProcessInfo(
    val pid: Long,
    val name: String,
    @SerializedName("cpu_percent") val cpuPercent: Float,
    @SerializedName("memory_percent") val memoryPercent: Float,
    val status: String
)

data class ProcessListResponse(
    val timestamp: String,
    val processes: List<ProcessInfo>
)

data class KillResponse(
    val success: Boolean,
    val message: String
)
