package com.feryaeljustice.supersnakegame.ui.navigation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
object MenuScreen

@Serializable
@Parcelize
data class GameScreen(
    val data: GameScreenData,
) : Parcelable

@Serializable
@Parcelize
data class GameScreenData(
    val gameId: String,
) : Parcelable
