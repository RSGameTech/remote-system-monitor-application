package `in`.rsgametech.systemmonitor.data.model

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
