package `in`.rsgametech.systemmonitor.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import `in`.rsgametech.systemmonitor.data.model.SystemInfo

@Composable
fun SystemInfoCard(system: SystemInfo) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(system.hostname, style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(4.dp))
            InfoRow("OS", "${system.os} ${system.osVersion}")
            InfoRow("Kernel", system.kernelVersion)
            InfoRow("Arch", system.architecture)
            InfoRow("Uptime", system.uptime)
            InfoRow("Boot time", system.bootTime)
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodySmall)
    }
}
