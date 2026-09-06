package com.feryaeljustice.supersnakegame.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.feryaeljustice.supersnakegame.domain.GameSettings
import com.feryaeljustice.supersnakegame.domain.GameSpeed
import com.feryaeljustice.supersnakegame.domain.ThemeMode
import com.feryaeljustice.supersnakegame.domain.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl
    @Inject
    constructor(
        @ApplicationContext context: Context,
    ) : SettingsRepository {
        private val prefs: SharedPreferences =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        private val _settingsFlow = MutableStateFlow(loadSettings())
        override val settingsFlow: StateFlow<GameSettings> = _settingsFlow.asStateFlow()

        override fun getSettings(): GameSettings = _settingsFlow.value

        override fun setThemeMode(mode: ThemeMode) {
            prefs.edit().putString(KEY_THEME_MODE, mode.name).apply()
            _settingsFlow.update { it.copy(themeMode = mode) }
        }

        override fun setGameSpeed(speed: GameSpeed) {
            prefs.edit().putString(KEY_GAME_SPEED, speed.name).apply()
            _settingsFlow.update { it.copy(gameSpeed = speed) }
        }

        override fun setShowGrid(enabled: Boolean) {
            prefs.edit().putBoolean(KEY_SHOW_GRID, enabled).apply()
            _settingsFlow.update { it.copy(showGrid = enabled) }
        }

        override fun setHapticsEnabled(enabled: Boolean) {
            prefs.edit().putBoolean(KEY_HAPTICS_ENABLED, enabled).apply()
            _settingsFlow.update { it.copy(hapticsEnabled = enabled) }
        }

        private fun loadSettings(): GameSettings {
            val themeStr = prefs.getString(KEY_THEME_MODE, ThemeMode.SYSTEM.name)
            val themeMode =
                try {
                    ThemeMode.valueOf(themeStr ?: ThemeMode.SYSTEM.name)
                } catch (_: Exception) {
                    ThemeMode.SYSTEM
                }

            val speedStr = prefs.getString(KEY_GAME_SPEED, GameSpeed.NORMAL.name)
            val gameSpeed =
                try {
                    GameSpeed.valueOf(speedStr ?: GameSpeed.NORMAL.name)
                } catch (_: Exception) {
                    GameSpeed.NORMAL
                }

            val showGrid = prefs.getBoolean(KEY_SHOW_GRID, true)
            val haptics = prefs.getBoolean(KEY_HAPTICS_ENABLED, true)

            return GameSettings(
                themeMode = themeMode,
                gameSpeed = gameSpeed,
                showGrid = showGrid,
                hapticsEnabled = haptics,
            )
        }

        companion object {
            private const val PREFS_NAME = "super_snake_settings_prefs"
            private const val KEY_THEME_MODE = "theme_mode"
            private const val KEY_GAME_SPEED = "game_speed"
            private const val KEY_SHOW_GRID = "show_grid"
            private const val KEY_HAPTICS_ENABLED = "haptics_enabled"
        }
    }
