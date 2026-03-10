package `in`.rsgametech.systemmonitor.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import `in`.rsgametech.systemmonitor.data.model.CpuInfo
import `in`.rsgametech.systemmonitor.data.model.TemperatureInfo
import `in`.rsgametech.systemmonitor.ui.components.InfoRow
import `in`.rsgametech.systemmonitor.ui.components.LocalTemperatureUnit
import `in`.rsgametech.systemmonitor.ui.components.formatTemperature
import `in`.rsgametech.systemmonitor.ui.components.usageColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CpuDetailScreen(
    cpu: CpuInfo,
    temperatures: List<TemperatureInfo> = emptyList(),
    onBack: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                title = { Text("CPU Details") },
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                ElevatedCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Overall Usage", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "${cpu.usagePercent}%",
                            style = MaterialTheme.typography.displaySmall,
                            color = usageColor(cpu.usagePercent)
                        )
                        Spacer(Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { cpu.usagePercent / 100f },
                            modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(6.dp)),
                            color = usageColor(cpu.usagePercent)
                        )
                        Spacer(Modifier.height(8.dp))
                        InfoRow("Physical Cores", "${cpu.coreCountPhysical}")
                        InfoRow("Logical Cores", "${cpu.coreCountLogical}")
                        InfoRow("Frequency", "${cpu.frequencyMhz} MHz")
                        val cpuTemp = temperatures
                            .firstOrNull { it.component.contains("CPU", ignoreCase = true) || it.component.contains("Package", ignoreCase = true) }
                            ?.temperatureCelsius
                            ?: cpu.temperatureCelsius
                        cpuTemp?.let { temp ->
                            val unit = LocalTemperatureUnit.current
                            InfoRow("Temperature", formatTemperature(temp, unit))
                        }
                    }
                }
            }

            item {
                Text(
                    "Per-Core Usage",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            itemsIndexed(cpu.perCorePercent) { index, usage ->
                ElevatedCard(Modifier.fillMaxWidth()) {
                    Row(
                        Modifier
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Core $index",
                            modifier = Modifier.width(64.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        LinearProgressIndicator(
                            progress = { usage / 100f },
                            modifier = Modifier.weight(1f).height(10.dp).clip(RoundedCornerShape(5.dp)),
                            color = usageColor(usage)
                        )
                        Text(
                            "${usage}%",
                            modifier = Modifier.width(56.dp).padding(start = 8.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.End,
                            color = usageColor(usage)
                        )
                    }
                }
            }
        }
    }
}
