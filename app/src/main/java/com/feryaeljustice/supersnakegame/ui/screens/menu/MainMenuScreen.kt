package com.feryaeljustice.supersnakegame.ui.screens.menu

import android.app.Activity.RESULT_OK
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.feryaeljustice.supersnakegame.R
import com.feryaeljustice.supersnakegame.ui.components.GoogleButton
import com.feryaeljustice.supersnakegame.ui.navigation.GameScreenData
import com.feryaeljustice.supersnakegame.ui.screens.menu.MainMenuViewModel.UiState

@Suppress("EffectKeys")
@Composable
fun MainMenuScreen(
    viewModel: MainMenuViewModel = hiltViewModel<MainMenuViewModel>(),
    navigateToGameScreen: (GameScreenData) -> Unit,
) {
    val ctx = LocalContext.current
    val uiEvents = viewModel.uiEvents
    val ui by viewModel.uiState.collectAsStateWithLifecycle()

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

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_supersnakegame),
                    contentDescription = "App logo",
                    contentScale = ContentScale.Crop,
                    modifier =
                        Modifier
                            .size(120.dp)
                            .clip(
                                CircleShape,
                            ).border(2.dp, Color.Green, CircleShape),
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Super Snake Game",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.Cyan,
                )
                Spacer(
                    modifier = Modifier.height(12.dp),
                )
                Text(
                    text = "By Feryael Justice",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Cyan,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Comienza el juego",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Green,
                )
                Spacer(modifier = Modifier.height(16.dp))
                when (ui) {
                    UiState.Idle, UiState.Loading -> {
                        GoogleButton(
                            loading = ui is UiState.Loading,
                            onClicked = {
                                viewModel.onGoogleButtonClick(ctx)
                            },
                        )
                    }

                    is UiState.LaunchUi -> {
                        // Disparamos la UI de One-Tap
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
                        // Ya autenticado, navegamos
                        LaunchedEffect(Unit) {
                            val data = GameScreenData(gameId = "1")
                            currentNavigateToGameScreen(data)
                        }
                    }

                    is UiState.Error -> {
                        Text("Error: ${(ui as UiState.Error).message}")
                    }
                }
            }
        }
    }
}
