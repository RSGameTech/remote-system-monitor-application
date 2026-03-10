package `in`.rsgametech.systemmonitor.data.model

import com.google.gson.annotations.SerializedName

data class MetricsResponse(
    val timestamp: String,
    val system: SystemInfo,
    val cpu: CpuInfo,
    val memory: MemoryInfo,
    val gpu: List<GpuInfo>,
    val disk: List<DiskInfo>,
    val network: NetworkInfo,
    val temperatures: List<TemperatureInfo> = emptyList()
)

data class SystemInfo(
    val hostname: String,
    val os: String,
    @SerializedName("os_version") val osVersion: String,
    @SerializedName("kernel_version") val kernelVersion: String,
    val architecture: String,
    val uptime: String,
    @SerializedName("uptime_seconds") val uptimeSeconds: Long,
    @SerializedName("boot_time") val bootTime: String
)

data class CpuInfo(
    @SerializedName("usage_percent") val usagePercent: Float,
    @SerializedName("core_count_logical") val coreCountLogical: Int,
    @SerializedName("core_count_physical") val coreCountPhysical: Int,
    @SerializedName("frequency_mhz") val frequencyMhz: Long,
    @SerializedName("per_core_percent") val perCorePercent: List<Float>,
    @SerializedName("temperature_celsius") val temperatureCelsius: Float? = null
)

data class MemoryInfo(
    @SerializedName("total_gb") val totalGb: Double,
    @SerializedName("used_gb") val usedGb: Double,
    @SerializedName("available_gb") val availableGb: Double,
    @SerializedName("usage_percent") val usagePercent: Float,
    @SerializedName("swap_total_gb") val swapTotalGb: Double,
    @SerializedName("swap_used_gb") val swapUsedGb: Double,
    @SerializedName("swap_percent") val swapPercent: Float
)

data class GpuInfo(
    val index: Int,
    val name: String,
    val vendor: String,
    @SerializedName("temperature_celsius") val temperatureCelsius: Int?,
    @SerializedName("utilization_percent") val utilizationPercent: Int?,
    @SerializedName("memory_total_mb") val memoryTotalMb: Long,
    @SerializedName("memory_used_mb") val memoryUsedMb: Long,
    @SerializedName("memory_usage_percent") val memoryUsagePercent: Float,
    @SerializedName("fan_speed_percent") val fanSpeedPercent: Int?,
    @SerializedName("power_draw_watts") val powerDrawWatts: Double?,
    @SerializedName("clock_speed_mhz") val clockSpeedMhz: Int?
)

data class DiskInfo(
    val name: String,
    val mountpoint: String,
    @SerializedName("file_system") val fileSystem: String,
    @SerializedName("total_gb") val totalGb: Double,
    @SerializedName("used_gb") val usedGb: Double,
    @SerializedName("free_gb") val freeGb: Double,
    @SerializedName("usage_percent") val usagePercent: Float,
    @SerializedName("is_removable") val isRemovable: Boolean
)

data class NetworkInfo(
    @SerializedName("upload_speed_mbps") val uploadSpeedMbps: Double,
    @SerializedName("download_speed_mbps") val downloadSpeedMbps: Double,
    @SerializedName("total_sent_gb") val totalSentGb: Double,
    @SerializedName("total_recv_gb") val totalRecvGb: Double,
    @SerializedName("packets_sent") val packetsSent: Long,
    @SerializedName("packets_recv") val packetsRecv: Long
)

data class HealthResponse(
    val status: String,
    val timestamp: String,
    val version: String
)

data class TemperatureInfo(
    val component: String,
    @SerializedName("temperature_celsius") val temperatureCelsius: Float,
    @SerializedName("max_celsius") val maxCelsius: Float? = null,
    @SerializedName("critical_celsius") val criticalCelsius: Float? = null
)
