package com.feryaeljustice.supersnakegame.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.feryaeljustice.supersnakegame.data.repository.SettingsRepositoryImpl
import com.feryaeljustice.supersnakegame.domain.GameSpeed
import com.feryaeljustice.supersnakegame.domain.ThemeMode
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsRepositoryImplTest {

    private lateinit var context: Context
    private lateinit var repository: SettingsRepositoryImpl

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        clearPrefs()
        repository = SettingsRepositoryImpl(context)
    }

    @After
    fun tearDown() {
        clearPrefs()
    }

    private fun clearPrefs() {
        context.getSharedPreferences("super_snake_settings_prefs", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
    }

    @Test
    fun defaultSettings_returnsSystemNormalGridAndHaptics() {
        val settings = repository.getSettings()
        assertEquals(ThemeMode.SYSTEM, settings.themeMode)
        assertEquals(GameSpeed.NORMAL, settings.gameSpeed)
        assertTrue(settings.showGrid)
        assertTrue(settings.hapticsEnabled)
    }

    @Test
    fun setThemeMode_persistsAndUpdatesFlow() {
        repository.setThemeMode(ThemeMode.DARK)
        assertEquals(ThemeMode.DARK, repository.getSettings().themeMode)
        assertEquals(ThemeMode.DARK, repository.settingsFlow.value.themeMode)

        // Verify across newly instantiated repository
        val newRepoInstance = SettingsRepositoryImpl(context)
        assertEquals(ThemeMode.DARK, newRepoInstance.getSettings().themeMode)
    }

    @Test
    fun setGameSpeed_persistsAndUpdatesFlow() {
        repository.setGameSpeed(GameSpeed.FAST)
        assertEquals(GameSpeed.FAST, repository.getSettings().gameSpeed)
        assertEquals(GameSpeed.FAST, repository.settingsFlow.value.gameSpeed)

        val newRepoInstance = SettingsRepositoryImpl(context)
        assertEquals(GameSpeed.FAST, newRepoInstance.getSettings().gameSpeed)
    }

    @Test
    fun setShowGrid_persistsAndUpdatesFlow() {
        repository.setShowGrid(false)
        assertFalse(repository.getSettings().showGrid)
        assertFalse(repository.settingsFlow.value.showGrid)

        val newRepoInstance = SettingsRepositoryImpl(context)
        assertFalse(newRepoInstance.getSettings().showGrid)
    }

    @Test
    fun setHapticsEnabled_persistsAndUpdatesFlow() {
        repository.setHapticsEnabled(false)
        assertFalse(repository.getSettings().hapticsEnabled)
        assertFalse(repository.settingsFlow.value.hapticsEnabled)

        val newRepoInstance = SettingsRepositoryImpl(context)
        assertFalse(newRepoInstance.getSettings().hapticsEnabled)
    }

    @Test
    fun corruptedPrefs_fallbacksToSafeDefaults() {
        context.getSharedPreferences("super_snake_settings_prefs", Context.MODE_PRIVATE)
            .edit()
            .putString("theme_mode", "INVALID_THEME")
            .putString("game_speed", "UNKNOWN_SPEED")
            .commit()

        val repo = SettingsRepositoryImpl(context)
        assertEquals(ThemeMode.SYSTEM, repo.getSettings().themeMode)
        assertEquals(GameSpeed.NORMAL, repo.getSettings().gameSpeed)
    }
}
