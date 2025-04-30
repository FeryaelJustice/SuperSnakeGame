package com.feryaeljustice.supersnakegame.data.repository

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.feryaeljustice.supersnakegame.domain.AuthResult
import com.feryaeljustice.supersnakegame.domain.repository.AuthRepository
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AuthRepositoryImpl
    @Inject
    constructor(
        @ApplicationContext private val ctx: Context,
        private val credentialManager: CredentialManager,
        private val firebaseAuth: FirebaseAuth,
        @Named("webClientId") private val webClientId: String,
    ) : AuthRepository {
        @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
        override suspend fun requestGoogleIdToken(): AuthResult =
            tryGetGoogleCredential(filterByAuthorized = true)
                ?: tryGetGoogleCredential(filterByAuthorized = false)
                ?: AuthResult.Failure(IllegalStateException("No valid Google credential found"))

        @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
        @Suppress("TooGenericExceptionCaught")
        override suspend fun tryGetGoogleCredential(filterByAuthorized: Boolean): AuthResult? =
            try {
                // 1) SIWG
                val googleIdOption =
                    GetGoogleIdOption
                        .Builder()
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(webClientId)
                        // Can cause the error of "Invalid Credentials"
                        .setFilterByAuthorizedAccounts(filterByAuthorized)
                        .build()

                // 2) Petición de credenciales
                val request =
                    GetCredentialRequest
                        .Builder()
                        .addCredentialOption(googleIdOption)
                        .build()

                // 3) Llamada SÍNCRONA en IO (no devuelve Task, por tanto no hay await)
                val response =
                    withContext(Dispatchers.IO) {
                        credentialManager.getCredential(ctx, request)
                    }

                // 4) Extrae el credential y castealo al tipo correcto
                val cred = response.credential
                if (cred is CustomCredential && cred.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    // Create Google ID Token
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(cred.data)
                    AuthResult.Success(googleIdTokenCredential.idToken)
                } else {
                    null
                }
            } catch (e: Exception) {
                Log.w("Auth", "GetCredentialException: ${e.message}", e)
                null
            }

        override suspend fun firebaseSignIn(idToken: String): FirebaseUser? =
            suspendCancellableCoroutine { cancellableContinuation ->
                firebaseAuth
                    .signInWithCredential(
                        GoogleAuthProvider.getCredential(idToken, null),
                    ).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = firebaseAuth.currentUser
                            cancellableContinuation.resume(user)
                        } else {
                            cancellableContinuation.resumeWithException(task.exception!!)
                        }
                    }.addOnFailureListener {
                        cancellableContinuation.resumeWithException(it)
                    }
            }

        override fun getCurrentFirebaseAuthUser(): FirebaseUser? = firebaseAuth.currentUser

        @Suppress("TooGenericExceptionCaught")
        override suspend fun signOut(): Boolean =
            try {
                firebaseAuth.signOut()
                val clearRequest = ClearCredentialStateRequest()
                credentialManager.clearCredentialState(clearRequest)
                true
            } catch (e: Exception) {
                Log.w("signOut", "signOut exception: ${e.message}", e)
                false
            }
    }
