package `in`.rsgametech.systemmonitor.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.DesktopWindows
import androidx.compose.material.icons.twotone.Laptop
import androidx.compose.ui.graphics.vector.ImageVector

data class MonitorItem(
    val id: Int,
    val iconType: IconType,
    val label: String,
    val supportingText: String,
    val apiKey: String = ""
)

enum class IconType(val displayName: String, val icon: ImageVector) {
    LAPTOP("Laptop", Icons.TwoTone.Laptop),
    DESKTOP("Desktop", Icons.TwoTone.DesktopWindows)
}
