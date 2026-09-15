<p align="center">
  <img src="app/src/main/res/drawable/logo_supersnakegame.webp" width="160" height="160" alt="Super Snake Game Logo" style="border-radius: 28px;" />
</p>

<h1 align="center">Super Snake Game</h1>

<p align="center">
  <strong>Retro Arcade Experience Reimagined for Modern Android</strong>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white" alt="Platform: Android" />
  <img src="https://img.shields.io/badge/Kotlin-2.4.10-7F52FF?logo=kotlin&logoColor=white" alt="Kotlin" />
  <img src="https://img.shields.io/badge/Compose_BOM-2026.08.00-4285F4?logo=jetpackcompose&logoColor=white" alt="Compose BOM" />
  <img src="https://img.shields.io/badge/Architecture-Clean_%2B_MVVM-blue" alt="Clean Architecture" />
  <img src="https://img.shields.io/badge/DI-Hilt_2.60.1-orange" alt="Dagger Hilt" />
  <img src="https://img.shields.io/badge/Firebase-Auth_%26_Firestore-FFCA28?logo=firebase&logoColor=black" alt="Firebase" />
  <img src="https://img.shields.io/badge/License-MIT-green.svg" alt="License: MIT" />
</p>

<p align="center">
  <a href="#-overview">Overview</a> •
  <a href="#-key-features">Key Features</a> •
  <a href="#-tech-stack--specifications">Tech Stack</a> •
  <a href="#-architecture">Architecture</a> •
  <a href="#-getting-started">Getting Started</a> •
  <a href="#-audio--sound-effects">Audio</a> •
  <a href="#-documentation">Documentation</a> •
  <a href="#-license">License</a>
</p>

---

## 🌟 Overview

**Super Snake Game** brings the legendary arcade snake experience into the modern Android ecosystem. Designed with an authentic 80s arcade neon aesthetic, it delivers ultra-smooth 60 FPS gameplay rendered with custom Jetpack Compose Canvas graphics.

Players navigate the snake across a dynamic screen-adaptive grid, eating glowing food dots, growing in length, and competing for personal high scores synchronized automatically in real time with **Google Firebase Cloud Firestore** and **Google Credential Manager**.

```
    [ 🎮 Main Menu ]  ──( Google Sign-In )──>  [ 🕹️ Arcade Grid (60 FPS) ]
           │                                                    │
           └──── Session Active (Auto-Skip) ────────────────────┘
```

---

## ✨ Key Features

| Category | Highlights |
| :--- | :--- |
| **🎨 Visuals & Rendering** | Custom `Canvas` rendering at 60 FPS, dynamic glowing food halos, directional snake eyes, and retro neon grid overlay. |
| **🔐 Authentication** | Instant Google Sign-In powered by Android Credential Manager (`androidx.credentials`) with cryptographic nonces and Firebase Authentication. |
| **☁️ Cloud Persistence** | Real-time high-score sync backed by Cloud Firestore with atomic transactions (`firestore.runTransaction`) to guarantee monotonic record progression. |
| **📐 Dynamic Grid** | Board dimensions (`cols` x `rows`) dynamically adapt to the physical screen aspect ratio, ensuring square cells on smartphones, foldables, and tablets. |
| **🕹️ Flexible Controls** | Touch swipe gestures, on-screen arcade D-pad buttons, and physical hardware keyboard support (Arrow keys and W/A/S/D). |
| **🔊 Arcade Audio** | Low-latency sound effects engine powered by `SoundPool` with automatic lifecycle management (`autoPause`/`autoResume`). |
| **⚙️ Customization** | Built-in settings sheet with speed selection (Chill, Normal, Pro), theme toggle (System, Dark, Light), grid visibility, and haptics. |

---

## 🛠️ Tech Stack & Specifications

### Core Technologies

- **Language:** Kotlin 2.4.10 (Modern Coroutines, StateFlow, SharedFlow, Serialization)
- **UI Framework:** Jetpack Compose with Material 3 Design System
- **Build System:** Gradle with Kotlin DSL (`build.gradle.kts`), AGP 9.4.0, KSP 2.3.2
- **Dependency Injection:** Dagger Hilt 2.60.1
- **Cloud Backend:** Firebase Auth & Cloud Firestore (Firebase BOM 34.18.0)
- **Authentication:** Google Credential Manager (`androidx.credentials` 1.6.0) & Google Identity
- **Sound Engine:** Android `SoundPool` with `AudioAttributes.USAGE_GAME`

### Android Specifications

```ini
minSdk     = 24  (Android 7.0 Nougat)
compileSdk = 37  (Android 15+)
targetSdk  = 37  (Android 15+)
versionCode = 10
versionName = "2.0.0"
```

---

## 🏛️ Architecture

Super Snake Game follows strict **Clean Architecture** and **MVVM** (Model-View-ViewModel) patterns with **Unidirectional Data Flow (UDF)**:

```
┌─────────────────────────────────────────────────────────────┐
│                    Presentation Layer                       │
│  - Jetpack Compose UI (Screens, Canvas, Theme, Controls)    │
│  - ViewModels (MainMenuViewModel, SnakeGameViewModel)       │
│  - Immutable UiState (StateFlow) & One-off Events (SharedFlow)│
└──────────────────────────────┬──────────────────────────────┘
                               │ Observes state / Dispatches intents
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                       Domain Layer                          │
│  - Pure Kotlin business logic (GameLogic.kt, Direction)     │
│  - Use Cases (GetHighScoreUseCase, SaveHighScoreUseCase)    │
│  - Repository Contracts (AuthRepository, RecordRepository)  │
│  - Domain Models (SnakeGameState, AuthResult, GameSettings) │
└──────────────────────────────▲──────────────────────────────┘
                               │ Implements interfaces
                               │
┌─────────────────────────────────────────────────────────────┐
│                        Data Layer                           │
│  - Repository Implementations (AuthRepositoryImpl, etc.)    │
│  - Cloud Firestore Client with Atomic Transactions          │
│  - Firebase Authentication & Google Credential Manager      │
│  - SoundEffectManager with Android Lifecycle Observer       │
└─────────────────────────────────────────────────────────────┘
```

### Package Structure

```
app/src/main/java/com/feryaeljustice/supersnakegame/
├── data/                  # Data layer implementations
│   ├── audio/             # SoundEffectManager (SoundPool audio system)
│   └── repository/        # AuthRepositoryImpl, RecordRepositoryImpl, SettingsRepositoryImpl
├── di/                    # Dependency injection modules (Hilt)
│   ├── AuthModule.kt      # Credential Manager & Firebase Auth bindings
│   ├── RepositoryModule.kt# Repository interface implementations
│   └── StorageModule.kt   # Firestore and local preferences
├── domain/                # Pure Kotlin domain layer (zero Android dependencies)
│   ├── model/             # Models and enums (Direction, AuthResult, ThemeMode, GameSpeed)
│   ├── repository/        # Repository interfaces (AuthRepository, RecordRepository)
│   ├── usecase/           # GetHighScoreUseCase, SaveHighScoreUseCase
│   ├── GameLogic.kt       # Pure movement, collision math, and food spawn algorithms
│   └── SnakeGameState.kt  # Immutable game snapshot model
└── ui/                    # Presentation layer: Jetpack Compose & ViewModels
    ├── components/        # SnakeGameCanvas, arcade D-pad, Google sign-in button
    ├── navigation/        # AppNavigation graph and type-safe destinations
    ├── screens/
    │   ├── game/          # SnakeGameScreen, GameSettingsSheet, SnakeGameViewModel
    │   └── menu/          # MainMenuScreen, ArcadeFeaturePill, MainMenuViewModel
    └── theme/             # Arcade neon palette, typography, shapes, SuperSnakeGameTheme
```

---

## 🚀 Getting Started

### Prerequisites

1. **Android Studio**: Android Studio Ladybug (or newer)
2. **JDK**: Java 17 or Java 21 configured in Android Studio
3. **Android Device / Emulator**: Running Android 7.0 (API 24) or higher

### Firebase Setup

1. Create a project in the [Firebase Console](https://console.firebase.google.com/).
2. Add an Android application with package name:
   ```
   com.feryaeljustice.supersnakegame
   ```
3. Download `google-services.json` and copy it into the `app/` directory:
   ```
   app/google-services.json
   ```
4. Enable **Google Sign-In** under **Authentication -> Sign-in method**.
5. Enable **Cloud Firestore** in test or production mode.
6. Configure the OAuth Web Client ID in `app/src/main/res/values/strings.xml`:
   ```xml
   <string name="default_web_client_id" translatable="false">YOUR_WEB_CLIENT_ID.apps.googleusercontent.com</string>
   ```
7. Register your debug and release SHA-1 certificate fingerprints in your Firebase project settings.

### Building and Running

```bash
# 1. Clone the repository
git clone https://github.com/FeryaelJustice/SuperSnakeGame.git
cd SuperSnakeGame

# 2. Build the debug APK
./gradlew assembleDebug

# 3. Install and run on connected device/emulator
./gradlew installDebug
```

---

## 🔊 Audio & Sound Effects

Super Snake Game integrates an ultra-low-latency sound system designed specifically for fast arcade interactions:

- **Technology:** `SoundPool` with `AudioAttributes.USAGE_GAME` and `CONTENT_TYPE_SONIFICATION`.
- **Latency:** Instant playback (< 10 ms) with pre-decompressed 16-bit PCM audio in memory.
- **Sound Assets:** Retro arcade bite sound (`app/src/main/res/raw/eat_apple.wav`, 44.1 kHz mono).
- **Lifecycle Integration:** Implements `DefaultLifecycleObserver` to automatically call `autoPause()` on backgrounding, `autoResume()` on foregrounding, and `release()` on activity disposal to eliminate memory leaks.

---

## 🎮 Controls Reference

| Control Method | Actions | Platform / Scenario |
| :--- | :--- | :--- |
| **Touch Swipes** | Swipe Up, Down, Left, Right | Smartphones & Tablets |
| **Arcade D-Pad** | On-screen directional buttons | One-handed touch gameplay |
| **Keyboard** | Arrow keys (`↑`, `↓`, `←`, `→`) or `W`, `A`, `S`, `D` | Emulators, Chromebooks & Tablets |
| **Pause / Resume** | Top scoreboard pause button or Options gear | In-game action |

---

## 📚 Documentation

Detailed architectural and technical guides are available in the [`docs/`](docs/) directory:

- [Overview & Gameplay Guide](docs/overview-and-gameplay.md) - Game manual, scoring rules, and navigation flow.
- [UI & Customization Guide](docs/ui-and-customization.md) - Arcade neon palette, settings bottom sheet, and theme modes.
- [Architecture Guide](docs/architecture.md) - Deep dive into Clean Architecture, MVVM, and Hilt modules.
- [Game Logic & Engine Guide](docs/game-logic-and-engine.md) - Collision detection, dynamic grid mathematics, and tick rate engine.
- [Audio Subsystem Guide](docs/audio-system.md) - `SoundPool` architecture, sound synthesis, and lifecycle integration.
- [Firestore & Persistence Guide](docs/firestore-and-data.md) - Schema design, concurrency handling, and atomic transactions.
- [Authentication Flow Guide](docs/authentication-flow.md) - Credential Manager, nonces, and Firebase Auth workflow.
- [Audit & Optimizations Report](docs/audit-and-optimizations-report.md) - Comprehensive performance audit, Lint analysis, and ANR prevention.
- [Google Play App Access Guide](docs/google-play-app-access.md) - Review compliance guide for Google Play Console testers.

---

## 📄 License

This project is licensed under the [MIT License](LICENSE) - see the LICENSE file for details.

Developed with ❤️ by **Feryael Justice**.
