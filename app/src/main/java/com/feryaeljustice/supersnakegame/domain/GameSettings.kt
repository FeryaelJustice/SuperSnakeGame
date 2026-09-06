package com.feryaeljustice.supersnakegame.domain

enum class ThemeMode {
    SYSTEM,
    DARK,
    LIGHT,
}

enum class GameSpeed(val fps: Int, val label: String) {
    CHILL(7, "Chill (Lento)"),
    NORMAL(10, "Normal"),
    FAST(14, "Pro (Rápido)"),
}

data class GameSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val gameSpeed: GameSpeed = GameSpeed.NORMAL,
    val showGrid: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val soundEffectsVolume: Float = 0.8f,
    val soundEffectsEnabled: Boolean = true,
)
