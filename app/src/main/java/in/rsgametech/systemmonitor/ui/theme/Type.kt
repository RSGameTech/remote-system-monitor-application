package `in`.rsgametech.systemmonitor.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import `in`.rsgametech.systemmonitor.R
import `in`.rsgametech.systemmonitor.model.FontChoice

private val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

private val JetBrainsMonoFamily = FontFamily(
    Font(googleFont = GoogleFont("JetBrains Mono"), fontProvider = provider)
)

private val FiraMonoFamily = FontFamily(
    Font(googleFont = GoogleFont("Fira Mono"), fontProvider = provider)
)

private val RubikFamily = FontFamily(
    Font(googleFont = GoogleFont("Rubik"), fontProvider = provider)
)

fun fontFamilyFor(fontChoice: FontChoice): FontFamily = when (fontChoice) {
    FontChoice.SYSTEM -> FontFamily.Default
    FontChoice.JETBRAINS_MONO -> JetBrainsMonoFamily
    FontChoice.FIRA_MONO -> FiraMonoFamily
    FontChoice.RUBIK -> RubikFamily
}

fun typographyWithFont(fontFamily: FontFamily): Typography {
    val default = Typography()
    return Typography(
        displayLarge = default.displayLarge.copy(fontFamily = fontFamily),
        displayMedium = default.displayMedium.copy(fontFamily = fontFamily),
        displaySmall = default.displaySmall.copy(fontFamily = fontFamily),
        headlineLarge = default.headlineLarge.copy(fontFamily = fontFamily),
        headlineMedium = default.headlineMedium.copy(fontFamily = fontFamily),
        headlineSmall = default.headlineSmall.copy(fontFamily = fontFamily),
        titleLarge = default.titleLarge.copy(fontFamily = fontFamily),
        titleMedium = default.titleMedium.copy(fontFamily = fontFamily),
        titleSmall = default.titleSmall.copy(fontFamily = fontFamily),
        bodyLarge = default.bodyLarge.copy(fontFamily = fontFamily),
        bodyMedium = default.bodyMedium.copy(fontFamily = fontFamily),
        bodySmall = default.bodySmall.copy(fontFamily = fontFamily),
        labelLarge = default.labelLarge.copy(fontFamily = fontFamily),
        labelMedium = default.labelMedium.copy(fontFamily = fontFamily),
        labelSmall = default.labelSmall.copy(fontFamily = fontFamily)
    )
}
