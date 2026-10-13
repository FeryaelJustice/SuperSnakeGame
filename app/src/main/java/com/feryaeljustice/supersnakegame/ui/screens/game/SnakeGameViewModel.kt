package com.feryaeljustice.supersnakegame.ui.screens.game

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.feryaeljustice.supersnakegame.domain.Direction
import com.feryaeljustice.supersnakegame.domain.GameSpeed
import com.feryaeljustice.supersnakegame.domain.ThemeMode
import com.feryaeljustice.supersnakegame.domain.moveSnake
import com.feryaeljustice.supersnakegame.domain.repository.AuthRepository
import com.feryaeljustice.supersnakegame.domain.repository.SettingsRepository
import com.feryaeljustice.supersnakegame.domain.usecase.GetHighScoreUseCase
import com.feryaeljustice.supersnakegame.domain.usecase.SaveHighScoreUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import com.feryaeljustice.supersnakegame.domain.usecase.ResetLocalRecordUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SnakeGameViewModel
    @Inject
    constructor(
        private val authRepo: AuthRepository,
        private val getHighScore: GetHighScoreUseCase,
        private val saveHighScore: SaveHighScoreUseCase,
        private val resetLocalRecordUseCase: ResetLocalRecordUseCase,
        private val settingsRepo: SettingsRepository,
        savedStateHandle: SavedStateHandle = SavedStateHandle(),
    ) : ViewModel() {
        val isOffline: Boolean = savedStateHandle.get<Boolean>("isOffline") ?: false
        val isOfflineMode: Boolean
            get() = isOffline || authRepo.getCurrentFirebaseAuthUser() == null

        private var cols = 20
        private var rows = 20

        private val _snakeState = MutableStateFlow(SnakeGameState())
        val snakeState = _snakeState.asStateFlow()

        private val _gameRunning = MutableStateFlow(true)
        val gameRunning = _gameRunning.asStateFlow()

        private val _isPaused = MutableStateFlow(false)
        val isPaused = _isPaused.asStateFlow()

        val settingsFlow = settingsRepo.settingsFlow

        // Velocidad en milisegundos del snake
        private val _moveDelayMs = MutableStateFlow(100L)
        val moveDelayMs = _moveDelayMs.asStateFlow()

        // Maxima puntuacion del usuario
        private val _record = MutableStateFlow(0)
        val record = _record.asStateFlow()

        init {
            viewModelScope.launch {
                val userId = if (isOfflineMode) null else authRepo.getCurrentFirebaseAuthUser()?.uid
                _record.value = getHighScore(userId)
            }

            viewModelScope.launch {
                settingsRepo.settingsFlow.collect { settings ->
                    val delay = 1000L / settings.gameSpeed.fps
                    _moveDelayMs.value = delay.coerceAtLeast(1L)
                }
            }
        }

        fun pauseGame() {
            _isPaused.value = true
        }

        fun resumeGame() {
            _isPaused.value = false
        }

        fun setThemeMode(mode: ThemeMode) = settingsRepo.setThemeMode(mode)

        fun setGameSpeed(speed: GameSpeed) = settingsRepo.setGameSpeed(speed)

        fun setShowGrid(enabled: Boolean) = settingsRepo.setShowGrid(enabled)

        fun setHapticsEnabled(enabled: Boolean) = settingsRepo.setHapticsEnabled(enabled)

        fun setSoundEffectsVolume(volume: Float) = settingsRepo.setSoundEffectsVolume(volume)

        fun setSoundEffectsEnabled(enabled: Boolean) = settingsRepo.setSoundEffectsEnabled(enabled)

        fun setSpeed(millis: Long) {
            _moveDelayMs.value = millis.coerceAtLeast(1L)
        }

        /** Llamar desde Composable cuando midas cols/rows */
        fun setGridSize(
            newCols: Int,
            newRows: Int,
        ) {
            cols = newCols
            rows = newRows
        }

        fun moveSnakeTo(): Boolean {
            if (_isPaused.value || !_gameRunning.value || _snakeState.value.isGameOver) {
                return false
            }

            val currentScore = _snakeState.value.score
            val updated = moveSnake(_snakeState.value, cols, rows)
            _snakeState.value = updated

            val ateFood = updated.score > currentScore

            // Fin del juego
            if (updated.isGameOver) {
                _gameRunning.value = false

                // Actualizar puntuacion maxima
                viewModelScope.launch {
                    val userId = if (isOfflineMode) null else authRepo.getCurrentFirebaseAuthUser()?.uid
                    val newRecord = saveHighScore(userId, _snakeState.value.score)
                    _record.value = newRecord
                }
            }
            return ateFood
        }

        fun setNewDirection(newDirection: Direction) {
            // prevent 180° turns:
            val opposite =
                when (_snakeState.value.direction) {
                    Direction.UP -> Direction.DOWN
                    Direction.DOWN -> Direction.UP
                    Direction.LEFT -> Direction.RIGHT
                    Direction.RIGHT -> Direction.LEFT
                }
            if (newDirection != opposite) {
                _snakeState.update { it.copy(direction = newDirection) }
            }
        }

        fun restartGame() {
            _snakeState.value = SnakeGameState.initial(cols, rows)
            _isPaused.value = false
            _gameRunning.value = true
        }

        fun signOut(onFinish: () -> Unit) {
            viewModelScope.launch {
                if (!isOfflineMode) {
                    authRepo.signOut()
                }
                onFinish()
            }
        }

        fun resetLocalRecord() {
            viewModelScope.launch {
                resetLocalRecordUseCase()
                _record.value = 0
            }
        }
    }
