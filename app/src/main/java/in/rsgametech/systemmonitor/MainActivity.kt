package `in`.rsgametech.systemmonitor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import `in`.rsgametech.systemmonitor.data.MonitorItemStorage
import `in`.rsgametech.systemmonitor.model.MonitorItem
import `in`.rsgametech.systemmonitor.ui.screens.MainScreen
import `in`.rsgametech.systemmonitor.ui.screens.SettingsScreen
import `in`.rsgametech.systemmonitor.ui.theme.RemoteSystemMonitorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RemoteSystemMonitorTheme {
                RemoteSystemMonitorApp()
            }
        }
    }
}

private enum class Screen { Main, Settings }

@Composable
private fun RemoteSystemMonitorApp() {
    val context = LocalContext.current
    val storage = remember { MonitorItemStorage(context) }
    var currentScreen by remember { mutableStateOf(Screen.Main) }
    val items = remember { mutableStateListOf<MonitorItem>().apply { addAll(storage.load()) } }

    AnimatedContent(
        targetState = currentScreen,
        transitionSpec = {
            if (targetState.ordinal > initialState.ordinal) {
                slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
            } else {
                slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
            }
        },
        label = "screen_transition"
    ) { screen ->
        when (screen) {
            Screen.Main -> MainScreen(
                items = items,
                onAddItem = { item ->
                    items.add(item)
                    storage.save(items)
                },
                onRefresh = { },
                onNavigateToSettings = { currentScreen = Screen.Settings }
            )
            Screen.Settings -> SettingsScreen(
                onNavigateBack = { currentScreen = Screen.Main }
            )
        }
    }
}
