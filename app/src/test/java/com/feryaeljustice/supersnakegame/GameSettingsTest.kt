package com.feryaeljustice.supersnakegame

import com.feryaeljustice.supersnakegame.domain.GameSettings
import com.feryaeljustice.supersnakegame.domain.GameSpeed
import com.feryaeljustice.supersnakegame.domain.ThemeMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GameSettingsTest {
    @Test
    fun defaultSettings_hasExpectedValues() {
        val settings = GameSettings()
        assertEquals(ThemeMode.SYSTEM, settings.themeMode)
        assertEquals(GameSpeed.NORMAL, settings.gameSpeed)
        assertTrue(settings.showGrid)
        assertTrue(settings.hapticsEnabled)
    }

    @Test
    fun gameSpeedFps_producesSensibleDelays() {
        assertEquals(7, GameSpeed.CHILL.fps)
        assertEquals(10, GameSpeed.NORMAL.fps)
        assertEquals(14, GameSpeed.FAST.fps)

        val chillDelay = 1000L / GameSpeed.CHILL.fps
        val normalDelay = 1000L / GameSpeed.NORMAL.fps
        val fastDelay = 1000L / GameSpeed.FAST.fps

        assertTrue(chillDelay > normalDelay)
        assertTrue(normalDelay > fastDelay)
    }
}
