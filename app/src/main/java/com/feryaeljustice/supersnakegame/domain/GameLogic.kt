package com.feryaeljustice.supersnakegame.domain

import com.feryaeljustice.supersnakegame.ui.screens.game.SnakeGameState
import kotlin.random.Random

object GameUISettings {
    const val SNAKE_SPEED = 10
    const val EAT_SCORE = 100
}

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

enum class Direction { UP, DOWN, LEFT, RIGHT }

fun moveSnake(
    state: SnakeGameState,
    cols: Int,
    rows: Int,
): SnakeGameState {
    val head = state.snake.first()
    val newHead =
        when (state.direction) {
            Direction.UP -> head.copy(second = head.second - 1)
            Direction.DOWN -> head.copy(second = head.second + 1)
            Direction.LEFT -> head.copy(first = head.first - 1)
            Direction.RIGHT -> head.copy(first = head.first + 1)
        }

    // check wall‐collision
    val hitWall =
        newHead.first !in 0 until cols ||
            newHead.second !in 0 until rows

    // check self‐collision
    val hitSelf = newHead in state.snake

    if (hitWall || hitSelf) {
        return state.copy(isGameOver = true)
    }

    // did we eat?
    val ate = newHead == state.food

    val newSnake =
        if (ate) {
            listOf(newHead) + state.snake
        } else {
            listOf(newHead) + state.snake.dropLast(1)
        }

    // new food only if ate
    val newFood =
        if (ate) {
            generateFood(newSnake, cols, rows)
        } else {
            state.food
        }

    return state.copy(
        snake = newSnake,
        food = newFood,
        isGameOver = false,
        score = if (ate) state.score + GameUISettings.EAT_SCORE else state.score,
    )
}
