package com.feryaeljustice.supersnakegame.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.feryaeljustice.supersnakegame.domain.Direction
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ButtonsDirectionControllerTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun clickingUpButton_triggersDirectionUp() {
        var lastDirection: Direction? = null
        composeTestRule.setContent {
            ButtonsDirectionController(
                modifier = Modifier,
                onDirectionChange = { lastDirection = it },
            )
        }

        composeTestRule.onNodeWithContentDescription("Up").performClick()
        assertEquals(Direction.UP, lastDirection)
    }

    @Test
    fun clickingDownButton_triggersDirectionDown() {
        var lastDirection: Direction? = null
        composeTestRule.setContent {
            ButtonsDirectionController(
                modifier = Modifier,
                onDirectionChange = { lastDirection = it },
            )
        }

        composeTestRule.onNodeWithContentDescription("Down").performClick()
        assertEquals(Direction.DOWN, lastDirection)
    }

    @Test
    fun clickingLeftButton_triggersDirectionLeft() {
        var lastDirection: Direction? = null
        composeTestRule.setContent {
            ButtonsDirectionController(
                modifier = Modifier,
                onDirectionChange = { lastDirection = it },
            )
        }

        composeTestRule.onNodeWithContentDescription("Left").performClick()
        assertEquals(Direction.LEFT, lastDirection)
    }

    @Test
    fun clickingRightButton_triggersDirectionRight() {
        var lastDirection: Direction? = null
        composeTestRule.setContent {
            ButtonsDirectionController(
                modifier = Modifier,
                onDirectionChange = { lastDirection = it },
            )
        }

        composeTestRule.onNodeWithContentDescription("Right").performClick()
        assertEquals(Direction.RIGHT, lastDirection)
    }
}
