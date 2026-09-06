package com.feryaeljustice.supersnakegame.data.repository

import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.feryaeljustice.supersnakegame.domain.AuthResult
import com.feryaeljustice.supersnakegame.domain.repository.AuthRepository
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

class AuthRepositoryImpl
    @Inject
    constructor(
        private val credentialManager: CredentialManager,
        private val firebaseAuth: FirebaseAuth,
        @param:Named("webClientId") private val webClientId: String,
    ) : AuthRepository {
        companion object {
            const val NONCE_BYTES = 32
        }

        override suspend fun requestGoogleIdToken(activityContext: Context): AuthResult =
            tryGetGoogleCredential(activityContext, filterByAuthorized = true)
                ?: tryGetGoogleCredential(activityContext, filterByAuthorized = false)
                ?: AuthResult.Failure(IllegalStateException("No valid Google credential found"))

        private fun generateNonce(): String {
            val bytes = ByteArray(NONCE_BYTES)
            java.security.SecureRandom().nextBytes(bytes)
            return bytes.joinToString("") { "%02x".format(it) }
        }

        @Suppress("TooGenericExceptionCaught")
        override suspend fun tryGetGoogleCredential(
            activityContext: Context,
            filterByAuthorized: Boolean,
        ): AuthResult? =
            try {
                // 1) SIWG
                val googleIdOption =
                    GetGoogleIdOption
                        .Builder()
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(webClientId)
                        // Can cause the error of "Invalid Credentials"
                        .setFilterByAuthorizedAccounts(filterByAuthorized)
                        .setNonce(generateNonce())
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
                        credentialManager.getCredential(activityContext, request)
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
            } catch (e: kotlinx.coroutines.CancellationException) {
                throw e
            } catch (e: NoCredentialException) {
                Log.d("Auth", "No credentials available: ${e.message}")
                null
            } catch (e: GetCredentialCancellationException) {
                Log.d("Auth", "User cancelled credential picker: ${e.message}")
                null
            } catch (e: GetCredentialException) {
                Log.w("Auth", "GetCredentialException: ${e.message}", e)
                null
            } catch (e: Exception) {
                Log.w("Auth", "GetCredentialException: ${e.message}", e)
                null
            }

        override suspend fun firebaseSignIn(idToken: String): FirebaseUser? =
            withContext(Dispatchers.IO) {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val result = firebaseAuth.signInWithCredential(credential).await()
                result.user
            }

        override fun getCurrentFirebaseAuthUser(): FirebaseUser? = firebaseAuth.currentUser

        @Suppress("TooGenericExceptionCaught")
        override suspend fun signOut(): Boolean =
            try {
                firebaseAuth.signOut()
                val clearRequest = ClearCredentialStateRequest()
                credentialManager.clearCredentialState(clearRequest)
                true
            } catch (e: kotlinx.coroutines.CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.w("signOut", "signOut exception: ${e.message}", e)
                false
            }
    }
