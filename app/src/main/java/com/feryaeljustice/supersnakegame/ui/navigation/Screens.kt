package com.feryaeljustice.supersnakegame.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
object MenuScreen

@Serializable
data class GameScreen(
    val isOffline: Boolean = false,
)