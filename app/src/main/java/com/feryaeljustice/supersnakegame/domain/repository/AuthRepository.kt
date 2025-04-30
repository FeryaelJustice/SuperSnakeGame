package com.feryaeljustice.supersnakegame.domain.repository

import com.feryaeljustice.supersnakegame.domain.AuthResult
import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    suspend fun requestGoogleIdToken(): AuthResult

    suspend fun tryGetGoogleCredential(filterByAuthorized: Boolean): AuthResult?

    suspend fun firebaseSignIn(idToken: String): FirebaseUser?

    fun getCurrentFirebaseAuthUser(): FirebaseUser?

    suspend fun signOut(): Boolean
}
