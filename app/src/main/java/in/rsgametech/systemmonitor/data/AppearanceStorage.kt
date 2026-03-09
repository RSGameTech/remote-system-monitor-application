package `in`.rsgametech.systemmonitor.data

import android.content.Context
import `in`.rsgametech.systemmonitor.model.AppearanceSettings
import `in`.rsgametech.systemmonitor.model.FontChoice
import `in`.rsgametech.systemmonitor.model.TemperatureUnit
import `in`.rsgametech.systemmonitor.model.ThemeMode

class AppearanceStorage(context: Context) {

    private val prefs = context.getSharedPreferences("appearance", Context.MODE_PRIVATE)

    fun load(): AppearanceSettings = AppearanceSettings(
        themeMode = ThemeMode.valueOf(prefs.getString(KEY_THEME_MODE, ThemeMode.SYSTEM.name)!!),
        amoledMode = prefs.getBoolean(KEY_AMOLED, false),
        fontChoice = FontChoice.valueOf(prefs.getString(KEY_FONT, FontChoice.SYSTEM.name)!!),
        materialYou = prefs.getBoolean(KEY_MATERIAL_YOU, true),
        temperatureUnit = TemperatureUnit.valueOf(
            prefs.getString(KEY_TEMPERATURE_UNIT, TemperatureUnit.CELSIUS.name)!!
        ),
        language = prefs.getString(KEY_LANGUAGE, "System Default")!!
    )

    fun save(settings: AppearanceSettings) {
        prefs.edit()
            .putString(KEY_THEME_MODE, settings.themeMode.name)
            .putBoolean(KEY_AMOLED, settings.amoledMode)
            .putString(KEY_FONT, settings.fontChoice.name)
            .putBoolean(KEY_MATERIAL_YOU, settings.materialYou)
            .putString(KEY_TEMPERATURE_UNIT, settings.temperatureUnit.name)
            .putString(KEY_LANGUAGE, settings.language)
            .apply()
    }

    companion object {
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_AMOLED = "amoled"
        private const val KEY_FONT = "font"
        private const val KEY_MATERIAL_YOU = "material_you"
        private const val KEY_TEMPERATURE_UNIT = "temperature_unit"
        private const val KEY_LANGUAGE = "language"
    }
}
