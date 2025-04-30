package com.feryaeljustice.supersnakegame.ui.screens.game

import com.feryaeljustice.supersnakegame.domain.Direction
import com.feryaeljustice.supersnakegame.domain.generateFood

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
            val startX = cols / 2
            val startY = rows / 2
            val dir = Direction.entries.toTypedArray().random()
            return SnakeGameState(
                snake = listOf(startX to startY),
                food = generateFood(listOf(startX to startY), cols, rows),
                direction = dir,
                isGameOver = false,
                score = 0,
            )
        }
    }
}
