package com.feryaeljustice.supersnakegame.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.feryaeljustice.supersnakegame.fakes.AndroidFakeAuthRepository
import com.feryaeljustice.supersnakegame.fakes.AndroidFakeSettingsRepository
import com.feryaeljustice.supersnakegame.ui.screens.menu.MainMenuScreen
import com.feryaeljustice.supersnakegame.ui.screens.menu.MainMenuViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainMenuScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun displaysArcadeBrandingAndButtons() {
        val fakeAuth = AndroidFakeAuthRepository(user = null)
        val fakeSettings = AndroidFakeSettingsRepository()
        val viewModel = MainMenuViewModel(
            authRepo = fakeAuth,
            settingsRepo = fakeSettings,
        )

        composeTestRule.setContent {
            MainMenuScreen(
                viewModel = viewModel,
                navigateToGameScreen = {},
            )
        }

        composeTestRule.onNodeWithText("SUPER SNAKE GAME").assertIsDisplayed()
        composeTestRule.onNodeWithText("Desarrollado por Feryael Justice").assertIsDisplayed()
        composeTestRule.onNodeWithText("ARCADE RETRO EDITION").assertIsDisplayed()
        composeTestRule.onNodeWithText("¡Comienza la partida!").assertIsDisplayed()
        composeTestRule.onNodeWithText("Iniciar sesión con Google").assertIsDisplayed()
        composeTestRule.onNodeWithText("60 FPS").assertIsDisplayed()
        composeTestRule.onNodeWithText("Nube").assertIsDisplayed()
        composeTestRule.onNodeWithText("D-Pad").assertIsDisplayed()
    }
}
