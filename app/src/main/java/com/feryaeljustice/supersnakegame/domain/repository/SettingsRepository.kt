package com.feryaeljustice.supersnakegame.domain.repository

import com.feryaeljustice.supersnakegame.domain.GameSettings
import com.feryaeljustice.supersnakegame.domain.GameSpeed
import com.feryaeljustice.supersnakegame.domain.ThemeMode
import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {
    val settingsFlow: StateFlow<GameSettings>
    fun getSettings(): GameSettings
    fun setThemeMode(mode: ThemeMode)
    fun setGameSpeed(speed: GameSpeed)
    fun setShowGrid(enabled: Boolean)
    fun setHapticsEnabled(enabled: Boolean)
    fun setSoundEffectsVolume(volume: Float)
    fun setSoundEffectsEnabled(enabled: Boolean)
}
