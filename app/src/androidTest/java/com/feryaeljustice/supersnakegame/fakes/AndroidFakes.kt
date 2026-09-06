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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AndroidFakeAuthRepository(
    var user: FirebaseUser? = null,
) : AuthRepository {
    override suspend fun requestGoogleIdToken(activityContext: Context): AuthResult =
        AuthResult.Success("fake_android_token")

    override suspend fun tryGetGoogleCredential(
        activityContext: Context,
        filterByAuthorized: Boolean,
    ): AuthResult? = AuthResult.Success("fake_android_token")

    override suspend fun firebaseSignIn(idToken: String): FirebaseUser? = user

    override fun getCurrentFirebaseAuthUser(): FirebaseUser? = user

    override suspend fun signOut(): Boolean {
        user = null
        return true
    }
}

class AndroidFakeRecordRepository(
    private val records: MutableMap<String, Int> = mutableMapOf(),
) : RecordRepository {
    override suspend fun getRecordForUser(userId: String): Int? = records[userId]

    override suspend fun saveIfHigher(userId: String, newScore: Int): Int {
        val current = records[userId] ?: 0
        val best = maxOf(current, newScore)
        records[userId] = best
        return best
    }
}

class AndroidFakeSettingsRepository(
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
}
