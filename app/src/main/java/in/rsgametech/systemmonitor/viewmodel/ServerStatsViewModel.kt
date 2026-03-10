package `in`.rsgametech.systemmonitor.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.rsgametech.systemmonitor.data.model.MetricsResponse
import `in`.rsgametech.systemmonitor.data.remote.ClientWsMessage
import `in`.rsgametech.systemmonitor.data.remote.MonitorWebSocketClient
import `in`.rsgametech.systemmonitor.data.remote.ServerWsMessage
import `in`.rsgametech.systemmonitor.data.remote.WsConnectionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
    val isRefreshing: Boolean = false,
    val pollIntervalMs: Long = 2000L
)

class ServerStatsViewModel : ViewModel() {

    private val _state = MutableStateFlow(ServerStatsState())
    val state = _state.asStateFlow()

    private val wsClient = MonitorWebSocketClient()
    private var serverUrl: String = ""
    private var apiKey: String = ""

    fun startMonitoring(serverUrl: String, apiKey: String) {
        this.serverUrl = serverUrl
        this.apiKey = apiKey

        _state.update {
            it.copy(
                connectionState = ConnectionState.Connecting,
                isPolling = true,
                metrics = null
            )
        }

        wsClient.connect(serverUrl, apiKey)

        viewModelScope.launch {
            wsClient.connectionState.collect { wsState ->
                _state.update {
                    it.copy(
                        connectionState = when (wsState) {
                            is WsConnectionState.Connecting -> ConnectionState.Connecting
                            is WsConnectionState.Connected -> ConnectionState.Connected
                            is WsConnectionState.Disconnected -> ConnectionState.Error(wsState.reason ?: "Disconnected")
                            is WsConnectionState.Failed -> ConnectionState.Error(wsState.error)
                        }
                    )
                }
            }
        }

        viewModelScope.launch {
            wsClient.messages.collect { message ->
                when (message) {
                    is ServerWsMessage.Metrics -> {
                        _state.update {
                            it.copy(
                                connectionState = ConnectionState.Connected,
                                metrics = message.data,
                                lastUpdated = message.data.timestamp
                            )
                        }
                    }
                    is ServerWsMessage.Error -> {
                        _state.update {
                            it.copy(connectionState = ConnectionState.Error(message.message))
                        }
                    }
                    else -> { /* pong, kill_result handled elsewhere */ }
                }
            }
        }
    }

    fun setPollInterval(intervalMs: Long) {
        _state.update { it.copy(pollIntervalMs = intervalMs) }
        wsClient.send(ClientWsMessage.SetInterval(intervalMs))
    }

    fun refresh() {
        // WS pushes metrics automatically; reconnect if disconnected
        val currentState = _state.value.connectionState
        if (currentState is ConnectionState.Error || currentState is ConnectionState.Disconnected) {
            startMonitoring(serverUrl, apiKey)
        }
    }

    fun stopMonitoring() {
        wsClient.disconnect()
        _state.update { it.copy(isPolling = false) }
    }

    override fun onCleared() {
        stopMonitoring()
        super.onCleared()
    }
}
