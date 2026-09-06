# SuperSnakeGame - Agent Guidelines

This document outlines the rules, architectural patterns, conventions, and operational policies for AI agents working within the `SuperSnakeGame` codebase.

## Shared Agent Policy Inheritance

This project adheres to the global shared agent policy established in `C:\Users\nano9\.agents\AGENTS.md`.

### Writing Style Rule (Strict)

- **Never use em dashes (—) or en dashes (–) in any output.** Use a plain hyphen (-) instead, always. This applies to all text: code comments, documentation, commit messages, explanations, chat responses, and file content.

### Shared Local CLIs

- Android CLI is available as `android`. For Android tasks, consult the shared `android-cli` skill from `C:\Users\nano9\.agents\skills\android-cli` before falling back to ad hoc commands.
- Antigravity CLI is available as `agy` (`C:\Users\nano9\AppData\Local\agy\bin\agy.exe`). Use it when Antigravity-specific project/agent operations are requested.

## Architecture and Design Principles

SuperSnakeGame follows strict Clean Architecture guidelines combined with MVVM and reactive Kotlin Coroutines:

1. **Domain Layer (`domain/`)**:
   - Pure Kotlin module with zero Android framework dependencies.
   - Contains domain models (`AuthResult`, `Direction`), business logic functions (`GameLogic.kt`), use cases (`GetHighScoreUseCase`, `SaveHighScoreUseCase`), and repository interfaces (`AuthRepository`, `RecordRepository`).
   - Business rules must remain deterministic and easily testable without Android mocks.

2. **Data Layer (`data/`)**:
   - Implements repository interfaces defined in the domain layer.
   - Interacts with external SDKs: Firebase Authentication, Cloud Firestore, and Android Credential Manager.
   - All asynchronous calls return suspend functions or Flows. Exceptions must be caught and mapped to domain outcomes.

3. **Presentation Layer (`ui/`)**:
   - Exclusively built with Jetpack Compose and Material 3.
   - Every screen has a dedicated ViewModel exposing an immutable `StateFlow<UiState>` and one-off `SharedFlow<UiEvent>`.
   - UI components must be stateless where possible, accepting states and lambdas for event hoisting.
   - Graphics and game board rendering are handled via custom `Canvas` drawing for 60 FPS rendering.

4. **Dependency Injection (`di/`)**:
   - Dagger Hilt is used throughout the project.
   - Android Entry Points: `SuperApp` (`@HiltAndroidApp`), `MainActivity` (`@AndroidEntryPoint`), and ViewModels (`@HiltViewModel`).
   - Modules (`AuthModule`, `RepositoryModule`, `StorageModule`) provide singleton bindings.

## Authentication and Play Store Guidelines

- The primary authentication mechanism is Google Sign-In managed via Android Credential Manager (`androidx.credentials`) and Firebase Authentication.
- When working on authentication or submission tasks, consult `docs/google-play-app-access.md` to ensure full compliance with Google Play Console App Access policies.
- Always provide clear reviewer instructions in English and ensure credentials/bypass mechanisms remain accessible.

## Development and Build Guidelines

- Build tool: Gradle with Kotlin DSL (`build.gradle.kts`).
- Keep code formatted according to standard Kotlin conventions.
- When adding new dependencies, update `gradle/libs.versions.toml` rather than hardcoding versions in module build files.
- Always verify compilation and tests after modifying code using `./gradlew test` or `android` CLI commands.
