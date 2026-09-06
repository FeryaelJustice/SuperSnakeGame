# UI, Theming and Customization Guide - Super Snake Game

This document details the visual design system, custom aesthetics, options modal, theming architecture, and contact support integrations in Super Snake Game.

## 1. Visual Design System and Aesthetic Identity

SuperSnakeGame features a custom arcade retro-modern aesthetic inspired by classic 80s arcade neon cabinets combined with modern Android Material 3 design principles.

### Color Palette

The application defines a dedicated arcade neon palette in `ui/theme/Color.kt`:

- **NeonGreen (`#00E676`)**: Primary accent representing the snake, life, and confirmation actions.
- **NeonGreenDark (`#00B248`)**: Gradient shadow and depth for snake segments.
- **NeonCyan (`#00E5FF`)**: Secondary cyber accent for section headings, interactive icons, and subtle borders.
- **NeonRed (`#FF3366`)**: High-visibility fruit color and game-over alert state.
- **NeonYellow (`#FFD600`)**: Digital high-score highlights and achievement badges.
- **ArcadeDarkBg (`#0A0E17`)**: Deep space obsidian background optimizing contrast and battery life on OLED screens.
- **ArcadeDarkCard (`#1A2234`)**: Elevated translucent surface container for menus, cards, and modal sheets.
- **ArcadeBoardBg (`#0D131F`)**: Playing field background providing high contrast against the snake and fruit.
- **ArcadeGridLine (`#1400E5FF`)**: Faint retro grid lines marking grid coordinates without distracting from gameplay.

---

## 2. Main Menu and Authentication Screen (`MainMenuScreen`)

The entry screen has been customized to reflect the arcade game identity rather than a standard generic authentication form:

1. **Header Badge**: Displays a glowing "ARCADE RETRO EDITION" badge with star accents.
2. **Pulsing Logo Frame**: The game logo (`logo_supersnakegame`) is framed inside a dual-ring circular border with a subtle rhythmic scale animation.
3. **Typography**: "SUPER SNAKE GAME" rendered in heavy headline typography with neon green lettering, accompanied by the creator credit ("Desarrollado por Feryael Justice").
4. **Arcade Highlights**: Three feature badges highlight core app strengths:
   - "60 FPS" (Smooth canvas loop)
   - "Nube" (Cloud record persistence)
   - "D-Pad" (Tactile arcade controls)
5. **Arcade-Themed Google Sign-In**: Framed within an elevated card with neon outline and loading feedback.
6. **Version Display**: The exact version number (e.g., `v1.1.0`) is dynamically queried from the Android `PackageManager` and displayed at the bottom of the screen.

---

## 3. In-Game Controls, Scoreboard and Options Button (`SnakeGameScreen`)

### Digital Scoreboard
- Located at the top of the screen in an elevated neon pill surface.
- Displays current run points ("PTS 100") and personal all-time cloud record ("RÉCORD: 500").
- Contains pause and settings action buttons.

### Non-Intrusive Options Button
- A gear icon located in the scoreboard header gives quick, non-intrusive access to settings at any time.
- **Auto-Pause Mechanism**: Tapping the options button automatically pauses the active game loop. The snake stops moving while the player adjusts settings, preventing unintended collisions. The game resumes upon dismissing the sheet.

### Game Board Canvas (`SnakeGameCanvas`)
- **Directional Snake Eyes**: The head segment renders two white eyes with pupils pointing toward the active direction of movement (`UP`, `DOWN`, `LEFT`, `RIGHT`).
- **Rounded Body Segments**: Body nodes render with smooth rounded corners and a neon emerald gradient.
- **Glowing Food**: Rendered with an outer pulsing halo, radial color gradient, and inner specular highlight.
- **Retro Grid**: Optional grid lines showing cell boundaries across the 20x20 playing field.

### Game Over Experience
- An arcade modal card appears over the board displaying the run's final score, high-score comparison, and fanfare badges if a new personal record was achieved.
- Offers direct actions: "Volver a Jugar" (Restart) and "Cerrar Sesión" (Sign Out).

---

## 4. Settings and Options Modal (`GameSettingsSheet`)

The settings sheet is built using Material 3 `ModalBottomSheet` and provides five main sections:

### A. Theme Selection
- **Options**: "Sistema (Predeterminado)", "Oscuro", "Claro".
- **Default**: `ThemeMode.SYSTEM` (automatically adapts to device day/night settings).
- Updates application colors dynamically across all screens.

### B. Game Speed / Difficulty
- **Chill (7 FPS)**: Relaxed pace for casual play.
- **Normal (10 FPS)**: Standard arcade challenge.
- **Pro (14 FPS)**: Fast-paced high-reflex challenge.
- Directly updates the snake move loop delay (`1000L / fps`).

### C. Display and Gameplay Preferences
- **Cuadrícula retro**: Toggle to display or hide the subtle grid lines on the board.
- **Vibración háptica**: Toggle to enable or disable tactile vibration on consuming fruit and on game over.

### D. Contact and Support Card
- Developer attribution: "Desarrollado por Feryael Justice".
- Direct email address: `fgonzalezserrano10@gmail.com`.
- Actions:
  - **Enviar correo**: Launches an Android email `Intent` with pre-filled subject line.
  - **Copiar**: Copies the email address to clipboard and provides toast confirmation.

### E. Version and Build Info
- Displays the official version name (e.g., `Super Snake Game v1.1.0`).

---

## 5. Architecture and Preferences Persistence

### Domain Models
- `ThemeMode`: Enum (`SYSTEM`, `DARK`, `LIGHT`).
- `GameSpeed`: Enum (`CHILL`, `NORMAL`, `FAST`).
- `GameSettings`: Data class holding user configuration state.
- `SettingsRepository`: Interface defining reactive `settingsFlow` and update functions.

### Data Layer
- `SettingsRepositoryImpl`: Backed by Android `SharedPreferences` (`super_snake_settings_prefs`), ensuring preferences survive app restarts.

### Application Entry Point
- In `MainActivity`, `SettingsRepository` is injected and observed as Compose state. The active `themeMode` is passed directly to `SuperSnakeGameTheme`, enabling instant UI-wide theme changes.
