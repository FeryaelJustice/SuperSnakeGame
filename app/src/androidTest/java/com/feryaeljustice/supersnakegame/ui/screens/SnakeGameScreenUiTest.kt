package com.feryaeljustice.supersnakegame.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.feryaeljustice.supersnakegame.domain.usecase.GetHighScoreUseCase
import com.feryaeljustice.supersnakegame.domain.usecase.SaveHighScoreUseCase
import com.feryaeljustice.supersnakegame.fakes.AndroidFakeAuthRepository
import com.feryaeljustice.supersnakegame.fakes.AndroidFakeRecordRepository
import com.feryaeljustice.supersnakegame.fakes.AndroidFakeSettingsRepository
import com.feryaeljustice.supersnakegame.ui.screens.game.SnakeGameScreen
import com.feryaeljustice.supersnakegame.ui.screens.game.SnakeGameViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SnakeGameScreenUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun createViewModel(): SnakeGameViewModel {
        val fakeAuth = AndroidFakeAuthRepository(user = null)
        val fakeRecord = AndroidFakeRecordRepository(mutableMapOf("user_1" to 420))
        val fakeSettings = AndroidFakeSettingsRepository()
        return SnakeGameViewModel(
            authRepo = fakeAuth,
            getHighScore = GetHighScoreUseCase(fakeRecord),
            saveHighScore = SaveHighScoreUseCase(fakeRecord),
            settingsRepo = fakeSettings,
        )
    }

    @Test
    fun displaysHudScoreRecordAndControls() {
        val viewModel = createViewModel()
        composeTestRule.setContent {
            SnakeGameScreen(
                navigateToMenu = {},
                viewModel = viewModel,
            )
        }

        composeTestRule.onNodeWithText("PTS").assertIsDisplayed()
        composeTestRule.onNodeWithText("RÉCORD: 0").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Pausar").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Ajustes").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Up").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Down").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Left").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Right").assertIsDisplayed()
    }

    @Test
    fun clickingPauseButton_togglesToResume() {
        val viewModel = createViewModel()
        composeTestRule.setContent {
            SnakeGameScreen(
                navigateToMenu = {},
                viewModel = viewModel,
            )
        }

        // Initially game is running, so button is "Pausar"
        composeTestRule.onNodeWithContentDescription("Pausar").performClick()

        // After clicking pause, button changes to "Reanudar"
        composeTestRule.onNodeWithContentDescription("Reanudar").assertIsDisplayed()

        // Clicking resume changes it back to "Pausar"
        composeTestRule.onNodeWithContentDescription("Reanudar").performClick()
        composeTestRule.onNodeWithContentDescription("Pausar").assertIsDisplayed()
    }

    @Test
    fun clickingSettingsButton_opensSettingsSheet() {
        val viewModel = createViewModel()
        composeTestRule.setContent {
            SnakeGameScreen(
                navigateToMenu = {},
                viewModel = viewModel,
            )
        }

        composeTestRule.onNodeWithContentDescription("Ajustes").performClick()
        composeTestRule.onNodeWithText("Ajustes y Opciones").assertIsDisplayed()
    }
}
