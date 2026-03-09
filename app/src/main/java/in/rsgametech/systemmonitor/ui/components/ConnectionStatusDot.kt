package `in`.rsgametech.systemmonitor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import `in`.rsgametech.systemmonitor.viewmodel.ConnectionState

@Composable
fun ConnectionStatusDot(state: ConnectionState) {
    val color = when (state) {
        is ConnectionState.Connected -> Color(0xFF4CAF50)
        is ConnectionState.Connecting -> Color(0xFFFFC107)
        is ConnectionState.Error -> Color(0xFFF44336)
        is ConnectionState.Disconnected -> Color(0xFF9E9E9E)
    }

    Box(
        modifier = Modifier
            .padding(end = 16.dp)
            .size(12.dp)
            .clip(CircleShape)
            .background(color)
    )
}
