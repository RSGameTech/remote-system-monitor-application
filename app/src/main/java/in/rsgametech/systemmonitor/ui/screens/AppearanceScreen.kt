package `in`.rsgametech.systemmonitor.ui.screens

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import `in`.rsgametech.systemmonitor.model.AppearanceSettings
import `in`.rsgametech.systemmonitor.model.FontChoice
import `in`.rsgametech.systemmonitor.model.ThemeMode
import `in`.rsgametech.systemmonitor.ui.theme.fontFamilyFor

private val OuterCornerRadius = 16.dp
private val InnerCornerRadius = 4.dp

private fun segmentedShape(index: Int, lastIndex: Int): RoundedCornerShape = when {
    lastIndex == 0 -> RoundedCornerShape(OuterCornerRadius)
    index == 0 -> RoundedCornerShape(
        topStart = OuterCornerRadius, topEnd = OuterCornerRadius,
        bottomStart = InnerCornerRadius, bottomEnd = InnerCornerRadius
    )
    index == lastIndex -> RoundedCornerShape(
        topStart = InnerCornerRadius, topEnd = InnerCornerRadius,
        bottomStart = OuterCornerRadius, bottomEnd = OuterCornerRadius
    )
    else -> RoundedCornerShape(InnerCornerRadius)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppearanceScreen(
    settings: AppearanceSettings,
    onSettingsChange: (AppearanceSettings) -> Unit,
    onNavigateBack: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val themeModes = listOf(
        ThemeMode.SYSTEM to "System",
        ThemeMode.DARK to "Dark",
        ThemeMode.LIGHT to "Light"
    )

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                title = { Text("Appearance") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors()
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(horizontal = 16.dp),
            contentPadding = innerPadding
        ) {
            // -- Theme Mode --
            item {
                SectionLabel("Theme Mode")
                Spacer(Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    themeModes.forEachIndexed { index, (mode, label) ->
                        ListItem(
                            headlineContent = { Text(label) },
                            trailingContent = {
                                RadioButton(
                                    selected = settings.themeMode == mode,
                                    onClick = { onSettingsChange(settings.copy(themeMode = mode)) }
                                )
                            },
                            colors = ListItemDefaults.colors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                            ),
                            modifier = Modifier.clip(segmentedShape(index, themeModes.lastIndex))
                        )
                    }
                }
            }

            item { Spacer(Modifier.height(12.dp)) }

            // AMOLED Mode
            item {
                ListItem(
                    headlineContent = { Text("AMOLED Mode") },
                    supportingContent = { Text("Pure black, only in dark mode") },
                    trailingContent = {
                        Switch(
                            checked = settings.amoledMode,
                            onCheckedChange = { onSettingsChange(settings.copy(amoledMode = it)) }
                        )
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    ),
                    modifier = Modifier.clip(RoundedCornerShape(OuterCornerRadius))
                )
            }

            item { Spacer(Modifier.height(24.dp)) }

            // -- Font --
            item {
                SectionLabel("Font")
                Spacer(Modifier.height(8.dp))

                val columns = if (LocalConfiguration.current.screenWidthDp >= 600) 4 else 2
                val spacing = 2.dp
                Column(verticalArrangement = Arrangement.spacedBy(spacing)) {
                    FontChoice.entries.chunked(columns).forEach { rowChoices ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(spacing),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            rowChoices.forEach { choice ->
                                ToggleButton(
                                    checked = settings.fontChoice == choice,
                                    onCheckedChange = {
                                        onSettingsChange(settings.copy(fontChoice = choice))
                                    },
                                    modifier = Modifier.weight(1f),
                                    shapes = ToggleButtonDefaults.shapes()
                                ) {
                                    Text(
                                        choice.displayName,
                                        fontFamily = fontFamilyFor(choice)
                                    )
                                }
                            }
                            repeat(columns - rowChoices.size) {
                                Spacer(Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(24.dp)) }

            // -- Color Scheme --
            item {
                SectionLabel("Color Scheme")
                Spacer(Modifier.height(8.dp))
            }

            // Material You - only show on API 31+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                item {
                    ListItem(
                        headlineContent = { Text("Material You") },
                        supportingContent = { Text("Dynamic colors from wallpaper") },
                        leadingContent = {
                            Icon(Icons.Outlined.Palette, contentDescription = null)
                        },
                        trailingContent = {
                            Switch(
                                checked = settings.materialYou,
                                onCheckedChange = { onSettingsChange(settings.copy(materialYou = it)) }
                            )
                        },
                        colors = ListItemDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                        ),
                        modifier = Modifier.clip(RoundedCornerShape(OuterCornerRadius))
                    )
                }
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 16.dp)
    )
}
