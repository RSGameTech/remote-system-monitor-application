package `in`.rsgametech.systemmonitor.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import `in`.rsgametech.systemmonitor.data.model.GpuInfo
import `in`.rsgametech.systemmonitor.ui.components.InfoRow
import `in`.rsgametech.systemmonitor.ui.components.LocalTemperatureUnit
import `in`.rsgametech.systemmonitor.ui.components.formatTemperature
import `in`.rsgametech.systemmonitor.ui.components.usageColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GpuDetailScreen(gpus: List<GpuInfo>, onBack: () -> Unit) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                title = { Text("GPU Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors()
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(gpus, key = { it.index }) { gpu ->
                ElevatedCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text(gpu.name, style = MaterialTheme.typography.titleMedium)
                        Text(
                            "${gpu.vendor} — GPU #${gpu.index}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(Modifier.height(12.dp))

                        // Utilization
                        gpu.utilizationPercent?.let { util ->
                            Text("Utilization", style = MaterialTheme.typography.labelMedium)
                            Text(
                                "${util}%",
                                style = MaterialTheme.typography.displaySmall,
                                color = usageColor(util.toFloat())
                            )
                            Spacer(Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { util / 100f },
                                modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(6.dp)),
                                color = usageColor(util.toFloat())
                            )
                            Spacer(Modifier.height(12.dp))
                        }

                        // VRAM
                        if (gpu.memoryTotalMb > 0) {
                            Text("VRAM", style = MaterialTheme.typography.labelMedium)
                            Text(
                                "${gpu.memoryUsagePercent}%",
                                style = MaterialTheme.typography.headlineSmall,
                                color = usageColor(gpu.memoryUsagePercent)
                            )
                            Spacer(Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { gpu.memoryUsagePercent / 100f },
                                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                                color = usageColor(gpu.memoryUsagePercent)
                            )
                            Spacer(Modifier.height(4.dp))
                            InfoRow("Total", "${gpu.memoryTotalMb} MB")
                            InfoRow("Used", "${gpu.memoryUsedMb} MB")
                            InfoRow("Free", "${gpu.memoryTotalMb - gpu.memoryUsedMb} MB")
                            Spacer(Modifier.height(12.dp))
                        }

                        // Stats
                        HorizontalDivider(Modifier.padding(vertical = 4.dp))
                        gpu.temperatureCelsius?.let {
                            val unit = LocalTemperatureUnit.current
                            InfoRow("Temperature", formatTemperature(it, unit))
                        }
                        gpu.fanSpeedPercent?.let { InfoRow("Fan Speed", "${it}%") }
                        gpu.powerDrawWatts?.let { InfoRow("Power Draw", "${it} W") }
                        gpu.clockSpeedMhz?.let { InfoRow("Clock Speed", "${it} MHz") }
                    }
                }
            }
        }
    }
}
