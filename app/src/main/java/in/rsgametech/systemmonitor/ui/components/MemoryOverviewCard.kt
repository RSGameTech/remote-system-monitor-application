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
import `in`.rsgametech.systemmonitor.data.model.MemoryInfo

@Composable
fun MemoryOverviewCard(memory: MemoryInfo) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Memory", style = MaterialTheme.typography.titleMedium)
                Text(
                    "${memory.usagePercent}%",
                    style = MaterialTheme.typography.headlineSmall,
                    color = usageColor(memory.usagePercent)
                )
            }

            Spacer(Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { memory.usagePercent / 100f },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = usageColor(memory.usagePercent)
            )

            Spacer(Modifier.height(4.dp))

            Text(
                "${memory.usedGb} GB used / ${memory.totalGb} GB total (${memory.availableGb} GB available)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (memory.swapTotalGb > 0) {
                HorizontalDivider(Modifier.padding(vertical = 8.dp))

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Swap", style = MaterialTheme.typography.labelMedium)
                    Text("${memory.swapPercent}%", style = MaterialTheme.typography.labelMedium)
                }

                Spacer(Modifier.height(4.dp))

                LinearProgressIndicator(
                    progress = { memory.swapPercent / 100f },
                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                    color = usageColor(memory.swapPercent)
                )

                Spacer(Modifier.height(2.dp))

                Text(
                    "${memory.swapUsedGb} GB / ${memory.swapTotalGb} GB",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
