package com.feryaeljustice.supersnakegame.ui.screens.menu

sealed class MainMenuUiEvent {
    data class ShowToast(
        val message: String,
    ) : MainMenuUiEvent()
}
