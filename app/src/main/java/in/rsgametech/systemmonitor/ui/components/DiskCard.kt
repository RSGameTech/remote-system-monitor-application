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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import `in`.rsgametech.systemmonitor.data.model.DiskInfo

@Composable
fun DiskCard(disk: DiskInfo) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        disk.name.ifEmpty { disk.mountpoint },
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        buildString {
                            append(disk.fileSystem)
                            if (disk.name.isNotEmpty()) append(" \u2014 ${disk.mountpoint}")
                            if (disk.isRemovable) append(" (removable)")
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    "${disk.usagePercent}%",
                    style = MaterialTheme.typography.titleMedium,
                    color = usageColor(disk.usagePercent)
                )
            }

            Spacer(Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { disk.usagePercent / 100f },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                color = usageColor(disk.usagePercent)
            )

            Spacer(Modifier.height(4.dp))

            Text(
                "${disk.usedGb} GB used / ${disk.totalGb} GB total (${disk.freeGb} GB free)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
