package `in`.rsgametech.systemmonitor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.PredictiveBackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.SeekableTransitionState
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import `in`.rsgametech.systemmonitor.data.AppearanceStorage
import `in`.rsgametech.systemmonitor.data.MonitorItemStorage
import `in`.rsgametech.systemmonitor.model.AppearanceSettings
import `in`.rsgametech.systemmonitor.model.MonitorItem
import `in`.rsgametech.systemmonitor.ui.screens.AboutScreen
import `in`.rsgametech.systemmonitor.ui.screens.AppearanceScreen
import `in`.rsgametech.systemmonitor.ui.screens.MainScreen
import `in`.rsgametech.systemmonitor.ui.screens.ServerStatsScreen
import `in`.rsgametech.systemmonitor.ui.screens.SettingsScreen
import `in`.rsgametech.systemmonitor.ui.theme.RemoteSystemMonitorTheme
import kotlin.coroutines.cancellation.CancellationException

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        window.isNavigationBarContrastEnforced = false
        setContent {
            RemoteSystemMonitorApp()
        }
    }
}

private enum class Screen { Main, Settings, Appearance, ServerStats, About }

@Composable
private fun RemoteSystemMonitorApp() {
    val context = LocalContext.current
    val monitorStorage = remember { MonitorItemStorage(context) }
    val appearanceStorage = remember { AppearanceStorage(context) }
    var currentScreen by remember { mutableStateOf(Screen.Main) }
    val items = remember { mutableStateListOf<MonitorItem>().apply { addAll(monitorStorage.load()) } }
    var appearance by remember { mutableStateOf(appearanceStorage.load()) }
    var selectedServer by remember { mutableStateOf<MonitorItem?>(null) }

    val transitionState = remember { SeekableTransitionState(Screen.Main) }
    val transition = rememberTransition(transitionState, label = "screen_transition")

    LaunchedEffect(currentScreen) {
        if (transitionState.currentState != currentScreen) {
            transitionState.animateTo(currentScreen)
        }
    }

    PredictiveBackHandler(enabled = currentScreen != Screen.Main) { progress ->
        val previousScreen = when (currentScreen) {
            Screen.Appearance -> Screen.Settings
            Screen.Settings -> Screen.Main
            Screen.ServerStats -> Screen.Main
            Screen.About -> Screen.Settings
            Screen.Main -> Screen.Main
        }
        try {
            progress.collect { backEvent ->
                transitionState.seekTo(fraction = backEvent.progress, targetState = previousScreen)
            }
            transitionState.animateTo(previousScreen)
            currentScreen = previousScreen
        } catch (e: CancellationException) {
            transitionState.snapTo(currentScreen)
        }
    }

    RemoteSystemMonitorTheme(settings = appearance) {
        Surface(modifier = Modifier.fillMaxSize()) {
            transition.AnimatedContent(
                transitionSpec = {
                    if (targetState.ordinal > initialState.ordinal) {
                        slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
                    } else {
                        slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
                    }
                },
            ) { screen ->
                when (screen) {
                    Screen.Main -> MainScreen(
                        items = items,
                        onAddItem = { item ->
                            items.add(item)
                            monitorStorage.save(items)
                        },
                        onEditItem = { edited ->
                            val idx = items.indexOfFirst { it.id == edited.id }
                            if (idx >= 0) {
                                items[idx] = edited
                                monitorStorage.save(items)
                            }
                        },
                        onDeleteItem = { item ->
                            items.removeAll { it.id == item.id }
                            monitorStorage.save(items)
                        },
                        onNavigateToSettings = { currentScreen = Screen.Settings },
                        onNavigateToStats = { server ->
                            selectedServer = server
                            currentScreen = Screen.ServerStats
                        }
                    )
                    Screen.Settings -> SettingsScreen(
                        onNavigateBack = { currentScreen = Screen.Main },
                        onNavigateToAppearance = { currentScreen = Screen.Appearance },
                        onNavigateToAbout = { currentScreen = Screen.About },
                        settings = appearance,
                        onSettingsChange = { newSettings ->
                            appearance = newSettings
                            appearanceStorage.save(newSettings)
                        }
                    )
                    Screen.Appearance -> AppearanceScreen(
                        settings = appearance,
                        onSettingsChange = { newSettings ->
                            appearance = newSettings
                            appearanceStorage.save(newSettings)
                        },
                        onNavigateBack = { currentScreen = Screen.Settings }
                    )
                    Screen.ServerStats -> {
                        selectedServer?.let { server ->
                            ServerStatsScreen(
                                server = server,
                                onBack = { currentScreen = Screen.Main }
                            )
                        }
                    }
                    Screen.About -> AboutScreen(
                        onNavigateBack = { currentScreen = Screen.Settings }
                    )
                }
            }
        }
    }
}
