package com.feryaeljustice.supersnakegame.ui.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GoogleButtonTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun defaultState_displaysLoginTextAndHandlesClicks() {
        var clicked = false
        composeTestRule.setContent {
            GoogleButton(
                loading = false,
                onClicked = { clicked = true },
            )
        }

        composeTestRule.onNodeWithText("Iniciar sesión con Google")
            .assertIsDisplayed()
            .performClick()

        assertTrue(clicked)
    }

    @Test
    fun loadingState_displaysLoadingText() {
        composeTestRule.setContent {
            GoogleButton(
                loading = true,
                onClicked = {},
            )
        }

        composeTestRule.onNodeWithText("Iniciando sesión...")
            .assertIsDisplayed()
    }
}
