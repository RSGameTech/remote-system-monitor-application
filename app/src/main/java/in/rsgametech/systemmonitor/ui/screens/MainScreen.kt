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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
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
    onEditItem: (MonitorItem) -> Unit,
    onDeleteItem: (MonitorItem) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToStats: (MonitorItem) -> Unit
) {
    var showAddForm by remember { mutableStateOf(false) }
    var menuOpenForId by remember { mutableStateOf<Int?>(null) }
    var deleteDialogItem by remember { mutableStateOf<MonitorItem?>(null) }
    var editItem by remember { mutableStateOf<MonitorItem?>(null) }

    val showOverlay = showAddForm || editItem != null
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    BackHandler(enabled = showOverlay) {
        showAddForm = false
        editItem = null
    }

    // Delete confirmation dialog
    deleteDialogItem?.let { item ->
        AlertDialog(
            onDismissRequest = { deleteDialogItem = null },
            icon = { Icon(Icons.Filled.Delete, contentDescription = null) },
            title = { Text("Delete Server") },
            text = { Text("Are you sure you want to delete \"${item.label}\"? This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteItem(item)
                    deleteDialogItem = null
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteDialogItem = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    SharedTransitionLayout {
        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    MediumTopAppBar(
                        title = { Text("Remote System Monitor") },
                        actions = {
                            IconButton(onClick = onNavigateToSettings) {
                                Icon(Icons.Default.Settings, contentDescription = "Settings")
                            }
                        },
                        scrollBehavior = scrollBehavior,
                        colors = TopAppBarDefaults.topAppBarColors()
                    )
                },
                floatingActionButton = {
                    AnimatedVisibility(
                        visible = !showOverlay,
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
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            top = innerPadding.calculateTopPadding(),
                            bottom = innerPadding.calculateBottomPadding()
                        ),
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
                                trailingContent = {
                                    Box {
                                        IconButton(onClick = { menuOpenForId = item.id }) {
                                            Icon(Icons.Default.MoreVert, contentDescription = "Options")
                                        }
                                        DropdownMenu(
                                            expanded = menuOpenForId == item.id,
                                            onDismissRequest = { menuOpenForId = null }
                                        ) {
                                            DropdownMenuItem(
                                                text = { Text("Edit") },
                                                onClick = {
                                                    menuOpenForId = null
                                                    editItem = item
                                                },
                                                leadingIcon = {
                                                    Icon(Icons.Filled.Edit, contentDescription = null)
                                                }
                                            )
                                            DropdownMenuItem(
                                                text = { Text("Delete") },
                                                onClick = {
                                                    menuOpenForId = null
                                                    deleteDialogItem = item
                                                },
                                                leadingIcon = {
                                                    Icon(Icons.Filled.Delete, contentDescription = null)
                                                }
                                            )
                                        }
                                    }
                                },
                                colors = ListItemDefaults.colors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                                ),
                                modifier = Modifier
                                    .clip(segmentedShape(index, items.lastIndex))
                                    .clickable { onNavigateToStats(item) }
                            )
                        }
                    }
                }
            }

            // Scrim
            AnimatedVisibility(
                visible = showOverlay,
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
                        ) {
                            showAddForm = false
                            editItem = null
                        }
                )
            }

            // Add form with container transform from FAB
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
                        onConfirm = { iconType, label, address, apiKey ->
                            onAddItem(
                                MonitorItem(
                                    id = items.size,
                                    iconType = iconType,
                                    label = label,
                                    supportingText = address,
                                    apiKey = apiKey
                                )
                            )
                            showAddForm = false
                        }
                    )
                }
            }

            // Edit form
            editItem?.let { item ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AddItemForm(
                        title = "Edit Device",
                        confirmText = "Save",
                        initialIconType = item.iconType,
                        initialLabel = item.label,
                        initialAddress = item.supportingText,
                        initialApiKey = item.apiKey,
                        onDismiss = { editItem = null },
                        onConfirm = { iconType, label, address, apiKey ->
                            onEditItem(
                                item.copy(
                                    iconType = iconType,
                                    label = label,
                                    supportingText = address,
                                    apiKey = apiKey
                                )
                            )
                            editItem = null
                        }
                    )
                }
            }
        }
    }
}
