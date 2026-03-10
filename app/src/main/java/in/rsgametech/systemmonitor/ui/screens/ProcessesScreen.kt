package `in`.rsgametech.systemmonitor.ui.screens

import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import `in`.rsgametech.systemmonitor.data.model.ProcessInfo
import `in`.rsgametech.systemmonitor.ui.components.ConnectionStatusDot
import `in`.rsgametech.systemmonitor.viewmodel.ConnectionState
import `in`.rsgametech.systemmonitor.viewmodel.ProcessSortMode
import `in`.rsgametech.systemmonitor.viewmodel.ProcessesViewModel

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

private fun statusColor(status: String): Color = when (status.lowercase()) {
    "running" -> Color(0xFF4CAF50)     // Green
    "sleeping", "idle" -> Color(0xFF2196F3) // Blue
    "stopped" -> Color(0xFFF44336)     // Red
    "zombie" -> Color(0xFF9C27B0)      // Purple
    "waiting", "parked" -> Color(0xFFFFC107) // Amber
    else -> Color(0xFF9E9E9E)          // Gray
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProcessesScreen(
    serverUrl: String,
    apiKey: String,
    onBack: () -> Unit,
    viewModel: ProcessesViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var killConfirmPid by remember { mutableStateOf<ProcessInfo?>(null) }

    LaunchedEffect(serverUrl, apiKey) {
        viewModel.startMonitoring(serverUrl, apiKey)
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.stopMonitoring() }
    }

    PredictiveBackHandler(enabled = true) { progress ->
        try {
            progress.collect { }
            onBack()
        } catch (_: kotlin.coroutines.cancellation.CancellationException) { }
    }

    LaunchedEffect(state.killResult) {
        state.killResult?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearKillResult()
        }
    }

    val processes = viewModel.filteredAndSortedProcesses()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                title = {
                    BasicTextField(
                        value = state.searchQuery,
                        onValueChange = viewModel::setSearchQuery,
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(28.dp))
                                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                if (state.searchQuery.isEmpty()) {
                                    Text(
                                        "Processes",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                },
                actions = {
                    if (state.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                    ConnectionStatusDot(state.connectionState)
                }
            )
        }
    ) { padding ->
        when {
            state.processes.isEmpty() && state.connectionState is ConnectionState.Error -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Filled.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            (state.connectionState as ConnectionState.Error).message,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            state.processes.isEmpty() && state.connectionState is ConnectionState.Connecting -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Loading processes…", style = MaterialTheme.typography.bodyLarge)
                }
            }

            else -> {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    // Sort chips
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val sortModes = ProcessSortMode.entries
                        items(sortModes.size) { index ->
                            val mode = sortModes[index]
                            val isSelected = state.sortMode == mode
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setSortMode(mode) },
                                label = { Text(mode.label) },
                                trailingIcon = if (isSelected) {
                                    {
                                        Icon(
                                            if (state.sortDescending) Icons.Default.KeyboardArrowDown
                                            else Icons.Default.KeyboardArrowUp,
                                            contentDescription = if (state.sortDescending) "Descending" else "Ascending",
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                } else null
                            )
                        }
                    }

                    // Process count
                    Text(
                        "${processes.size} process${if (processes.size != 1) "es" else ""}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(Modifier.height(4.dp))

                    // Process list
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        itemsIndexed(processes, key = { _, p -> p.pid }) { index, process ->
                            ProcessListItem(
                                process = process,
                                index = index,
                                lastIndex = processes.lastIndex,
                                onKillClick = { killConfirmPid = process }
                            )
                        }

                        item { Spacer(Modifier.height(16.dp)) }
                    }
                }
            }
        }
    }

    // Kill confirmation dialog
    killConfirmPid?.let { process ->
        AlertDialog(
            onDismissRequest = { killConfirmPid = null },
            title = { Text("Stop Process") },
            text = {
                Text("Stop \"${process.name}\" (PID: ${process.pid})?")
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.killProcess(process.pid)
                    killConfirmPid = null
                }) {
                    Text("Stop", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { killConfirmPid = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun ProcessListItem(
    process: ProcessInfo,
    index: Int,
    lastIndex: Int,
    onKillClick: () -> Unit
) {
    ListItem(
        headlineContent = {
            Text(
                process.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        supportingContent = {
            Column {
                Text("PID: ${process.pid}")
                Text("CPU: ${"%.1f".format(process.cpuPercent)}%  ·  MEM: ${"%.1f".format(process.memoryPercent)}%")
            }
        },
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(statusColor(process.status))
            )
        },
        trailingContent = {
            IconButton(onClick = onKillClick) {
                Icon(
                    Icons.Outlined.Stop,
                    contentDescription = "Stop process",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        },
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        modifier = Modifier.clip(segmentedShape(index, lastIndex))
    )
}
