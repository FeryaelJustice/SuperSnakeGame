package com.feryaeljustice.supersnakegame.fakes

import android.content.Context
import com.feryaeljustice.supersnakegame.domain.AuthResult
import com.feryaeljustice.supersnakegame.domain.GameSettings
import com.feryaeljustice.supersnakegame.domain.GameSpeed
import com.feryaeljustice.supersnakegame.domain.ThemeMode
import com.feryaeljustice.supersnakegame.domain.repository.AuthRepository
import com.feryaeljustice.supersnakegame.domain.repository.RecordRepository
import com.feryaeljustice.supersnakegame.domain.repository.SettingsRepository
import com.google.firebase.auth.FirebaseUser
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FakeAuthRepository(
    var initialUser: FirebaseUser? = null,
    var googleIdTokenResult: AuthResult = AuthResult.Success("fake_id_token"),
    var signInUserResult: FirebaseUser? = mockk<FirebaseUser>(relaxed = true),
) : AuthRepository {
    private var currentUser: FirebaseUser? = initialUser
    var signOutCalled: Boolean = false
        private set

    override suspend fun requestGoogleIdToken(activityContext: Context): AuthResult = googleIdTokenResult

    override suspend fun tryGetGoogleCredential(
        activityContext: Context,
        filterByAuthorized: Boolean,
    ): AuthResult? = googleIdTokenResult

    override suspend fun firebaseSignIn(idToken: String): FirebaseUser? {
        currentUser = signInUserResult
        return signInUserResult
    }

    override fun getCurrentFirebaseAuthUser(): FirebaseUser? = currentUser ?: initialUser

    override suspend fun signOut(): Boolean {
        signOutCalled = true
        currentUser = null
        return true
    }
}

class FakeRecordRepository(
    initialRecords: Map<String, Int> = emptyMap(),
) : RecordRepository {
    val records = initialRecords.toMutableMap()

    override suspend fun getRecordForUser(userId: String): Int? = records[userId]

    override suspend fun saveIfHigher(
        userId: String,
        newScore: Int,
    ): Int {
        val current = records[userId] ?: 0
        val best = maxOf(current, newScore)
        records[userId] = best
        return best
    }
}

class FakeSettingsRepository(
    initialSettings: GameSettings = GameSettings(),
) : SettingsRepository {
    private val _settingsFlow = MutableStateFlow(initialSettings)
    override val settingsFlow: StateFlow<GameSettings> = _settingsFlow.asStateFlow()

    override fun getSettings(): GameSettings = _settingsFlow.value

    override fun setThemeMode(mode: ThemeMode) {
        _settingsFlow.update { it.copy(themeMode = mode) }
    }

    override fun setGameSpeed(speed: GameSpeed) {
        _settingsFlow.update { it.copy(gameSpeed = speed) }
    }

    override fun setShowGrid(enabled: Boolean) {
        _settingsFlow.update { it.copy(showGrid = enabled) }
    }

    override fun setHapticsEnabled(enabled: Boolean) {
        _settingsFlow.update { it.copy(hapticsEnabled = enabled) }
    }

    override fun setSoundEffectsVolume(volume: Float) {
        _settingsFlow.update { it.copy(soundEffectsVolume = volume.coerceIn(0.0f, 1.0f)) }
    }

    override fun setSoundEffectsEnabled(enabled: Boolean) {
        _settingsFlow.update { it.copy(soundEffectsEnabled = enabled) }
    }
}
