# Super Snake Game - Overview and Gameplay Guide

This guide describes the user experience, game objectives, core mechanics, control systems, and navigation flow of Super Snake Game.

## 1. Game Concept and Objective

Super Snake Game is a modern reimagining of the classic arcade Snake game. The player takes control of a snake that continuously moves across a grid.

### Primary Goals

- **Consume Food**: Direct the snake to eat food dots (represented by red circles) that spawn at random coordinates on the grid.
- **Grow Longer**: Each piece of food consumed appends a new segment to the snake's body.
- **Earn Points**: Each food eaten awards 100 points (`GameUISettings.EAT_SCORE = 100`).
- **Beat the High Score**: The player's lifetime record is fetched from and saved to Google Firebase Cloud Firestore, displayed at the top of the screen during gameplay.
- **Survive**: Avoid hitting the grid walls or running into the snake's own tail/body. Any collision triggers Game Over.

## 2. Navigation and User Journey

The application consists of a single Activity architecture using Jetpack Navigation Compose with two primary screens:

```
[ Launch App ]
      │
      ▼
┌──────────────────────────────────────┐
│          MainMenuScreen              │
│  - Logo and Title                    │
│  - Google Sign-In Button             │
│  - Automatic session check           │
└──────────────────┬───────────────────┘
                   │ User signs in / Session active
                   ▼
┌──────────────────────────────────────┐
│          SnakeGameScreen             │
│  - Score Header (Current vs Record)  │
│  - Canvas Game Board (60 FPS)        │
│  - Touch / On-screen Controllers     │
│  - Game Over Overlay (Restart / Exit)│
└──────────────────────────────────────┘
```

### Main Menu (`MainMenuScreen`)

- Displays the game branding: animated pulsing logo, stylized retro neon game title, creator credit ("Desarrollado por Feryael Justice"), and feature highlight pills.
- Shows the dynamic app version number (e.g., `v1.1.0`) at the bottom of the screen.
- On launch, the ViewModel automatically verifies if an active Firebase session already exists via `authRepo.getCurrentFirebaseAuthUser()`. If an authenticated user is found, it navigates directly to the game screen.
- If not signed in, the user is presented with the customized arcade Google Sign-In button. Tapping it opens the Google Credential Manager One-Tap bottom sheet.
- Once authenticated, the navigation controller routes the user to `SnakeGameScreen`.

### Game Screen (`SnakeGameScreen`)

- **Top Scoreboard**: Shows the real-time score of the current run alongside the player's personal high score retrieved from Cloud Firestore, with an integrated pause button and a non-intrusive settings gear icon.
- **Center Canvas (`SnakeGameCanvas`)**: Interactive playing field where the snake is rendered with directional eyes, smooth rounded segments, glowing food dot, and optional retro grid lines.
- **Options and Settings Modal (`GameSettingsSheet`)**: Non-intrusive button opens a bottom sheet with Theme selector (System default, Dark, Light), Speed selection (Chill, Normal, Pro), Grid toggle, Haptics toggle, and Contact/Support card (`fgonzalezserrano10@gmail.com`). Active game loop automatically pauses when the sheet is open.
- **Control Area**: Touch gestures, on-screen arcade D-pad buttons, and keyboard listening area.
- **Game Over State**: When a collision occurs, an arcade overlay presents the run score, high-score comparison with record fanfare, and two primary actions:
  - **Volver a Jugar (Restart Game)**: Resets the snake to the center of the grid, clears the score, and generates new food.
  - **Cerrar Sesión (Sign Out)**: Clears the Firebase authentication session and Credential Manager state, returning the player to the Main Menu.

## 3. Game Controls

The game provides multiple control schemes to ensure accessibility across phones, tablets, and emulators:

### A. On-Screen D-Pad Buttons (`ButtonsDirectionController`)

- Located at the bottom of the screen.
- Features directional buttons (Up, Down, Left, Right) arranged in an arcade cross-pad pattern.
- Direction inputs that would cause an instant 180-degree reversal into the snake's own neck are automatically filtered out to prevent accidental self-elimination.

### B. Direct Touch / Swipe Gestures (`DirectionController`)

- The game canvas captures directional swipes and taps across the screen.
- Players can swipe towards any of the four cardinal directions to redirect the snake.

### C. Physical Keyboard Support (WASD and Arrow Keys)

- Embedded directly into the game canvas using `.onKeyEvent`:
  - `W` or `Key.DirectionUp` -> Move Up
  - `S` or `Key.DirectionDown` -> Move Down
  - `A` or `Key.DirectionLeft` -> Move Left
  - `D` or `Key.DirectionRight` -> Move Right
- Ideal for testing in the Android Emulator, playing on Chromebooks, or using tablets with hardware keyboards.

## 4. Scoring and Cloud Records

1. Every food eaten adds 100 points to the current run score.
2. When a game ends (`isGameOver = true`), the `SnakeGameViewModel` triggers `SaveHighScoreUseCase`.
3. If the final score exceeds the user's historical record in Cloud Firestore, the cloud database is updated atomically via a Firestore transaction.
4. The updated high score is immediately reflected in the UI.
