package `in`.rsgametech.systemmonitor.data.remote

import com.google.gson.Gson
import com.google.gson.JsonParser
import `in`.rsgametech.systemmonitor.data.model.MetricsResponse
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit

sealed class ServerWsMessage {
    data class Metrics(val data: MetricsResponse) : ServerWsMessage()
    data class KillResult(val pid: Long, val success: Boolean, val error: String?) : ServerWsMessage()
    data object Pong : ServerWsMessage()
    data class Error(val message: String) : ServerWsMessage()
}

sealed class ClientWsMessage {
    data class SetInterval(val ms: Long) : ClientWsMessage()
    data class KillProcess(val pid: Long) : ClientWsMessage()
    data object Ping : ClientWsMessage()
}

sealed class WsConnectionState {
    data object Connecting : WsConnectionState()
    data object Connected : WsConnectionState()
    data class Disconnected(val reason: String?) : WsConnectionState()
    data class Failed(val error: String) : WsConnectionState()
}

class MonitorWebSocketClient {

    private val gson = Gson()
    private var webSocket: WebSocket? = null
    private var client: OkHttpClient? = null

    private val _messages = MutableSharedFlow<ServerWsMessage>(
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val messages: SharedFlow<ServerWsMessage> = _messages

    private val _connectionState = MutableSharedFlow<WsConnectionState>(
        replay = 1,
        extraBufferCapacity = 8,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val connectionState: SharedFlow<WsConnectionState> = _connectionState

    fun connect(serverUrl: String, apiKey: String) {
        disconnect()

        val baseUrl = if (serverUrl.startsWith("http://") || serverUrl.startsWith("https://")) {
            serverUrl
        } else {
            "http://$serverUrl"
        }

        val wsUrl = baseUrl
            .replace("http://", "ws://")
            .replace("https://", "wss://")
            .trimEnd('/') + "/ws?key=$apiKey"

        client = OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(0, TimeUnit.MILLISECONDS) // no read timeout for WS
            .pingInterval(30, TimeUnit.SECONDS)
            .build()

        val request = Request.Builder().url(wsUrl).build()
        _connectionState.tryEmit(WsConnectionState.Connecting)
        webSocket = client!!.newWebSocket(request, WsListener())
    }

    fun send(message: ClientWsMessage) {
        val json = when (message) {
            is ClientWsMessage.SetInterval -> """{"action":"set_interval","ms":${message.ms}}"""
            is ClientWsMessage.KillProcess -> """{"action":"kill_process","pid":${message.pid}}"""
            is ClientWsMessage.Ping -> """{"action":"ping"}"""
        }
        webSocket?.send(json)
    }

    fun disconnect() {
        webSocket?.close(1000, "Client closing")
        webSocket = null
        client?.dispatcher?.executorService?.shutdown()
        client = null
    }

    private inner class WsListener : WebSocketListener() {

        override fun onOpen(webSocket: WebSocket, response: Response) {
            _connectionState.tryEmit(WsConnectionState.Connected)
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            try {
                val json = JsonParser.parseString(text).asJsonObject
                val event = json.get("event")?.asString ?: return

                when (event) {
                    "metrics" -> {
                        val data = gson.fromJson(json.get("data"), MetricsResponse::class.java)
                        _messages.tryEmit(ServerWsMessage.Metrics(data))
                    }
                    "kill_result" -> {
                        _messages.tryEmit(
                            ServerWsMessage.KillResult(
                                pid = json.get("pid").asLong,
                                success = json.get("success").asBoolean,
                                error = json.get("error")?.takeIf { !it.isJsonNull }?.asString
                            )
                        )
                    }
                    "pong" -> {
                        _messages.tryEmit(ServerWsMessage.Pong)
                    }
                    "error" -> {
                        _messages.tryEmit(
                            ServerWsMessage.Error(json.get("message")?.asString ?: "Unknown error")
                        )
                    }
                }
            } catch (e: Exception) {
                _messages.tryEmit(ServerWsMessage.Error("Parse error: ${e.message}"))
            }
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            webSocket.close(1000, null)
            _connectionState.tryEmit(WsConnectionState.Disconnected(reason))
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            _connectionState.tryEmit(WsConnectionState.Disconnected(reason))
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            val msg = when {
                t is java.net.ConnectException -> "Cannot reach server"
                t is java.net.SocketTimeoutException -> "Connection timed out"
                response?.code == 401 -> "Invalid API key"
                else -> t.message ?: "Connection failed"
            }
            _connectionState.tryEmit(WsConnectionState.Failed(msg))
        }
    }
}
