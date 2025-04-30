package com.feryaeljustice.supersnakegame.domain

import android.content.IntentSender

sealed class AuthResult {
    data class Success(
        val idToken: String,
    ) : AuthResult()

    data class NeedsUi(
        val intentSender: IntentSender?,
    ) : AuthResult()

    data class Failure(
        val exception: Exception,
    ) : AuthResult()
}
