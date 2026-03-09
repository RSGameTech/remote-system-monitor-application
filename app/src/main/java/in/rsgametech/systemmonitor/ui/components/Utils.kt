package `in`.rsgametech.systemmonitor.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import java.util.Locale

@Composable
fun usageColor(percent: Float): Color = when {
    percent > 90f -> MaterialTheme.colorScheme.error
    percent > 70f -> MaterialTheme.colorScheme.tertiary
    else -> MaterialTheme.colorScheme.primary
}

fun formatSpeed(mbps: Double): String = when {
    mbps < 0.001 -> "0 B/s"
    mbps < 1.0 -> String.format(Locale.US, "%.0f KB/s", mbps * 1024)
    else -> String.format(Locale.US, "%.2f MB/s", mbps)
}

fun formatNumber(n: Long): String {
    return java.text.NumberFormat.getIntegerInstance().format(n)
}
