package `in`.rsgametech.systemmonitor.ui.components

import androidx.compose.runtime.compositionLocalOf
import `in`.rsgametech.systemmonitor.model.TemperatureUnit
import java.util.Locale

val LocalTemperatureUnit = compositionLocalOf { TemperatureUnit.CELSIUS }

fun formatTemperature(celsius: Number, unit: TemperatureUnit): String {
    return when (unit) {
        TemperatureUnit.CELSIUS -> "${celsius}\u00B0C"
        TemperatureUnit.FAHRENHEIT -> {
            val f = celsius.toDouble() * 9.0 / 5.0 + 32.0
            String.format(Locale.US, "%.0f\u00B0F", f)
        }
    }
}
