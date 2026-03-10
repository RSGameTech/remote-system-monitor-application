package `in`.rsgametech.systemmonitor.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.rsgametech.systemmonitor.data.model.ProcessInfo
import `in`.rsgametech.systemmonitor.data.remote.ApiClientFactory
import `in`.rsgametech.systemmonitor.data.remote.ClientWsMessage
import `in`.rsgametech.systemmonitor.data.remote.MonitorWebSocketClient
import `in`.rsgametech.systemmonitor.data.remote.ServerWsMessage
import `in`.rsgametech.systemmonitor.data.remote.WsConnectionState
import `in`.rsgametech.systemmonitor.data.repository.AuthException
import `in`.rsgametech.systemmonitor.data.repository.MonitorRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class ProcessSortMode(val label: String) {
    NAME("Name"),
    PID("PID"),
    CPU("CPU%"),
    MEM("MEM%")
}

data class ProcessesState(
    val connectionState: ConnectionState = ConnectionState.Connecting,
    val processes: List<ProcessInfo> = emptyList(),
    val searchQuery: String = "",
    val sortMode: ProcessSortMode = ProcessSortMode.CPU,
    val sortDescending: Boolean = true,
    val killResult: String? = null
)

class ProcessesViewModel : ViewModel() {

    private val _state = MutableStateFlow(ProcessesState())
    val state = _state.asStateFlow()

    private var pollingJob: Job? = null
    private var wsListenerJob: Job? = null
    private var repository: MonitorRepository? = null
    private val wsClient = MonitorWebSocketClient()

    fun startMonitoring(serverUrl: String, apiKey: String) {
        pollingJob?.cancel()
        wsListenerJob?.cancel()

        val url = if (serverUrl.startsWith("http://") || serverUrl.startsWith("https://")) {
            serverUrl
        } else {
            "http://$serverUrl"
        }

        val api = ApiClientFactory.create(url, apiKey)
        repository = MonitorRepository(api)

        // Connect WebSocket for kill commands
        wsClient.connect(serverUrl, apiKey)

        // Listen for kill results from WebSocket
        wsListenerJob = viewModelScope.launch {
            wsClient.messages.collect { message ->
                when (message) {
                    is ServerWsMessage.KillResult -> {
                        val resultMsg = if (message.success) {
                            "Process ${message.pid} stopped"
                        } else {
                            "Failed: ${message.error ?: "unknown error"}"
                        }
                        _state.update { it.copy(killResult = resultMsg) }
                        if (message.success) fetchProcesses()
                    }
                    else -> { /* ignore metrics/pong/error from WS */ }
                }
            }
        }

        _state.update { it.copy(connectionState = ConnectionState.Connecting) }

        pollingJob = viewModelScope.launch {
            while (isActive) {
                fetchProcesses()
                delay(3000L)
            }
        }
    }

    private suspend fun fetchProcesses() {
        repository?.fetchProcesses()
            ?.onSuccess { response ->
                _state.update {
                    it.copy(
                        connectionState = ConnectionState.Connected,
                        processes = response.processes
                    )
                }
            }
            ?.onFailure { error ->
                val msg = when (error) {
                    is AuthException -> "Invalid API key"
                    is java.net.ConnectException -> "Cannot reach server"
                    is java.net.SocketTimeoutException -> "Connection timed out"
                    else -> error.message ?: "Unknown error"
                }
                _state.update { it.copy(connectionState = ConnectionState.Error(msg)) }
            }
    }

    fun setSearchQuery(query: String) {
        _state.update { it.copy(searchQuery = query) }
    }

    fun setSortMode(mode: ProcessSortMode) {
        _state.update {
            if (it.sortMode == mode) {
                it.copy(sortDescending = !it.sortDescending)
            } else {
                it.copy(sortMode = mode, sortDescending = true)
            }
        }
    }

    fun filteredAndSortedProcesses(): List<ProcessInfo> {
        val s = _state.value
        val filtered = if (s.searchQuery.isBlank()) {
            s.processes
        } else {
            s.processes.filter {
                it.name.contains(s.searchQuery, ignoreCase = true) ||
                        it.pid.toString().contains(s.searchQuery)
            }
        }
        val sorted = when (s.sortMode) {
            ProcessSortMode.NAME -> filtered.sortedBy { it.name.lowercase() }
            ProcessSortMode.PID -> filtered.sortedBy { it.pid }
            ProcessSortMode.CPU -> filtered.sortedBy { it.cpuPercent }
            ProcessSortMode.MEM -> filtered.sortedBy { it.memoryPercent }
        }
        return if (s.sortDescending) sorted.reversed() else sorted
    }

    fun killProcess(pid: Long) {
        wsClient.send(ClientWsMessage.KillProcess(pid))
    }

    fun clearKillResult() {
        _state.update { it.copy(killResult = null) }
    }

    fun stopMonitoring() {
        pollingJob?.cancel()
        pollingJob = null
        wsListenerJob?.cancel()
        wsListenerJob = null
        wsClient.disconnect()
    }

    override fun onCleared() {
        stopMonitoring()
        super.onCleared()
    }
}
