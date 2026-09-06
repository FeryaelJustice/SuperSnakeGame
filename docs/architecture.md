# Architecture Guide - Super Snake Game

This document provides a comprehensive overview of the architectural patterns, layer responsibilities, and data flow implemented in SuperSnakeGame.

## 1. Architectural Philosophy

The application follows the recommended Android Architecture guidelines based on **Clean Architecture** and **MVVM (Model-View-ViewModel)** with **Unidirectional Data Flow (UDF)**.

### Architectural Principles

- **Separation of Concerns**: Each package and layer has a single, well-defined responsibility.
- **Independence of Frameworks**: Business rules in the Domain layer do not depend on the Android framework, database drivers, or UI components.
- **Testability**: Business logic, use cases, and ViewModels can be tested with standard unit tests without requiring Android device emulators.
- **Loose Coupling**: Dependencies are inverted using interfaces and injected at runtime via Dagger Hilt.

```
┌─────────────────────────────────────────────────────────────┐
│                    Presentation Layer                       │
│  - Jetpack Compose UI (Screens, Components, Theme)          │
│  - ViewModels (MainMenuViewModel, SnakeGameViewModel)       │
│  - UI State (StateFlow) & UI Events (SharedFlow)            │
└──────────────────────────────┬──────────────────────────────┘
                               │ Observes state / Dispatches intents
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                       Domain Layer                          │
│  - Use Cases (GetHighScoreUseCase, SaveHighScoreUseCase)    │
│  - Business Rules (GameLogic.kt, Direction, GameUISettings) │
│  - Repository Contracts (AuthRepository, RecordRepository)  │
│  - Domain Models (AuthResult)                               │
└──────────────────────────────▲──────────────────────────────┘
                               │ Implements interfaces
                               │
┌─────────────────────────────────────────────────────────────┐
│                        Data Layer                           │
│  - Repository Implementations (AuthRepositoryImpl, etc.)    │
│  - External SDKs: Firebase Auth, Cloud Firestore            │
│  - Android Credential Manager & Google Identity             │
└─────────────────────────────────────────────────────────────┘
```

## 2. Layer Breakdown

### A. Domain Layer (`com.feryaeljustice.supersnakegame.domain`)

The core of the application containing pure Kotlin business rules:

- **`GameLogic.kt`**: Contains the pure functions driving the snake movement, wall collision checks, self-collision validation, and food generation algorithms.
- **Use Cases**:
  - `GetHighScoreUseCase`: Encapsulates retrieving the stored high score for a given user ID.
  - `SaveHighScoreUseCase`: Encapsulates saving a new score if it exceeds the prior high score.
- **Repository Interfaces**:
  - `AuthRepository`: Contract defining authentication actions (`requestGoogleIdToken`, `firebaseSignIn`, `getCurrentFirebaseAuthUser`, `signOut`).
  - `RecordRepository`: Contract defining cloud record storage (`getRecordForUser`, `saveIfHigher`).
  - `SettingsRepository`: Contract defining reactive user preferences (`settingsFlow`, `setThemeMode`, `setGameSpeed`, `setShowGrid`, `setHapticsEnabled`).
- **Domain Models**:
  - `AuthResult`: Sealed class modeling authentication outcomes (`Success`, `NeedsUi`, `Failure`).
  - `Direction`: Enum representing cardinal movement (`UP`, `DOWN`, `LEFT`, `RIGHT`).
  - `ThemeMode`: Enum representing active theme mode (`SYSTEM`, `DARK`, `LIGHT`).
  - `GameSpeed`: Enum representing game speed presets (`CHILL`, `NORMAL`, `FAST`).
  - `GameSettings`: Data class holding user configuration state.

### B. Data Layer (`com.feryaeljustice.supersnakegame.data`)

Responsible for retrieving, persisting, and synchronizing data with remote backends:

- **`AuthRepositoryImpl.kt`**:
  - Interfaces with Android's `CredentialManager` to request Google ID tokens via `GetGoogleIdOption`.
  - Converts retrieved tokens into Firebase credentials using `GoogleAuthProvider.getCredential(idToken, null)`.
  - Handles session sign-out by clearing both Firebase Auth and Credential Manager states.
- **`RecordRepositoryImpl.kt`**:
  - Interacts with Firebase Cloud Firestore under the `records` collection.
  - Utilizes atomic transactions (`firestore.runTransaction`) to guarantee race-condition-free updates to user high scores.
- **`SettingsRepositoryImpl.kt`**:
  - Interacts with Android's `SharedPreferences` to load and persist user theme choices, game speed, grid lines toggle, haptic feedback, and sound effects volume/toggle.
- **`data/audio/SoundEffectManager.kt`**:
  - Manages low-latency audio playback for gameplay events using Android's native `SoundPool`.
  - Implements `DefaultLifecycleObserver` to pause, resume, and release audio resources in synchronization with Android lifecycle events.

### C. Presentation Layer (`com.feryaeljustice.supersnakegame.ui`)

Built entirely with modern Jetpack Compose:

- **Navigation (`ui/navigation/`)**:
  - Single Activity host (`MainActivity`) executing `AppNavigation`.
  - Observes `SettingsRepository.settingsFlow` to dynamically apply `ThemeMode` to `SuperSnakeGameTheme`.
  - Type-safe navigation using Kotlin Serialization and `@Serializable` route definitions (`MenuScreen`, `GameScreen(data)`).
- **Screens & ViewModels (`ui/screens/`)**:
  - `MainMenuScreen` & `MainMenuViewModel`: Manages authentication state (`Idle`, `Loading`, `LaunchUi`, `SignedIn`, `Error`), handles One-Tap callbacks, and displays dynamic version numbers.
  - `SnakeGameScreen` & `SnakeGameViewModel`: Manages live game state (`snakeState`), pause state (`isPaused`), game loop speed (`moveDelayMs`), running status (`gameRunning`), and score tracking.
- **Components (`ui/components/`)**:
  - `SnakeGameCanvas`: High-performance custom Canvas rendering for snake segments with directional eyes, glowing food dot, and optional retro grid.
  - `GameSettingsSheet`: Reusable modal bottom sheet containing theme mode options, game speed, grid switch, haptic switch, developer contact card, and version info.
  - `DirectionController`: Gesture overlay capturing touch and swipe inputs.
  - `ButtonsDirectionController`: Arcade D-pad button layout with neon accents and tactile control.
  - `GoogleButton`: Standardized Google Sign-In button with loading spinner state and neon styling.

### D. Dependency Injection (`com.feryaeljustice.supersnakegame.di`)

Configured with Dagger Hilt:

- **`AuthModule.kt`**: Provides singleton instances of `FirebaseAuth`, `CredentialManager`, and the configured Web Client ID string.
- **`RepositoryModule.kt`**: Binds repository interfaces (`AuthRepository`, `RecordRepository`, `SettingsRepository`) to their implementations (`AuthRepositoryImpl`, `RecordRepositoryImpl`, `SettingsRepositoryImpl`).
- **`StorageModule.kt`**: Provides the singleton `FirebaseFirestore` instance.

## 3. Unidirectional Data Flow (UDF)

The application adheres to UDF principles across all screens:

1. **State Flows Down**: ViewModels expose immutable `StateFlow<UiState>` collected by Composables using `collectAsStateWithLifecycle()` or `collectAsState()`.
2. **Events Flow Up**: User interactions (button taps, direction changes, keyboard inputs) trigger ViewModel functions.
3. **Immutability**: Game state is modeled as immutable data classes (`SnakeGameState`). State updates produce fresh copies via `copy(...)` or atomic updates via `.update { ... }`.
