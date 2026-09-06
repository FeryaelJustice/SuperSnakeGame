package com.feryaeljustice.supersnakegame.domain

import com.feryaeljustice.supersnakegame.ui.screens.game.SnakeGameState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GameLogicTest {

    private val cols = 20
    private val rows = 20

    @Test
    fun generateFood_staysWithinBoardBounds() {
        val snake = listOf(Pair(5, 5), Pair(5, 6), Pair(5, 7))
        for (i in 0 until 50) {
            val food = generateFood(snake, cols, rows)
            assertTrue("Food X is within bounds", food.first in 0 until cols)
            assertTrue("Food Y is within bounds", food.second in 0 until rows)
        }
    }

    @Test
    fun generateFood_neverSpawnsOnSnakeBody() {
        val snake = listOf(
            Pair(5, 5),
            Pair(5, 6),
            Pair(5, 7),
            Pair(6, 7),
            Pair(7, 7),
        )
        for (i in 0 until 100) {
            val food = generateFood(snake, cols, rows)
            assertFalse("Food must never spawn on snake coordinates", food in snake)
        }
    }

    @Test
    fun moveSnake_movesRightCorrectly() {
        val initialSnake = listOf(Pair(5, 5), Pair(4, 5))
        val state = SnakeGameState(
            snake = initialSnake,
            food = Pair(15, 15),
            direction = Direction.RIGHT,
            isGameOver = false,
            score = 0,
        )

        val updated = moveSnake(state, cols, rows)

        assertEquals(Pair(6, 5), updated.snake.first())
        assertEquals(listOf(Pair(6, 5), Pair(5, 5)), updated.snake)
        assertFalse(updated.isGameOver)
        assertEquals(0, updated.score)
    }

    @Test
    fun moveSnake_movesLeftCorrectly() {
        val initialSnake = listOf(Pair(5, 5), Pair(6, 5))
        val state = SnakeGameState(
            snake = initialSnake,
            food = Pair(15, 15),
            direction = Direction.LEFT,
            isGameOver = false,
            score = 0,
        )

        val updated = moveSnake(state, cols, rows)

        assertEquals(Pair(4, 5), updated.snake.first())
        assertEquals(listOf(Pair(4, 5), Pair(5, 5)), updated.snake)
        assertFalse(updated.isGameOver)
    }

    @Test
    fun moveSnake_movesUpCorrectly() {
        val initialSnake = listOf(Pair(5, 5), Pair(5, 6))
        val state = SnakeGameState(
            snake = initialSnake,
            food = Pair(15, 15),
            direction = Direction.UP,
            isGameOver = false,
            score = 0,
        )

        val updated = moveSnake(state, cols, rows)

        assertEquals(Pair(5, 4), updated.snake.first())
        assertEquals(listOf(Pair(5, 4), Pair(5, 5)), updated.snake)
        assertFalse(updated.isGameOver)
    }

    @Test
    fun moveSnake_movesDownCorrectly() {
        val initialSnake = listOf(Pair(5, 5), Pair(5, 4))
        val state = SnakeGameState(
            snake = initialSnake,
            food = Pair(15, 15),
            direction = Direction.DOWN,
            isGameOver = false,
            score = 0,
        )

        val updated = moveSnake(state, cols, rows)

        assertEquals(Pair(5, 6), updated.snake.first())
        assertEquals(listOf(Pair(5, 6), Pair(5, 5)), updated.snake)
        assertFalse(updated.isGameOver)
    }

    @Test
    fun moveSnake_collidingWithLeftWall_triggersGameOver() {
        val state = SnakeGameState(
            snake = listOf(Pair(0, 5), Pair(1, 5)),
            food = Pair(10, 10),
            direction = Direction.LEFT,
            isGameOver = false,
        )

        val updated = moveSnake(state, cols, rows)
        assertTrue(updated.isGameOver)
    }

    @Test
    fun moveSnake_collidingWithRightWall_triggersGameOver() {
        val state = SnakeGameState(
            snake = listOf(Pair(cols - 1, 5), Pair(cols - 2, 5)),
            food = Pair(10, 10),
            direction = Direction.RIGHT,
            isGameOver = false,
        )

        val updated = moveSnake(state, cols, rows)
        assertTrue(updated.isGameOver)
    }

    @Test
    fun moveSnake_collidingWithTopWall_triggersGameOver() {
        val state = SnakeGameState(
            snake = listOf(Pair(5, 0), Pair(5, 1)),
            food = Pair(10, 10),
            direction = Direction.UP,
            isGameOver = false,
        )

        val updated = moveSnake(state, cols, rows)
        assertTrue(updated.isGameOver)
    }

    @Test
    fun moveSnake_collidingWithBottomWall_triggersGameOver() {
        val state = SnakeGameState(
            snake = listOf(Pair(5, rows - 1), Pair(5, rows - 2)),
            food = Pair(10, 10),
            direction = Direction.DOWN,
            isGameOver = false,
        )

        val updated = moveSnake(state, cols, rows)
        assertTrue(updated.isGameOver)
    }

    @Test
    fun moveSnake_collidingWithSelf_triggersGameOver() {
        // Snake forming a loop where moving UP will hit its own body
        // Head at (5, 5), body at (5, 6), (6, 6), (6, 4), (5, 4)
        val snake = listOf(
            Pair(5, 5),
            Pair(5, 6),
            Pair(6, 6),
            Pair(6, 4),
            Pair(5, 4),
        )
        val state = SnakeGameState(
            snake = snake,
            food = Pair(15, 15),
            direction = Direction.UP,
            isGameOver = false,
        )

        val updated = moveSnake(state, cols, rows)
        assertTrue(updated.isGameOver)
    }

    @Test
    fun moveSnake_eatingFood_increasesScoreAndSnakeLength() {
        val initialSnake = listOf(Pair(5, 5), Pair(4, 5))
        val foodPos = Pair(6, 5)
        val state = SnakeGameState(
            snake = initialSnake,
            food = foodPos,
            direction = Direction.RIGHT,
            isGameOver = false,
            score = 100,
        )

        val updated = moveSnake(state, cols, rows)

        assertFalse(updated.isGameOver)
        assertEquals(100 + GameUISettings.EAT_SCORE, updated.score)
        assertEquals(3, updated.snake.size)
        assertEquals(foodPos, updated.snake.first())
        assertEquals(initialSnake.first(), updated.snake[1])
        assertEquals(initialSnake[1], updated.snake[2])
        assertNotEquals(foodPos, updated.food)
    }

    @Test
    fun moveSnake_normalMovement_keepsScoreAndFoodSame() {
        val initialSnake = listOf(Pair(5, 5), Pair(4, 5))
        val foodPos = Pair(10, 10)
        val state = SnakeGameState(
            snake = initialSnake,
            food = foodPos,
            direction = Direction.RIGHT,
            isGameOver = false,
            score = 250,
        )

        val updated = moveSnake(state, cols, rows)

        assertFalse(updated.isGameOver)
        assertEquals(250, updated.score)
        assertEquals(2, updated.snake.size)
        assertEquals(foodPos, updated.food)
    }
}
