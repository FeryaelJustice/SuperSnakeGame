# Authentication Flow Guide - Super Snake Game

This document details the authentication architecture implemented in Super Snake Game, utilizing modern Android Credential Manager and Firebase Authentication.

## 1. Overview

Super Snake Game delegates user identity to Google Sign-In backed by Firebase Authentication. This architecture provides several key advantages:

- **Security**: Eliminates the need to store passwords or manage user credentials locally.
- **Identity Consistency**: Guarantees a unique, permanent Firebase User ID (`uid`) across device reboots and app updates.
- **Data Association**: High scores in Cloud Firestore are tied directly to the authenticated user ID.

## 2. Authentication Flow Diagram

```
User Taps "Sign in with Google"
                │
                ▼
      MainMenuViewModel.onGoogleButtonClick()
                │
                ▼
      AuthRepository.requestGoogleIdToken()
                │
                ▼
   ┌──────────────────────────────────────────────┐
   │ Step 1: Filter by Authorized Accounts        │
   │ Checks for accounts already authorized.      │
   └──────────────────────┬───────────────────────┘
                          │ If null (no prior auth)
                          ▼
   ┌──────────────────────────────────────────────┐
   │ Step 2: Request Any Google Account           │
   │ Displays the Credential Manager Bottom Sheet │
   └──────────────────────┬───────────────────────┘
                          │
                          ▼
   Google Identity Returns GoogleIdTokenCredential
                          │
                          ▼
             Extracts raw idToken string
                          │
                          ▼
      AuthRepository.firebaseSignIn(idToken)
                          │
                          ▼
      Firebase exchanges token for FirebaseUser
                          │
                          ▼
           ViewModel emits UiState.SignedIn
                          │
                          ▼
            Navigate to SnakeGameScreen
```

## 3. Step-by-Step Technical Implementation

### A. Nonce Generation

To prevent replay attacks during authentication, `AuthRepositoryImpl` generates a cryptographically secure random 32-byte nonce converted into a hexadecimal string:

```kotlin
private fun generateNonce(): String {
    val bytes = ByteArray(NONCE_BYTES)
    java.security.SecureRandom().nextBytes(bytes)
    return bytes.joinToString("") { "%02x".format(it) }
}
```

### B. Credential Manager Request

The app constructs a `GetGoogleIdOption` targeting the server's Web Client ID (configured in `strings.xml` under `default_web_client_id`):

```kotlin
val googleIdOption = GetGoogleIdOption.Builder()
    .setServerClientId(webClientId)
    .setFilterByAuthorizedAccounts(filterByAuthorized)
    .setNonce(generateNonce())
    .build()

val request = GetCredentialRequest.Builder()
    .addCredentialOption(googleIdOption)
    .build()

val response = withContext(Dispatchers.IO) {
    credentialManager.getCredential(activityContext, request)
}
```

The app executes a two-tier strategy:
1. First, it attempts `filterByAuthorized = true` for silent or instant sign-in if the user previously authorized the app.
2. If null, it falls back to `filterByAuthorized = false`, prompting the user with the Google account chooser bottom sheet.

### C. Firebase Token Exchange

Once Credential Manager returns a valid `CustomCredential` of type `TYPE_GOOGLE_ID_TOKEN_CREDENTIAL`, the ID token is parsed via `GoogleIdTokenCredential.createFrom(cred.data)`.

The repository then passes the token to Firebase Auth:

```kotlin
firebaseAuth.signInWithCredential(
    GoogleAuthProvider.getCredential(idToken, null)
)
```

Upon successful task completion, Firebase returns the active `FirebaseUser`, which contains the unique identifier (`user.uid`) used for cloud high scores.

### D. Session Lifecycle and Auto-Login

- When `MainMenuViewModel` is initialized, it calls `authRepo.getCurrentFirebaseAuthUser()`.
- If Firebase Auth has an existing active session cached on disk, the ViewModel immediately transitions to `UiState.SignedIn(user)`.
- The UI observes this state and navigates directly to the game screen without prompting the user to sign in again.

### E. Sign-Out Process

When the user selects "Cerrar Sesión" on the Game Over screen:
1. `firebaseAuth.signOut()` is invoked to terminate the Firebase session.
2. `credentialManager.clearCredentialState(clearRequest)` is called to clear cached credential state in Google Play Services.
3. The app routes back to `MainMenuScreen`.
