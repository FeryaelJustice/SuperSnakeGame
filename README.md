# Super Snake Game

Arcade retro snake game built for modern Android using Jetpack Compose, Material 3, Clean Architecture, MVVM, Dagger Hilt, and Firebase Authentication & Cloud Firestore.

## Overview

Super Snake Game is an arcade mobile game where players control a snake inside a dynamic grid. The objective is to eat food to grow and achieve the highest possible score without colliding with the boundaries or the snake's own body. High scores are synchronized in real time with Google Firebase Cloud Firestore tied to the user's Google account.

## Key Features

- **Modern Jetpack Compose UI**: Declarative user interface with smooth custom Canvas rendering for the game board.
- **Google Sign-In with Credential Manager**: Seamless, secure authentication using Android's Credential Manager and Firebase Authentication.
- **Cloud High Scores**: Automatic synchronization and persistent record tracking per user in Firebase Cloud Firestore using atomic transactions.
- **Dynamic Adaptive Grid**: Game canvas calculates optimal grid dimensions (`cols` x `rows`) based on the device screen size and aspect ratio.
- **Multiple Input Controls**:
  - Touch swipe / directional pad gestures on the screen.
  - On-screen directional buttons for precise arcade-style steering.
  - Physical keyboard support (Arrow keys and W/A/S/D) for Chromebooks, tablets, and emulators.
- **Clean Architecture & MVVM**: Modular separation of concerns into Domain, Data, and Presentation layers.
- **Dependency Injection**: Fully wired with Dagger Hilt for loose coupling and testability.

## Tech Stack

- **UI & Design**: Jetpack Compose, Material 3, Canvas graphics
- **Architecture**: Clean Architecture + MVVM (Model-View-ViewModel) + Single Activity
- **Asynchronous Flow**: Kotlin Coroutines, StateFlow, SharedFlow
- **Dependency Injection**: Dagger Hilt
- **Authentication**: Google Credential Manager (`androidx.credentials`), Google Identity (`GetGoogleIdOption`), Firebase Auth
- **Database**: Cloud Firestore
- **Navigation**: Jetpack Navigation Compose with type-safe routing
- **Target SDK**: Android 14 / Android 15 (compileSdk 37, minSdk 24, targetSdk 37)

## Architecture Overview

The project adheres to Clean Architecture guidelines:

```
app/src/main/java/com/feryaeljustice/supersnakegame/
├── data/              # Data layer: repository implementations, data sources
│   └── repository/    # AuthRepositoryImpl, RecordRepositoryImpl
├── di/                # Dependency injection modules (Hilt)
│   ├── AuthModule.kt
│   ├── RepositoryModule.kt
│   └── StorageModule.kt
├── domain/            # Domain layer: pure Kotlin business logic, models, use cases
│   ├── model/         # Domain models and enums (Direction, AuthResult)
│   ├── repository/    # Repository interfaces (AuthRepository, RecordRepository)
│   ├── usecase/       # GetHighScoreUseCase, SaveHighScoreUseCase
│   └── GameLogic.kt   # Pure movement, collision, and food generation logic
└── ui/                # Presentation layer: Jetpack Compose UI and ViewModels
    ├── components/    # Canvas, directional buttons, Google sign-in button
    ├── navigation/    # Type-safe navigation graph and route destinations
    ├── screens/       # MainMenuScreen, SnakeGameScreen and ViewModels
    └── theme/         # Color palettes, typography, theme definition
```

## Getting Started

### Prerequisites

1. **Android Studio**: Android Studio Ladybug (or newer).
2. **JDK**: Java 17 or Java 21 configured in Android Studio.
3. **Firebase Project**:
   - Create a project in the [Firebase Console](https://console.firebase.google.com/).
   - Add an Android application with package name `com.feryaeljustice.supersnakegame`.
   - Download `google-services.json` and place it in the `app/` folder.
   - Enable **Google Sign-In** under **Authentication -> Sign-in method**.
   - Create a **Cloud Firestore** database.
   - Configure the Web Client ID in `app/src/main/res/values/strings.xml`:
     ```xml
     <string name="default_web_client_id" translatable="false">YOUR_WEB_CLIENT_ID.apps.googleusercontent.com</string>
     ```
   - Register your debug and release SHA-1 certificate fingerprints in the Firebase Console project settings.

### Building and Running

1. Clone the repository:
   ```bash
   git clone https://github.com/FeryaelJustice/SuperSnakeGame.git
   cd SuperSnakeGame
   ```
2. Build the debug APK using Gradle:
   ```bash
   ./gradlew assembleDebug
   ```
3. Run on a connected device or emulator:
   ```bash
   ./gradlew installDebug
   ```

## Documentation

Comprehensive project documentation is available in the `docs/` folder:

- [docs/overview-and-gameplay.md](docs/overview-and-gameplay.md) - Game manual, objectives, mechanics, scoring, and UI navigation.
- [docs/ui-and-customization.md](docs/ui-and-customization.md) - UI design system, arcade neon aesthetics, settings modal, theme modes, and contact channels.
- [docs/architecture.md](docs/architecture.md) - In-depth breakdown of Clean Architecture, MVVM, and Hilt DI.
- [docs/authentication-flow.md](docs/authentication-flow.md) - Credential Manager, nonce generation, and Firebase Auth workflow.
- [docs/game-logic-and-engine.md](docs/game-logic-and-engine.md) - Real-time tick engine, dual-ticker architecture, and collision mathematics.
- [docs/firestore-and-data.md](docs/firestore-and-data.md) - Firestore collection schema, concurrency, and transaction rules.
- [docs/google-play-app-access.md](docs/google-play-app-access.md) - Step-by-step resolution for Google Play Console App Access policy review.

## License

This project is open-source and available under the MIT License.
