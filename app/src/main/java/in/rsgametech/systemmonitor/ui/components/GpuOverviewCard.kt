package `in`.rsgametech.systemmonitor.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import `in`.rsgametech.systemmonitor.data.model.GpuInfo

@Composable
fun GpuOverviewCard(gpu: GpuInfo) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(gpu.name, style = MaterialTheme.typography.titleSmall)
                    Text(gpu.vendor, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                gpu.utilizationPercent?.let { util ->
                    Text(
                        "${util}%",
                        style = MaterialTheme.typography.headlineSmall,
                        color = usageColor(util.toFloat())
                    )
                }
            }

            gpu.utilizationPercent?.let { util ->
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { util / 100f },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = usageColor(util.toFloat())
                )
            }

            Spacer(Modifier.height(8.dp))

            if (gpu.memoryTotalMb > 0) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("VRAM", style = MaterialTheme.typography.labelMedium)
                    Text("${gpu.memoryUsagePercent}%", style = MaterialTheme.typography.labelMedium)
                }
                Spacer(Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { gpu.memoryUsagePercent / 100f },
                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                    color = usageColor(gpu.memoryUsagePercent)
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    "${gpu.memoryUsedMb} MB / ${gpu.memoryTotalMb} MB",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            HorizontalDivider(Modifier.padding(vertical = 8.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                gpu.temperatureCelsius?.let { temp ->
                    GpuStatItem("Temp", "${temp}\u00B0C")
                }
                gpu.fanSpeedPercent?.let { fan ->
                    GpuStatItem("Fan", "${fan}%")
                }
                gpu.powerDrawWatts?.let { power ->
                    GpuStatItem("Power", "${power}W")
                }
                gpu.clockSpeedMhz?.let { clock ->
                    GpuStatItem("Clock", "${clock} MHz")
                }
            }
        }
    }
}

@Composable
private fun GpuStatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}
