# Game Logic and Engine Guide - Super Snake Game

This document details the mechanics, mathematics, tick systems, and rendering engine driving the gameplay in Super Snake Game.

## 1. Grid Coordinate System

The game operates on a two-dimensional integer grid coordinate system where:
- The top-left corner is `(0, 0)`.
- The horizontal axis `X` ranges from `0` to `cols - 1`.
- The vertical axis `Y` ranges from `0` to `rows - 1`.

### Dynamic Screen Adaptation

Rather than enforcing a fixed resolution, the game dynamically calculates grid dimensions to match the physical device display using Jetpack Compose's `Modifier.onSizeChanged`:

```kotlin
val cellW = size.width / 20f
val cellH = size.height / 20f
val cellSize = min(cellW, cellH)

cols = floor(size.width / cellSize).toInt()
rows = floor(size.height / cellSize).toInt()

viewModel.setGridSize(cols, rows)
```

This guarantees square grid cells across all aspect ratios, preventing distortion on tablets, folding devices, and smartphones.

## 2. State Representation (`SnakeGameState`)

The entire game state is encapsulated within an immutable data class:

```kotlin
data class SnakeGameState(
    val snake: List<Pair<Int, Int>> = listOf(Pair(5, 5)),
    val food: Pair<Int, Int> = Pair(10, 10),
    val direction: Direction = Direction.RIGHT,
    val isGameOver: Boolean = false,
    val score: Int = 0,
)
```

- **`snake`**: An ordered list of `(X, Y)` pairs. The first element (`snake.first()`) is the snake's head. Subsequent elements represent body segments down to the tail.
- **`food`**: An `(X, Y)` coordinate pair representing the current food position.
- **`direction`**: Current movement heading (`UP`, `DOWN`, `LEFT`, `RIGHT`).
- **`isGameOver`**: Boolean flag indicating whether a fatal collision has occurred.
- **`score`**: Current accumulated score for the active run.

## 3. Dual-Ticker Architecture

SuperSnakeGame decouples visual rendering from game physics through a dual-ticker pattern in `SnakeGameScreen.kt`:

```
┌─────────────────────────────────────────────────────────────┐
│                    Ticker 1: Visual Rendering               │
│  - Powered by withFrameNanos                                │
│  - Runs at display refresh rate (~60 FPS / ~120 FPS)        │
│  - Ensures buttery-smooth UI transitions and animations     │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                    Ticker 2: Game Physics                   │
│  - Powered by coroutine delay(moveDelayMs)                  │
│  - Runs at game tick speed (default: 10 FPS / 100 ms)       │
│  - Executes moveSnakeTo() to advance position               │
└─────────────────────────────────────────────────────────────┘
```

### Movement Ticker Loop

```kotlin
LaunchedEffect(gameRunning, moveDelay) {
    while (gameRunning && !gameState.isGameOver) {
        delay(moveDelay)
        viewModel.moveSnakeTo()
    }
}
```

This separation ensures that physical simulation remains consistent and predictable regardless of display refresh rate fluctuations.

## 4. Physics and Movement Logic (`moveSnake`)

Every physics tick evaluates `moveSnake(state, cols, rows)`:

### A. New Head Calculation

Depending on the current direction, the head advances by one grid unit:
- `Direction.UP` -> `(head.x, head.y - 1)`
- `Direction.DOWN` -> `(head.x, head.y + 1)`
- `Direction.LEFT` -> `(head.x - 1, head.y)`
- `Direction.RIGHT` -> `(head.x + 1, head.y)`

### B. Collision Validation

1. **Wall Collision**:
   ```kotlin
   val hitWall = newHead.first !in 0 until cols || newHead.second !in 0 until rows
   ```
2. **Self Collision**:
   ```kotlin
   val hitSelf = newHead in state.snake
   ```

If either collision condition evaluates to true, the function returns `state.copy(isGameOver = true)`, halting the physics ticker.

### C. Consumption and Growth

The algorithm checks whether the snake's new head occupies the food coordinates:
```kotlin
val ate = newHead == state.food
```

- **If food was eaten**:
  - The new head is prepended to the existing snake list without dropping the tail (`listOf(newHead) + state.snake`), effectively increasing the snake's length by 1.
  - A new food location is generated using `generateFood(newSnake, cols, rows)`.
  - The score increases by `GameUISettings.EAT_SCORE` (100 points).
- **If food was not eaten**:
  - The new head is prepended and the last segment (tail) is discarded (`listOf(newHead) + state.snake.dropLast(1)`), maintaining constant length.

## 5. Food Generation Algorithm (`generateFood`)

Food generation guarantees that food never spawns inside the snake's body:

```kotlin
fun generateFood(
    snake: List<Pair<Int, Int>>,
    cols: Int,
    rows: Int,
): Pair<Int, Int> {
    var candidate: Pair<Int, Int>
    do {
        candidate = Pair(Random.nextInt(cols), Random.nextInt(rows))
    } while (candidate in snake)
    return candidate
}
```

## 6. Direction Validation (180-Degree Turn Prevention)

To prevent players from accidentally crashing into their own neck by pressing the opposite direction, `SnakeGameViewModel` verifies incoming directional commands:

```kotlin
fun setNewDirection(newDirection: Direction) {
    val opposite = when (_snakeState.value.direction) {
        Direction.UP -> Direction.DOWN
        Direction.DOWN -> Direction.UP
        Direction.LEFT -> Direction.RIGHT
        Direction.RIGHT -> Direction.LEFT
    }
    if (newDirection != opposite) {
        _snakeState.update { it.copy(direction = newDirection) }
    }
}
```
