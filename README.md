# 🏎️ Formula 1 Kotlin Multiplatform (KMP) Library & Telemetry App

[![Kotlin Multiplatform](https://img.shields.io/badge/Kotlin-Multiplatform-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org/docs/multiplatform.html)
[![Compose Multiplatform](https://img.shields.io/badge/Compose-Multiplatform-4285F4?logo=jetpackcompose&logoColor=white)](https://www.jetbrains.com/lp/compose-multiplatform/)
[![Build & Test](https://github.com/slashgil/formula1/actions/workflows/ci.yml/badge.svg)](https://github.com/slashgil/formula1/actions)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

A modern, cross-platform **Formula 1 Telemetry Library & Client App** built with **Kotlin Multiplatform (KMP)**. It encapsulates all Ergast Formula 1 REST API endpoints with local **SQLDelight** caching, dynamic **Wikipedia REST API** media fetching, and a high-performance **Compose Multiplatform** UI adhering to F1 Telemetry dark design aesthetics.

---

## 🌟 Key Features

- **🏎️ Complete Ergast F1 REST API Encapsulation**:
  - **Drivers & Standings**: Driver details, championship points, season wins, nationality, DOB, and number/code.
  - **Constructors / Teams**: Championship standings, team details, and season driver lineups.
  - **Race Calendar & Results**: Season schedule, Grand Prix rounds, finish positions (P1–P20), points scored, and status.
  - **Pit Stops Telemetry**: Pit stop lap numbers, stop counts, and duration telemetry.
- **🖼️ Dynamic Media Fetching via Wikipedia REST API**:
  - Automatically extracts Wikipedia article slugs from Ergast data and queries `https://en.wikipedia.org/api/rest_v1/page/summary/{title}`.
  - Loads high-resolution driver headshot photos, team logos, and circuit images asynchronously using **Coil 3**.
- **📅 Interactive Season / Year Selector**:
  - Dynamically switch between seasons (`2026`, `2025`, `2024`, `2023`, `2022`, `2021`, `2020`) with instant telemetry reload.
- **⚡ Concurrent Asynchronous Progressive Rendering**:
  - Fetches Standings, Drivers, and Race Schedules in parallel using Kotlin Coroutine child jobs on `Dispatchers.IO`.
  - Displays data progressively as each network call completes (~150ms) without blocking the UI.
- **💾 Offline Persistence & Caching**:
  - **SQLDelight 2** caching for Drivers, Races, and Standings with fallback logic when offline.
- **📱 Multi-Screen Compose Navigation**:
  - Full navigation host connecting [`HomeScreen`](./shared/src/commonMain/kotlin/com/slashgil/formula1/ui/screens/HomeScreen.kt), [`DriverDetailScreen`](./shared/src/commonMain/kotlin/com/slashgil/formula1/ui/screens/DriverDetailScreen.kt), [`ConstructorDetailScreen`](./shared/src/commonMain/kotlin/com/slashgil/formula1/ui/screens/ConstructorDetailScreen.kt), and [`RaceDetailScreen`](./shared/src/commonMain/kotlin/com/slashgil/formula1/ui/screens/RaceDetailScreen.kt).
- **🎨 Environment Product Flavors**:
  - **`dev` Flavor** (`com.slashgil.formula1.dev`): Technical Dark Blueprint Wireframe F1 Car branding & icon.
  - **`prod` Flavor** (`com.slashgil.formula1`): Official Red F1 Car on Dark Asphalt branding & icon.

---

## 🏗️ Clean Architecture Overview

The project is structured according to **Clean Architecture** principles across shared KMP layers:

```
                  ┌─────────────────────────────────────┐
                  │       Compose Multiplatform UI      │
                  │  (HomeScreen, DetailScreens, Nav)   │
                  └──────────────────┬──────────────────┘
                                     │
                                     ▼
                  ┌─────────────────────────────────────┐
                  │          ViewModel & State          │
                  │     (F1ViewModel & StateFlow)       │
                  └──────────────────┬──────────────────┘
                                     │
                                     ▼
                  ┌─────────────────────────────────────┐
                  │            Domain Layer             │
                  │   (Use Cases, Models, Repository)   │
                  └──────────────────┬──────────────────┘
                                     │
                                     ▼
                  ┌─────────────────────────────────────┐
                  │             Data Layer              │
                  │   (Ktor Client, SQLDelight Cache)   │
                  └─────────────────────────────────────┘
```

### Module & Source Structure

- **[`:shared`](./shared)**: Common KMP module targeting Android, iOS (`iosArm64`, `iosSimulatorArm64`), and JVM.
  - `domain`: Core entity models (`Driver`, `Constructor`, `Race`, `PitStop`, `LapTiming`) and Use Cases (`GetDriversUseCase`, `GetDriverStandingsUseCase`, `GetPitStopsUseCase`, `GetWikipediaSummaryUseCase`).
  - `data`: Ktor `ErgastApiService`, DTO mappings, SQLDelight `F1LocalDataSource`, and `F1RepositoryImpl`.
  - `di`: **Koin 4** modules (`networkModule`, `dataModule`, `domainModule`, `viewModelModule`).
  - `ui`: **Compose Multiplatform** screens, theme, components, and navigation graph.
- **[`:androidApp`](./androidApp)**: Native Android entry point configuring product flavors (`dev`, `prod`), adaptive launcher icons, and activity initialization.

---

## 🛠️ Tech Stack & Libraries

| Technology | Purpose |
| :--- | :--- |
| **Kotlin Multiplatform (2.4.20)** | Cross-platform core shared module |
| **Compose Multiplatform (1.12.1)** | Declarative UI framework across targets |
| **Ktor 3.1.1** | Multiplatform HTTP REST API client with Kotlinx JSON |
| **SQLDelight 2.0.2** | Cross-platform SQLite database persistence & caching |
| **Koin 4.0.2** | Lightweight dependency injection |
| **Coil 3.1.0** | Asynchronous image loading for Compose Multiplatform |
| **Navigation Compose (2.9.2)** | Type-safe screen navigation |
| **KotlinX Coroutines & Serialization** | Asynchronous reactive streams on `Dispatchers.IO` |
| **Detekt & Android Lint** | Static code analysis & quality review |

---

## 🚀 Getting Started

### Prerequisites

- **JDK 17** or higher
- **Android Studio 2026.2+** (or IntelliJ IDEA with KMP plugin)
- Android SDK (`compileSdk 37`, `minSdk 29`)

### Installation & Run

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/slashgil/formula1.git
   cd formula1
   ```

2. **Run Dev Flavor (Technical Blueprint Debug Icon)**:
   ```bash
   ./gradlew :androidApp:installDevDebug
   ```

3. **Run Prod Flavor (Official Red F1 Car Icon)**:
   ```bash
   ./gradlew :androidApp:installProdDebug
   ```

4. **Run Unit Test Suite**:
   ```bash
   ./gradlew :shared:allTests
   ```

5. **Run Static Quality Review (Detekt & Lint)**:
   ```bash
   ./gradlew detekt :androidApp:lint :shared:lint
   ```

---

## 🤖 CI/CD Workflows

The project includes pre-configured **GitHub Actions** workflows:

- **[`ci.yml`](./.github/workflows/ci.yml)**: Automatically builds KMP targets, runs unit tests, compiles the Android Debug APK, and uploads build artifacts on every push or PR.
- **[`quality-checks.yml`](./.github/workflows/quality-checks.yml)**: Executes Detekt code analysis and Android Lint checks on pull requests.
- **[`release-library.yml`](./.github/workflows/release-library.yml)**: Triggered on version tags (`git tag v1.0.0 && git push origin v1.0.0`) to automatically compile, package, and create a GitHub Release with KMP library release binaries (`.aar`, `.jar`, `.klib`).

---

## 📜 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
