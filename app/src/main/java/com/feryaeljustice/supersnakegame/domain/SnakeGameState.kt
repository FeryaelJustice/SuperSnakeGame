package com.feryaeljustice.supersnakegame.domain

data class SnakeGameState(
    val snake: List<Pair<Int, Int>> = listOf(Pair(5, 5)),
    val food: Pair<Int, Int> = Pair(10, 10),
    val direction: Direction = Direction.RIGHT,
    val isGameOver: Boolean = false,
    val score: Int = 0,
) {
    companion object {
        fun initial(
            cols: Int,
            rows: Int,
        ): SnakeGameState {
            val startX = (cols / 2).coerceAtLeast(0)
            val startY = (rows / 2).coerceAtLeast(0)
            val dir = Direction.entries.toTypedArray().random()
            val initialSnake = listOf(startX to startY)
            return SnakeGameState(
                snake = initialSnake,
                food = generateFood(initialSnake, cols, rows),
                direction = dir,
                isGameOver = false,
                score = 0,
            )
        }
    }
}
