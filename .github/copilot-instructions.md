# Copilot Instructions

## Build & Test Commands

```bash
# Build
./gradlew assembleDebug
./gradlew assembleRelease

# Install on connected device/emulator
./gradlew installDebug

# Run all unit tests
./gradlew test

# Run a single test class
./gradlew test --tests "in.rsgametech.systemmonitor.ExampleUnitTest"

# Run instrumented tests (requires connected device)
./gradlew connectedAndroidTest

# Clean
./gradlew clean
```

## Tech Stack

- **Language:** Kotlin 2.2.10, Java 11 compatibility
- **UI:** Jetpack Compose + Material 3 (BOM 2025.12.00)
- **Networking:** Retrofit 2.11.0 + GSON, OkHttp 4.12.0
- **Build:** Gradle 9.3.1 (Kotlin DSL), AGP 9.1.0
- **Min SDK:** 29 / **Target SDK:** 36
- **Dependencies:** Managed via `gradle/libs.versions.toml` version catalog

## Architecture

Single-module MVVM app. All source under `app/src/main/java/in/rsgametech/systemmonitor/`:

```
model/          — Domain models (MonitorItem, AppearanceSettings) and their enums
data/
  model/        — API response DTOs (MetricsResponse, CpuInfo, MemoryInfo, etc.)
  remote/       — Retrofit interface (MonitorApi) and ApiClientFactory
  repository/   — MonitorRepository wraps API calls in Result<T>
  *Storage.kt   — SharedPreferences persistence (MonitorItemStorage, AppearanceStorage)
viewmodel/      — ServerStatsViewModel: polling loop, ConnectionState sealed class
ui/
  theme/        — RemoteSystemMonitorTheme, Google Fonts integration, AMOLED support
  screens/      — Full-screen composables (MainScreen, ServerStatsScreen, etc.)
  components/   — Reusable composables (cards, dialogs, status indicators, shimmer)
```

**Navigation** is manual state-based in `MainActivity.kt` — there is no Jetpack Navigation component. The activity holds a `currentScreen` state variable and renders screens directly with animated transitions (slide + fade).

**Polling** runs in `ServerStatsViewModel` as a coroutine loop every 2000ms. The `ConnectionState` sealed class (`Disconnected`, `Connecting`, `Connected`, `Error`) drives all UI connection feedback.

**Each monitored server** gets its own `ApiClientFactory`-created Retrofit instance with an `X-API-Key` auth header. Servers are persisted as JSON in SharedPreferences via `MonitorItemStorage`.

## Key Conventions

### Package requires backtick imports
The package `in.rsgametech.systemmonitor` starts with the Kotlin keyword `in`. Imports use backticks:
```kotlin
import `in`.rsgametech.systemmonitor.model.MonitorItem
```

### Theme wrapper is mandatory
All composable content must be wrapped in `RemoteSystemMonitorTheme { ... }`. The theme reads `AppearanceSettings` to apply dynamic color (Android 12+), AMOLED mode (pure black surfaces), and the selected Google Font.

### API DTOs use `@SerializedName`
All fields in `data/model/MetricsResponse.kt` use `@SerializedName` for JSON mapping — never rely on property names matching JSON keys.

### Repository returns `Result<T>`
`MonitorRepository.fetchMetrics()` returns `kotlin.Result<MetricsResponse>`. HTTP 401 maps to a custom `AuthException`; all other failures are wrapped as generic exceptions.

### Cleartext HTTP is allowed
`android:usesCleartextTraffic="true"` is set in the manifest and `network_security_config.xml` permits plain HTTP — the app connects to local dev servers over HTTP.

### Component utilities
`ui/components/Utils.kt` provides shared helpers used across cards:
- `usageColor(percent)` — returns `MaterialTheme.colorScheme.error/tertiary/primary` thresholds
- `formatSpeed(bytesPerSec)` — human-readable MB/s string
- `formatNumber(n)` — localized thousands separator

### Shimmer skeleton loading
`ShimmerEffect.kt` exposes a `Modifier.shimmer()` extension. Use `ServerStatsSkeletonScreen` during initial data load before the first `MetricsResponse` arrives.
