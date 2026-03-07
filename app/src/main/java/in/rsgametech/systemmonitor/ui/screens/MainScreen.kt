package `in`.rsgametech.systemmonitor.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import `in`.rsgametech.systemmonitor.model.MonitorItem
import `in`.rsgametech.systemmonitor.ui.components.AddItemForm

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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun MainScreen(
    items: List<MonitorItem>,
    onAddItem: (MonitorItem) -> Unit,
    onRefresh: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    var showAddForm by remember { mutableStateOf(false) }

    BackHandler(enabled = showAddForm) { showAddForm = false }

    SharedTransitionLayout {
        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Remote System Monitor") },
                        actions = {
                            IconButton(onClick = onRefresh) {
                                Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                            }
                            IconButton(onClick = onNavigateToSettings) {
                                Icon(Icons.Default.Settings, contentDescription = "Settings")
                            }
                        }
                    )
                },
                floatingActionButton = {
                    AnimatedVisibility(
                        visible = !showAddForm,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        ExtendedFloatingActionButton(
                            onClick = { showAddForm = true },
                            icon = { Icon(Icons.Default.Add, contentDescription = null) },
                            text = { Text("Add") },
                            modifier = Modifier.sharedBounds(
                                sharedContentState = rememberSharedContentState("fab_to_form"),
                                animatedVisibilityScope = this@AnimatedVisibility
                            )
                        )
                    }
                }
            ) { innerPadding ->
                if (items.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No devices added yet",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.padding(innerPadding),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        itemsIndexed(items) { index, item ->
                            ListItem(
                                headlineContent = { Text(item.label) },
                                supportingContent = { Text(item.supportingText) },
                                leadingContent = {
                                    Icon(
                                        imageVector = item.iconType.icon,
                                        contentDescription = null
                                    )
                                },
                                colors = ListItemDefaults.colors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                                ),
                                modifier = Modifier.clip(segmentedShape(index, items.lastIndex))
                            )
                        }
                    }
                }
            }

            // Scrim
            AnimatedVisibility(
                visible = showAddForm,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.32f))
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { showAddForm = false }
                )
            }

            // Form with container transform from FAB
            AnimatedVisibility(
                visible = showAddForm,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AddItemForm(
                        modifier = Modifier.sharedBounds(
                            sharedContentState = rememberSharedContentState("fab_to_form"),
                            animatedVisibilityScope = this@AnimatedVisibility
                        ),
                        onDismiss = { showAddForm = false },
                        onConfirm = { iconType, label, supportingText ->
                            onAddItem(
                                MonitorItem(
                                    id = items.size,
                                    iconType = iconType,
                                    label = label,
                                    supportingText = supportingText
                                )
                            )
                            showAddForm = false
                        }
                    )
                }
            }
        }
    }
}
