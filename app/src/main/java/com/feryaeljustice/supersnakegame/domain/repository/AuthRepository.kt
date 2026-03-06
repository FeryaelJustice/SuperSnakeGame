package com.feryaeljustice.supersnakegame.domain.repository

import android.content.Context
import com.feryaeljustice.supersnakegame.domain.AuthResult
import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    suspend fun requestGoogleIdToken(activityContext: Context): AuthResult

    suspend fun tryGetGoogleCredential(
        activityContext: Context,
        filterByAuthorized: Boolean,
    ): AuthResult?

    suspend fun firebaseSignIn(idToken: String): FirebaseUser?

    fun getCurrentFirebaseAuthUser(): FirebaseUser?

    suspend fun signOut(): Boolean
}
