package `in`.rsgametech.systemmonitor.model

enum class ThemeMode { SYSTEM, DARK, LIGHT }

enum class FontChoice(val displayName: String) {
    SYSTEM("System"),
    JETBRAINS_MONO("JetBrains Mono"),
    FIRA_MONO("Fira Mono"),
    RUBIK("Rubik")
}

data class AppearanceSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val amoledMode: Boolean = false,
    val fontChoice: FontChoice = FontChoice.SYSTEM,
    val materialYou: Boolean = true
)
