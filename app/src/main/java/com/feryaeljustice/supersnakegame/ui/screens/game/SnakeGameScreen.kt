package com.feryaeljustice.supersnakegame.ui.screens.game

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.feryaeljustice.supersnakegame.data.audio.SoundEffectManager
import com.feryaeljustice.supersnakegame.domain.Direction
import com.feryaeljustice.supersnakegame.ui.components.ButtonsDirectionController
import com.feryaeljustice.supersnakegame.ui.components.DirectionController
import com.feryaeljustice.supersnakegame.ui.components.GameSettingsSheet
import com.feryaeljustice.supersnakegame.ui.components.SnakeGameCanvas
import com.feryaeljustice.supersnakegame.ui.navigation.GameScreenData
import com.feryaeljustice.supersnakegame.ui.theme.ArcadeBoardBg
import com.feryaeljustice.supersnakegame.ui.theme.ArcadeBorder
import com.feryaeljustice.supersnakegame.ui.theme.ArcadeDarkCard
import com.feryaeljustice.supersnakegame.ui.theme.NeonCyan
import com.feryaeljustice.supersnakegame.ui.theme.NeonGreen
import com.feryaeljustice.supersnakegame.ui.theme.NeonRed
import com.feryaeljustice.supersnakegame.ui.theme.NeonYellow
import kotlinx.coroutines.delay
import kotlin.math.floor
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SnakeGameScreen(
    data: GameScreenData,
    navigateToMenu: () -> Unit,
    viewModel: SnakeGameViewModel = hiltViewModel<SnakeGameViewModel>(),
) {
    val gameState by viewModel.snakeState.collectAsStateWithLifecycle()
    val gameRunning by viewModel.gameRunning.collectAsStateWithLifecycle()
    val isPaused by viewModel.isPaused.collectAsStateWithLifecycle()
    val moveDelay by viewModel.moveDelayMs.collectAsStateWithLifecycle()
    val highestUserCore by viewModel.record.collectAsStateWithLifecycle()
    val settings by viewModel.settingsFlow.collectAsStateWithLifecycle()

    var showSettingsSheet by remember { mutableStateOf(false) }
    val haptics = LocalHapticFeedback.current
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val soundEffectManager = remember(context) { SoundEffectManager(context) }
    DisposableEffect(lifecycleOwner, soundEffectManager) {
        lifecycleOwner.lifecycle.addObserver(soundEffectManager)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(soundEffectManager)
            soundEffectManager.release()
        }
    }

    // Para medir en píxeles (primitivos sin autoboxing)
    var measuredCols by remember { mutableIntStateOf(20) }
    var measuredRows by remember { mutableIntStateOf(20) }

    val focusRequester = remember { FocusRequester() }

    // Loop de movimiento de la serpiente
    LaunchedEffect(gameRunning, isPaused, moveDelay) {
        while (gameRunning && !isPaused && !gameState.isGameOver) {
            delay(moveDelay)
            val ate = viewModel.moveSnakeTo()
            if (ate) {
                if (settings.hapticsEnabled) {
                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }
                if (settings.soundEffectsEnabled) {
                    soundEffectManager.playEatSound(settings.soundEffectsVolume)
                }
            }
        }
    }

    // Efecto háptico en Game Over
    LaunchedEffect(gameState.isGameOver) {
        if (gameState.isGameOver && settings.hapticsEnabled) {
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
        ) {
            // Header: Marcadores neón y botón no intrusivo de opciones
            Surface(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
                border = androidx.compose.foundation.BorderStroke(1.dp, ArcadeBorder.copy(alpha = 0.5f)),
            ) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Puntuación actual
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "PTS",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonGreen,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${gameState.score}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }

                    // Récord
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Récord",
                            tint = NeonYellow,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "RÉCORD: $highestUserCore",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = NeonYellow,
                        )
                    }

                    // Botones de acción: Pausa y Opciones/Ajustes
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (!gameState.isGameOver) {
                            IconButton(
                                onClick = {
                                    if (isPaused) viewModel.resumeGame() else viewModel.pauseGame()
                                },
                                modifier = Modifier.size(36.dp),
                            ) {
                                Icon(
                                    imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                                    contentDescription = if (isPaused) "Reanudar" else "Pausar",
                                    tint = NeonGreen,
                                    modifier = Modifier.size(20.dp),
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                viewModel.pauseGame()
                                showSettingsSheet = true
                            },
                            modifier = Modifier.size(36.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Ajustes",
                                tint = NeonCyan,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }
                }
            }

            // Tablero de juego
            Box(
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(ArcadeBoardBg)
                        .border(1.5.dp, ArcadeBorder, RoundedCornerShape(20.dp))
                        .onSizeChanged { size ->
                            val cellW = size.width / 20f
                            val cellH = size.height / 20f
                            val cellSize = min(cellW, cellH)
                            val cols = floor(size.width / cellSize).toInt()
                            val rows = floor(size.height / cellSize).toInt()

                            measuredCols = cols
                            measuredRows = rows
                            viewModel.setGridSize(cols, rows)
                        }.focusRequester(focusRequester)
                        .focusable()
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
                // Canvas de serpiente y comida
                SnakeGameCanvas(
                    state = gameState,
                    cols = measuredCols,
                    rows = measuredRows,
                    showGrid = settings.showGrid,
                )

                // Controlador gestual por toques en la pantalla
                DirectionController { newDirection ->
                    viewModel.setNewDirection(newDirection = newDirection)
                }

                // D-Pad arcade inferior
                ButtonsDirectionController(
                    modifier =
                        Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 16.dp),
                ) { newDirect ->
                    viewModel.setNewDirection(newDirection = newDirect)
                }

                // Modal/Overlay de Pausa
                if (isPaused && !gameState.isGameOver && !showSettingsSheet) {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.65f))
                                .zIndex(50f),
                        contentAlignment = Alignment.Center,
                    ) {
                        Card(
                            colors =
                                CardDefaults.cardColors(
                                    containerColor = ArcadeDarkCard,
                                ),
                            shape = RoundedCornerShape(20.dp),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, NeonGreen),
                            modifier = Modifier.padding(24.dp),
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Text(
                                    text = "JUEGO EN PAUSA",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonGreen,
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = { viewModel.resumeGame() },
                                    colors =
                                        ButtonDefaults.buttonColors(
                                            containerColor = NeonGreen,
                                            contentColor = Color.Black,
                                        ),
                                    shape = RoundedCornerShape(12.dp),
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Reanudar Partida", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Overlay de Game Over con diseño Arcade
                if (gameState.isGameOver) {
                    val isNewRecord = gameState.score > 0 && gameState.score >= highestUserCore

                    Box(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.75f))
                                .zIndex(100f),
                        contentAlignment = Alignment.Center,
                    ) {
                        Card(
                            modifier =
                                Modifier
                                    .fillMaxWidth(0.88f)
                                    .border(2.dp, NeonRed, RoundedCornerShape(24.dp)),
                            colors =
                                CardDefaults.cardColors(
                                    containerColor = ArcadeDarkCard,
                                ),
                            shape = RoundedCornerShape(24.dp),
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Text(
                                    text = "¡FIN DEL JUEGO!",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Black,
                                    color = NeonRed,
                                    letterSpacing = 1.sp,
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                if (isNewRecord) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = NeonYellow.copy(alpha = 0.2f),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonYellow),
                                    ) {
                                        Text(
                                            text = "🏆 ¡NUEVO RÉCORD PERSONAL!",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = NeonYellow,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                }

                                Text(
                                    text = "Puntuación obtenida: ${gameState.score}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Tu mejor récord: $highestUserCore",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = NeonYellow,
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                Button(
                                    onClick = { viewModel.restartGame() },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors =
                                        ButtonDefaults.buttonColors(
                                            containerColor = NeonGreen,
                                            contentColor = Color.Black,
                                        ),
                                    shape = RoundedCornerShape(14.dp),
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Volver a Jugar", fontWeight = FontWeight.Bold)
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedButton(
                                    onClick = {
                                        viewModel.signOut {
                                            navigateToMenu()
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors =
                                        ButtonDefaults.outlinedButtonColors(
                                            contentColor = Color.White.copy(alpha = 0.8f),
                                        ),
                                    shape = RoundedCornerShape(14.dp),
                                ) {
                                    Text("Cerrar Sesión")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal de Opciones y Ajustes
    if (showSettingsSheet) {
        GameSettingsSheet(
            settings = settings,
            onThemeChanged = { viewModel.setThemeMode(it) },
            onSpeedChanged = { viewModel.setGameSpeed(it) },
            onGridChanged = { viewModel.setShowGrid(it) },
            onHapticsChanged = { viewModel.setHapticsEnabled(it) },
            onSoundEffectsVolumeChanged = { viewModel.setSoundEffectsVolume(it) },
            onSoundEffectsEnabledChanged = { viewModel.setSoundEffectsEnabled(it) },
            onDismissRequest = {
                showSettingsSheet = false
                viewModel.resumeGame()
            },
        )
    }
}
