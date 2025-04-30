package com.feryaeljustice.supersnakegame.ui.screens.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.feryaeljustice.supersnakegame.domain.Direction
import com.feryaeljustice.supersnakegame.domain.moveSnake
import com.feryaeljustice.supersnakegame.domain.repository.AuthRepository
import com.feryaeljustice.supersnakegame.domain.usecase.GetHighScoreUseCase
import com.feryaeljustice.supersnakegame.domain.usecase.SaveHighScoreUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
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
    ) : ViewModel() {
        private var cols = 20
        private var rows = 20

        private val _snakeState = MutableStateFlow(SnakeGameState())
        val snakeState = _snakeState.asStateFlow()

        private val _gameRunning = MutableStateFlow(true)
        val gameRunning = _gameRunning.asStateFlow()

        // --- Velocidad en milisegundos del snake---
        private val _moveDelayMs = MutableStateFlow(200L)
        val moveDelayMs = _moveDelayMs.asStateFlow()

        // Maxima puntuacion del usuario
        private val _record = MutableStateFlow(0)
        val record = _record.asStateFlow()

        init {
            viewModelScope.launch {
                authRepo.getCurrentFirebaseAuthUser()?.let { user ->
                    _record.value = getHighScore(user.uid)
                }
            }
        }

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

        fun moveSnakeTo() {
            val updated = moveSnake(_snakeState.value, cols, rows)
            _snakeState.value = updated

            // Fin del juego
            if (updated.isGameOver) {
                _gameRunning.value = false

                // Actualizar puntuacion maxima
                viewModelScope.launch {
                    authRepo.getCurrentFirebaseAuthUser()?.let { user ->
                        val newRecord = saveHighScore(user.uid, _snakeState.value.score)
                        _record.value = newRecord
                    }
                }
            }
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
            _gameRunning.value = true
        }

        fun signOut(onFinish: () -> Unit) {
            viewModelScope.launch {
                authRepo.signOut()
                onFinish()
            }
        }
    }
