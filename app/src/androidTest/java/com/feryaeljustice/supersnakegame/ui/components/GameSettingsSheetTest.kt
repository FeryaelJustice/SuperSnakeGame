package com.feryaeljustice.supersnakegame.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.feryaeljustice.supersnakegame.domain.GameSettings
import com.feryaeljustice.supersnakegame.domain.GameSpeed
import com.feryaeljustice.supersnakegame.domain.ThemeMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalMaterial3Api::class)
@RunWith(AndroidJUnit4::class)
class GameSettingsSheetTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun displaysSettingsTitleAndOptions() {
        val settings = GameSettings()
        composeTestRule.setContent {
            GameSettingsSheet(
                settings = settings,
                onThemeChanged = {},
                onSpeedChanged = {},
                onGridChanged = {},
                onHapticsChanged = {},
                onDismissRequest = {},
            )
        }

        composeTestRule.onNodeWithText("Ajustes y Opciones").assertIsDisplayed()
        composeTestRule.onNodeWithText("Tema de la aplicación").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sistema").assertIsDisplayed()
        composeTestRule.onNodeWithText("Oscuro").assertIsDisplayed()
        composeTestRule.onNodeWithText("Claro").assertIsDisplayed()
        composeTestRule.onNodeWithText("Dificultad y Velocidad").assertIsDisplayed()
        composeTestRule.onNodeWithText("Normal").assertIsDisplayed()
    }

    @Test
    fun clickingThemeChip_triggersCallback() {
        var selectedTheme: ThemeMode? = null
        val settings = GameSettings()
        composeTestRule.setContent {
            GameSettingsSheet(
                settings = settings,
                onThemeChanged = { selectedTheme = it },
                onSpeedChanged = {},
                onGridChanged = {},
                onHapticsChanged = {},
                onDismissRequest = {},
            )
        }

        composeTestRule.onNodeWithText("Oscuro").performClick()
        assertEquals(ThemeMode.DARK, selectedTheme)
    }

    @Test
    fun clickingSpeedChip_triggersCallback() {
        var selectedSpeed: GameSpeed? = null
        val settings = GameSettings()
        composeTestRule.setContent {
            GameSettingsSheet(
                settings = settings,
                onThemeChanged = {},
                onSpeedChanged = { selectedSpeed = it },
                onGridChanged = {},
                onHapticsChanged = {},
                onDismissRequest = {},
            )
        }

        composeTestRule.onNodeWithText(GameSpeed.FAST.label).performClick()
        assertEquals(GameSpeed.FAST, selectedSpeed)
    }

    @Test
    fun clickingCloseButton_triggersDismiss() {
        var dismissed = false
        val settings = GameSettings()
        composeTestRule.setContent {
            GameSettingsSheet(
                settings = settings,
                onThemeChanged = {},
                onSpeedChanged = {},
                onGridChanged = {},
                onHapticsChanged = {},
                onDismissRequest = { dismissed = true },
            )
        }

        composeTestRule.onNodeWithContentDescription("Cerrar").performClick()
        assertTrue(dismissed)
    }

    @Test
    fun displaysSoundEffectsControls() {
        val settings = GameSettings(soundEffectsEnabled = true, soundEffectsVolume = 0.8f)
        composeTestRule.setContent {
            GameSettingsSheet(
                settings = settings,
                onThemeChanged = {},
                onSpeedChanged = {},
                onGridChanged = {},
                onHapticsChanged = {},
                onSoundEffectsVolumeChanged = {},
                onSoundEffectsEnabledChanged = {},
                onDismissRequest = {},
            )
        }

        composeTestRule.onNodeWithText("Efectos de sonido").assertIsDisplayed()
        composeTestRule.onNodeWithText("Volumen de efectos").assertIsDisplayed()
        composeTestRule.onNodeWithText("80%").assertIsDisplayed()
    }
}
