package com.feryaeljustice.supersnakegame.domain

import com.feryaeljustice.supersnakegame.ui.screens.game.SnakeGameState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SnakeGameStateTest {

    @Test
    fun defaultState_hasDefaultValues() {
        val state = SnakeGameState()
        assertEquals(listOf(Pair(5, 5)), state.snake)
        assertEquals(Pair(10, 10), state.food)
        assertEquals(Direction.RIGHT, state.direction)
        assertFalse(state.isGameOver)
        assertEquals(0, state.score)
    }

    @Test
    fun initialFactory_placesSnakeAtGridCenter() {
        val cols = 24
        val rows = 30
        val state = SnakeGameState.initial(cols, rows)

        assertEquals(1, state.snake.size)
        assertEquals(Pair(cols / 2, rows / 2), state.snake.first())
        assertFalse(state.isGameOver)
        assertEquals(0, state.score)
        assertNotEquals(state.snake.first(), state.food)
        assertTrue(state.food.first in 0 until cols)
        assertTrue(state.food.second in 0 until rows)
        assertTrue(state.direction in Direction.entries)
    }
}
