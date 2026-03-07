# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Remote System Monitor — an Android app built with Kotlin and Jetpack Compose. Package: `in.rsgametech.systemmonitor`. Currently in early/scaffold stage (single activity with default template code).

## Build & Run

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Install debug build on connected device/emulator
./gradlew installDebug

# Run unit tests
./gradlew test

# Run a single unit test class
./gradlew test --tests "in.rsgametech.systemmonitor.ExampleUnitTest"

# Run instrumented (on-device) tests
./gradlew connectedAndroidTest

# Clean build
./gradlew clean
```

## Tech Stack & Versions

- **Language:** Kotlin 2.2.10, Java 11 compatibility
- **UI:** Jetpack Compose with Material 3 (Compose BOM 2024.09.00)
- **Build:** Gradle 9.3.1 with Kotlin DSL, AGP 9.1.0
- **Min SDK:** 29 (Android 10) / **Target SDK:** 36
- **Version catalog:** `gradle/libs.versions.toml`

## Architecture

Single-module Gradle project (`:app`). All source lives under `app/src/main/java/in/rsgametech/systemmonitor/`.

- `MainActivity.kt` — Single entry point activity, uses `ComponentActivity` with edge-to-edge and Compose `setContent`
- `ui/theme/` — Material 3 theming (Color, Theme, Type). Supports dynamic color on Android 12+

## Key Conventions

- Compose UI — all UI is declarative Compose, no XML layouts
- The Kotlin package requires backtick imports due to the `in` keyword: `` `in`.rsgametech.systemmonitor ``
- Theme wrapper: `RemoteSystemMonitorTheme { ... }` must wrap all composable content
- ProGuard rules are in `app/proguard-rules.pro` (minification disabled for now)
