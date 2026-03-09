package `in`.rsgametech.systemmonitor.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.LocalCafe
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import `in`.rsgametech.systemmonitor.BuildConfig
import `in`.rsgametech.systemmonitor.R

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
fun AboutScreen(
    onNavigateBack: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val uriHandler = LocalUriHandler.current

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                title = { Text("About") },
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
            // App Info Card
            item {
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_launcher_foreground),
                            contentDescription = null,
                            modifier = Modifier
                                .size(72.dp)
                                .clip(MaterialTheme.shapes.large)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            stringResource(R.string.app_name),
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            "Version ${BuildConfig.VERSION_NAME}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Connect & Support section
            item {
                SectionHeader("Connect & Support")
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    ListItem(
                        headlineContent = { Text("Contact Developer") },
                        leadingContent = {
                            Icon(Icons.Outlined.Email, contentDescription = null)
                        },
                        trailingContent = {
                            Icon(Icons.AutoMirrored.Outlined.OpenInNew, contentDescription = null)
                        },
                        colors = ListItemDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                        ),
                        modifier = Modifier
                            .clip(segmentedShape(0, 2))
                            .clickable {
                                uriHandler.openUri("mailto:developer@rsgametech.in")
                            }
                    )
                    ListItem(
                        headlineContent = { Text("Buy Me a Coffee") },
                        leadingContent = {
                            Icon(Icons.Outlined.LocalCafe, contentDescription = null)
                        },
                        trailingContent = {
                            Icon(Icons.AutoMirrored.Outlined.OpenInNew, contentDescription = null)
                        },
                        colors = ListItemDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                        ),
                        modifier = Modifier
                            .clip(segmentedShape(1, 2))
                            .clickable {
                                uriHandler.openUri("https://buymeacoffee.com/rsgametech")
                            }
                    )
                    ListItem(
                        headlineContent = { Text("Visit GitHub") },
                        leadingContent = {
                            Icon(Icons.Outlined.Code, contentDescription = null)
                        },
                        trailingContent = {
                            Icon(Icons.AutoMirrored.Outlined.OpenInNew, contentDescription = null)
                        },
                        colors = ListItemDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                        ),
                        modifier = Modifier
                            .clip(segmentedShape(2, 2))
                            .clickable {
                                uriHandler.openUri("https://github.com/rsgametech")
                            }
                    )
                }
            }

            // Legal section
            item {
                SectionHeader("Legal")
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    ListItem(
                        headlineContent = { Text("Privacy Policy") },
                        leadingContent = {
                            Icon(Icons.Outlined.PrivacyTip, contentDescription = null)
                        },
                        trailingContent = {
                            Icon(Icons.AutoMirrored.Outlined.OpenInNew, contentDescription = null)
                        },
                        colors = ListItemDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                        ),
                        modifier = Modifier
                            .clip(segmentedShape(0, 1))
                            .clickable {
                                uriHandler.openUri("https://rsgametech.in/privacy")
                            }
                    )
                    ListItem(
                        headlineContent = { Text("Attributions") },
                        leadingContent = {
                            Icon(Icons.Outlined.Description, contentDescription = null)
                        },
                        colors = ListItemDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                        ),
                        modifier = Modifier
                            .clip(segmentedShape(1, 1))
                            .clickable { }
                    )
                }
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp)
    )
}
