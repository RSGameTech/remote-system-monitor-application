package `in`.rsgametech.systemmonitor.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.rsgametech.systemmonitor.data.model.MetricsResponse
import `in`.rsgametech.systemmonitor.data.remote.ApiClientFactory
import `in`.rsgametech.systemmonitor.data.repository.AuthException
import `in`.rsgametech.systemmonitor.data.repository.MonitorRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

sealed class ConnectionState {
    data object Disconnected : ConnectionState()
    data object Connecting : ConnectionState()
    data object Connected : ConnectionState()
    data class Error(val message: String) : ConnectionState()
}

data class ServerStatsState(
    val connectionState: ConnectionState = ConnectionState.Connecting,
    val metrics: MetricsResponse? = null,
    val lastUpdated: String = "",
    val isPolling: Boolean = false,
    val isRefreshing: Boolean = false
)

class ServerStatsViewModel : ViewModel() {

    private val _state = MutableStateFlow(ServerStatsState())
    val state = _state.asStateFlow()

    private var pollingJob: Job? = null
    private var repository: MonitorRepository? = null

    companion object {
        const val POLL_INTERVAL_MS = 2000L
    }

    fun startMonitoring(serverUrl: String, apiKey: String) {
        pollingJob?.cancel()

        val url = if (serverUrl.startsWith("http://") || serverUrl.startsWith("https://")) {
            serverUrl
        } else {
            "http://$serverUrl"
        }

        val api = ApiClientFactory.create(url, apiKey)
        repository = MonitorRepository(api)

        _state.update { it.copy(connectionState = ConnectionState.Connecting, isPolling = true) }

        pollingJob = viewModelScope.launch {
            while (isActive) {
                fetchAndUpdate()
                delay(POLL_INTERVAL_MS)
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true) }
            fetchAndUpdate()
            _state.update { it.copy(isRefreshing = false) }
        }
    }

    private suspend fun fetchAndUpdate() {
        repository?.fetchMetrics()
            ?.onSuccess { metrics ->
                _state.update {
                    it.copy(
                        connectionState = ConnectionState.Connected,
                        metrics = metrics,
                        lastUpdated = metrics.timestamp
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
                _state.update {
                    it.copy(connectionState = ConnectionState.Error(msg))
                }
            }
    }

    fun stopMonitoring() {
        pollingJob?.cancel()
        pollingJob = null
        _state.update { it.copy(isPolling = false) }
    }

    override fun onCleared() {
        stopMonitoring()
        super.onCleared()
    }
}
