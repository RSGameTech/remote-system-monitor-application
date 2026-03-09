package `in`.rsgametech.systemmonitor.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import `in`.rsgametech.systemmonitor.data.model.MetricsResponse
import `in`.rsgametech.systemmonitor.model.MonitorItem
import `in`.rsgametech.systemmonitor.ui.components.ConnectionErrorBanner
import `in`.rsgametech.systemmonitor.ui.components.ConnectionStatusDot
import `in`.rsgametech.systemmonitor.ui.components.CpuOverviewCard
import `in`.rsgametech.systemmonitor.ui.components.DiskCard
import `in`.rsgametech.systemmonitor.ui.components.GpuOverviewCard
import `in`.rsgametech.systemmonitor.ui.components.MemoryOverviewCard
import `in`.rsgametech.systemmonitor.ui.components.NetworkOverviewCard
import `in`.rsgametech.systemmonitor.ui.components.ServerStatsSkeletonScreen
import `in`.rsgametech.systemmonitor.ui.components.SystemInfoCard
import `in`.rsgametech.systemmonitor.viewmodel.ConnectionState
import `in`.rsgametech.systemmonitor.viewmodel.ServerStatsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerStatsScreen(
    server: MonitorItem,
    onBack: () -> Unit,
    viewModel: ServerStatsViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    LaunchedEffect(server.supportingText, server.apiKey) {
        viewModel.startMonitoring(server.supportingText, server.apiKey)
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.stopMonitoring() }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> viewModel.stopMonitoring()
                Lifecycle.Event.ON_START -> viewModel.startMonitoring(server.supportingText, server.apiKey)
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                title = {
                    Column {
                        Text(server.label)
                        Text(
                            server.supportingText,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    ConnectionStatusDot(state.connectionState)
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors()
            )
        }
    ) { padding ->
        val isLoading = state.metrics == null && state.connectionState is ConnectionState.Connecting

        when {
            state.metrics == null && state.connectionState is ConnectionState.Error -> {
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

            else -> {
                AnimatedContent(
                    targetState = isLoading,
                    transitionSpec = {
                        fadeIn(tween(300)) togetherWith fadeOut(tween(200))
                    },
                    label = "statsContent"
                ) { loading ->
                    if (loading) {
                        ServerStatsSkeletonScreen(
                            modifier = Modifier.padding(padding)
                        )
                    } else {
                        state.metrics?.let { metrics ->
                            StatsContent(
                                metrics = metrics,
                                connectionState = state.connectionState,
                                modifier = Modifier.padding(padding)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatsContent(
    metrics: MetricsResponse,
    connectionState: ConnectionState,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (connectionState is ConnectionState.Error) {
            item { ConnectionErrorBanner(connectionState.message) }
        }

        item { SystemInfoCard(metrics.system) }

        item { CpuOverviewCard(metrics.cpu) }

        item { MemoryOverviewCard(metrics.memory) }

        if (metrics.gpu.isNotEmpty()) {
            item {
                Text("GPU", style = MaterialTheme.typography.titleMedium)
            }
            items(metrics.gpu, key = { it.index }) { gpu ->
                GpuOverviewCard(gpu)
            }
        }

        item {
            Text("Disks", style = MaterialTheme.typography.titleMedium)
        }
        items(metrics.disk, key = { it.mountpoint }) { disk ->
            DiskCard(disk)
        }

        item { NetworkOverviewCard(metrics.network) }

        item {
            Text(
                "Last updated: ${metrics.timestamp}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}
