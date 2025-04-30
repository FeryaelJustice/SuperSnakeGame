package com.feryaeljustice.supersnakegame.ui.screens.game

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.feryaeljustice.supersnakegame.domain.Direction
import com.feryaeljustice.supersnakegame.domain.GameUISettings
import com.feryaeljustice.supersnakegame.ui.components.ButtonsDirectionController
import com.feryaeljustice.supersnakegame.ui.components.DirectionController
import com.feryaeljustice.supersnakegame.ui.components.SnakeGameCanvas
import com.feryaeljustice.supersnakegame.ui.navigation.GameScreenData
import com.feryaeljustice.supersnakegame.ui.theme.PurpleGrey80
import kotlinx.coroutines.delay
import kotlin.math.floor
import kotlin.math.min

@Composable
fun SnakeGameScreen(
    data: GameScreenData,
    navigateToMenu: () -> Unit,
    viewModel: SnakeGameViewModel = hiltViewModel<SnakeGameViewModel>(),
) {
    val gameState by viewModel.snakeState.collectAsState()
    val gameRunning by viewModel.gameRunning.collectAsState()
    val moveDelay by viewModel.moveDelayMs.collectAsState()

    val highestUserCore by viewModel.record.collectAsState()

    // Para medir en píxeles
    var measuredCols by remember { mutableStateOf(20) }
    var measuredRows by remember { mutableIntStateOf(20) }

    val focusRequester = remember { FocusRequester() }

    // 1) dummy state que cambia cada frame para forzar recomposición
    var frameTick by remember { mutableLongStateOf(0L) }

    // ① fijar velocidad de la serpiente (once)
    LaunchedEffect(Unit) {
        // fija la velocidad de la serpiente a 10 FPS
        val initialDelayMs = 1000L / GameUISettings.SNAKE_SPEED
        viewModel.setSpeed(initialDelayMs)
    }

    // 2) ticker de render a ~60 FPS
    LaunchedEffect(Unit) {
        while (true) {
            withFrameNanos { nano ->
                // no lo usamos, solo disparamos una recomposición por frame
                frameTick = nano
            }
        }
    }

    // 3) ticker de movimiento de la serpiente a velocidad variable (~20 FPS)
    LaunchedEffect(gameRunning, moveDelay) {
        while (gameRunning && !gameState.isGameOver) {
            delay(moveDelay)
            viewModel.moveSnakeTo()
        }
    }

    // Pantalla
    // Pantalla
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
        ) {
            // Puntaje
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = "Puntuación: ${gameState.score}",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(16.dp),
                )
                Text(
                    text = "Tu puntuación más alta: $highestUserCore",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Yellow,
                    modifier = Modifier.padding(16.dp),
                )
            }

            // Juego
            Box(
                modifier =
                    Modifier
                        .weight(1f) // 1) Permitir foco para teclado
                        .fillMaxWidth()
                        .background(PurpleGrey80)
                        .onSizeChanged { size ->
                            val cellW = size.width / 20f
                            val cellH = size.height / 20f

                            // Cubrimos toda la pantalla con 20×20:
                            var cols = 20
                            var rows = 20

                            // Celdas cuadradas al mínimo:
                            val cellSize = min(cellW, cellH)
                            cols = floor(size.width / cellSize).toInt()
                            rows = floor(size.height / cellSize).toInt()

                            measuredCols = cols
                            measuredRows = rows

                            viewModel.setGridSize(cols, rows)
                        }.focusRequester(focusRequester)
                        .focusable()
                        // 2) Capturar flechas y W/A/S/D
                        .onKeyEvent { event ->
                            if (event.type == KeyEventType.KeyDown) {
                                when (event.key) {
                                    Key.DirectionUp, Key.W -> {
                                        viewModel.setNewDirection(Direction.UP)
                                        true
                                    }

                                    Key.DirectionDown, Key.S -> {
                                        viewModel.setNewDirection(Direction.DOWN)
                                        true
                                    }

                                    Key.DirectionLeft, Key.A -> {
                                        viewModel.setNewDirection(Direction.LEFT)
                                        true
                                    }

                                    Key.DirectionRight, Key.D -> {
                                        viewModel.setNewDirection(Direction.RIGHT)
                                        true
                                    }

                                    else -> false
                                }
                            } else {
                                false
                            }
                        },
            ) {
                SnakeGameCanvas(
                    state = gameState,
                    cols = measuredCols,
                    rows = measuredRows,
                    frame = frameTick,
                )
                DirectionController { newDirection ->
                    viewModel.setNewDirection(newDirection = newDirection)
                }
                ButtonsDirectionController(
                    modifier =
                        Modifier
                            .align(Alignment.BottomCenter)
                            .padding(12.dp),
                ) { newDirect ->
                    viewModel.setNewDirection(newDirection = newDirect)
                }
            }

            // Game over acciones
            if (gameState.isGameOver) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .zIndex(100f),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Button(
                        onClick = {
                            viewModel.restartGame()
                        },
                        modifier = Modifier.padding(16.dp),
                    ) {
                        Text("Reiniciar Juego")
                    }

                    Button(
                        onClick = {
                            viewModel.signOut {
                                navigateToMenu()
                            }
                        },
                        modifier = Modifier.padding(16.dp),
                    ) { Text("Cerrar Sesión") }
                }
            }
        }
    }
}
