package com.feryaeljustice.supersnakegame.ui.screens.menu

import android.app.Activity.RESULT_OK
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.feryaeljustice.supersnakegame.R
import com.feryaeljustice.supersnakegame.ui.components.GoogleButton
import com.feryaeljustice.supersnakegame.ui.screens.menu.MainMenuViewModel.UiState
import com.feryaeljustice.supersnakegame.ui.theme.ArcadeBorder
import com.feryaeljustice.supersnakegame.ui.theme.ArcadeDarkBg
import com.feryaeljustice.supersnakegame.ui.theme.ArcadeDarkCard
import com.feryaeljustice.supersnakegame.ui.theme.NeonCyan
import com.feryaeljustice.supersnakegame.ui.theme.NeonGreen
import com.feryaeljustice.supersnakegame.ui.theme.NeonYellow

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("EffectKeys", "ParamsComparedByRef")
@Composable
fun MainMenuScreen(
    viewModel: MainMenuViewModel = hiltViewModel<MainMenuViewModel>(),
    navigateToGameScreen: () -> Unit,
) {
    val ctx = LocalContext.current
    val uiEvents = viewModel.uiEvents
    val ui by viewModel.uiState.collectAsStateWithLifecycle()

    val versionName =
        remember(ctx) {
            try {
                val pInfo = ctx.packageManager.getPackageInfo(ctx.packageName, 0)
                pInfo.versionName ?: "1.1.0"
            } catch (_: Exception) {
                "1.1.0"
            }
        }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(1500, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
        label = "pulseScale",
    )

    val googleSignInLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                viewModel.onOneTapResult(ctx)
            }
        }

    LaunchedEffect(Unit) {
        uiEvents.collect { event ->
            when (event) {
                is MainMenuUiEvent.ShowToast -> {
                    Toast.makeText(ctx, event.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    val currentNavigateToGameScreen by rememberUpdatedState(navigateToGameScreen)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(
                        Brush.verticalGradient(
                            colors =
                                listOf(
                                    MaterialTheme.colorScheme.background,
                                    ArcadeDarkBg,
                                    MaterialTheme.colorScheme.surface,
                                ),
                        ),
                    ),
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
                        .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Badge arcade superior
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = NeonGreen.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonGreen.copy(alpha = 0.5f)),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = NeonYellow,
                            modifier = Modifier.size(14.dp),
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ARCADE RETRO EDITION",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonGreen,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Logo con halo neón y animación de pulso sutil
                Box(
                    modifier =
                        Modifier
                            .size(136.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(NeonGreen.copy(alpha = 0.12f))
                            .border(3.dp, NeonGreen, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_supersnakegame),
                        contentDescription = "App logo",
                        contentScale = ContentScale.Crop,
                        modifier =
                            Modifier
                                .size(118.dp)
                                .clip(CircleShape)
                                .border(1.5.dp, NeonCyan, CircleShape),
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Título con estética arcade
                Text(
                    text = "SUPER SNAKE GAME",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = NeonGreen,
                    letterSpacing = 1.5.sp,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Desarrollado por Feryael Justice",
                    style = MaterialTheme.typography.labelMedium,
                    color = NeonCyan,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.5.sp,
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Tarjeta de inicio de sesión estilo arcade
                Card(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .border(1.dp, ArcadeBorder, RoundedCornerShape(20.dp)),
                    colors =
                        CardDefaults.cardColors(
                            containerColor = ArcadeDarkCard.copy(alpha = 0.85f),
                        ),
                    shape = RoundedCornerShape(20.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "¡Comienza la partida!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Inicia sesión para guardar tus récords en la nube y competir por la máxima puntuación.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center,
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        when (ui) {
                            UiState.Idle, UiState.Loading -> {
                                GoogleButton(
                                    loading = ui is UiState.Loading,
                                    shape = RoundedCornerShape(14.dp),
                                    borderColor = NeonGreen.copy(alpha = 0.6f),
                                    backgroundColor = MaterialTheme.colorScheme.surface,
                                    onClicked = {
                                        viewModel.onGoogleButtonClick(ctx)
                                    },
                                )
                            }

                            is UiState.LaunchUi -> {
                                val intent = (ui as UiState.LaunchUi).sender
                                LaunchedEffect(intent) {
                                    intent?.let {
                                        googleSignInLauncher.launch(
                                            IntentSenderRequest.Builder(it).build(),
                                        )
                                    }
                                }
                            }

                            is UiState.SignedIn -> {
                                LaunchedEffect(Unit) {
                                    currentNavigateToGameScreen()
                                }
                            }

                            is UiState.Error -> {
                                Text(
                                    text = "Error: ${(ui as UiState.Error).message}",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Pills / Características arcade del juego
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    ArcadeFeaturePill(emoji = "⚡", title = "60 FPS")
                    ArcadeFeaturePill(emoji = "☁️", title = "Nube")
                    ArcadeFeaturePill(emoji = "🕹️", title = "D-Pad")
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Pie de página con el número de versión (no código)
                Text(
                    text = "v$versionName",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.sp,
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun ArcadeFeaturePill(
    emoji: String,
    title: String,
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        border = androidx.compose.foundation.BorderStroke(1.dp, ArcadeBorder.copy(alpha = 0.4f)),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = emoji, fontSize = 12.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}
