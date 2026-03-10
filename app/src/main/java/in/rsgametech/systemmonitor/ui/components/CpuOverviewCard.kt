package `in`.rsgametech.systemmonitor.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import `in`.rsgametech.systemmonitor.data.model.CpuInfo
import `in`.rsgametech.systemmonitor.data.model.TemperatureInfo

@Composable
fun CpuOverviewCard(
    cpu: CpuInfo,
    temperatures: List<TemperatureInfo> = emptyList(),
    onClick: (() -> Unit)? = null
) {
    val cpuTemp = temperatures
        .firstOrNull { it.component.contains("CPU", ignoreCase = true) || it.component.contains("Package", ignoreCase = true) }
        ?.temperatureCelsius
        ?: cpu.temperatureCelsius
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("CPU", style = MaterialTheme.typography.titleMedium)
                Text(
                    "${cpu.usagePercent}%",
                    style = MaterialTheme.typography.headlineSmall,
                    color = usageColor(cpu.usagePercent)
                )
            }

            Spacer(Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { cpu.usagePercent / 100f },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = usageColor(cpu.usagePercent)
            )

            Spacer(Modifier.height(8.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "${cpu.coreCountPhysical}C / ${cpu.coreCountLogical}T @ ${cpu.frequencyMhz} MHz",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                cpuTemp?.let { temp ->
                    val unit = LocalTemperatureUnit.current
                    Text(
                        formatTemperature(temp, unit),
                        style = MaterialTheme.typography.bodySmall,
                        color = usageColor(temp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Text("Per Core", style = MaterialTheme.typography.labelMedium)
            Spacer(Modifier.height(4.dp))

            val chunked = cpu.perCorePercent.chunked(2)
            chunked.forEachIndexed { rowIdx, pair ->
                Row(
                    Modifier.fillMaxWidth().padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    pair.forEachIndexed { colIdx, usage ->
                        val coreIdx = rowIdx * 2 + colIdx
                        Row(
                            Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "$coreIdx",
                                modifier = Modifier.width(24.dp),
                                style = MaterialTheme.typography.labelSmall
                            )
                            LinearProgressIndicator(
                                progress = { usage / 100f },
                                modifier = Modifier.weight(1f).height(6.dp).clip(RoundedCornerShape(3.dp)),
                                color = usageColor(usage)
                            )
                            Text(
                                "${usage.toInt()}%",
                                modifier = Modifier.width(36.dp).padding(start = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                textAlign = TextAlign.End
                            )
                        }
                    }
                    if (pair.size == 1) {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
