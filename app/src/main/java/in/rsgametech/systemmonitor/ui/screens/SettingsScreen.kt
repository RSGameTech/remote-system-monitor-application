package `in`.rsgametech.systemmonitor.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import `in`.rsgametech.systemmonitor.model.AppearanceSettings
import `in`.rsgametech.systemmonitor.model.TemperatureUnit

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAppearance: () -> Unit,
    onNavigateToAbout: () -> Unit,
    settings: AppearanceSettings,
    onSettingsChange: (AppearanceSettings) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showTemperatureDialog by remember { mutableStateOf(false) }

    // Language picker dialog
    if (showLanguageDialog) {
        val languages = listOf("System Default", "English")
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text("Default Language") },
            text = {
                Column {
                    languages.forEach { lang ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    onSettingsChange(settings.copy(language = lang))
                                    showLanguageDialog = false
                                }
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = settings.language == lang,
                                onClick = {
                                    onSettingsChange(settings.copy(language = lang))
                                    showLanguageDialog = false
                                }
                            )
                            Text(
                                text = lang,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Temperature unit picker dialog
    if (showTemperatureDialog) {
        AlertDialog(
            onDismissRequest = { showTemperatureDialog = false },
            title = { Text("Temperature Unit") },
            text = {
                Column {
                    TemperatureUnit.entries.forEach { unit ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    onSettingsChange(settings.copy(temperatureUnit = unit))
                                    showTemperatureDialog = false
                                }
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = settings.temperatureUnit == unit,
                                onClick = {
                                    onSettingsChange(settings.copy(temperatureUnit = unit))
                                    showTemperatureDialog = false
                                }
                            )
                            Text(
                                text = unit.displayName,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showTemperatureDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                title = { Text("Settings") },
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
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = innerPadding
        ) {
            // General section
            item {
                SectionLabel("General")
                Spacer(Modifier.height(8.dp))
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    ListItem(
                        headlineContent = { Text("Default Language") },
                        supportingContent = { Text(settings.language) },
                        leadingContent = {
                            Icon(Icons.Outlined.Language, contentDescription = null)
                        },
                        colors = ListItemDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                        ),
                        modifier = Modifier
                            .clip(segmentedShape(0, 1))
                            .clickable { showLanguageDialog = true }
                    )
                    ListItem(
                        headlineContent = { Text("Temperature Unit") },
                        supportingContent = { Text(settings.temperatureUnit.displayName) },
                        leadingContent = {
                            Icon(Icons.Outlined.Thermostat, contentDescription = null)
                        },
                        colors = ListItemDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                        ),
                        modifier = Modifier
                            .clip(segmentedShape(1, 1))
                            .clickable { showTemperatureDialog = true }
                    )
                }
            }

            item { Spacer(Modifier.height(24.dp)) }

            // Appearance section
            item {
                SectionLabel("Appearance")
                Spacer(Modifier.height(8.dp))
            }
            item {
                ListItem(
                    headlineContent = { Text("Appearance") },
                    supportingContent = { Text("Theme, colors, and display") },
                    leadingContent = {
                        Icon(Icons.Default.Palette, contentDescription = null)
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(OuterCornerRadius))
                        .clickable { onNavigateToAppearance() }
                )
            }

            item { Spacer(Modifier.height(24.dp)) }

            // About section
            item {
                ListItem(
                    headlineContent = { Text("About") },
                    leadingContent = {
                        Icon(Icons.Outlined.Info, contentDescription = "About")
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(OuterCornerRadius))
                        .clickable { onNavigateToAbout() }
                )
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
