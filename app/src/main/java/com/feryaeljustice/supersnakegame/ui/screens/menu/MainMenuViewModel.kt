package com.feryaeljustice.supersnakegame.ui.screens.menu

import android.content.IntentSender
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.feryaeljustice.supersnakegame.domain.AuthResult
import com.feryaeljustice.supersnakegame.domain.repository.AuthRepository
import com.feryaeljustice.supersnakegame.ui.screens.menu.MainMenuViewModel.UiState.Idle
import com.feryaeljustice.supersnakegame.ui.screens.menu.MainMenuViewModel.UiState.LaunchUi
import com.feryaeljustice.supersnakegame.ui.screens.menu.MainMenuViewModel.UiState.Loading
import com.feryaeljustice.supersnakegame.ui.screens.menu.MainMenuViewModel.UiState.SignedIn
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainMenuViewModel
    @Inject
    constructor(
        private val authRepo: AuthRepository,
    ) : ViewModel() {
        sealed class UiState {
            object Idle : UiState()

            object Loading : UiState()

            data class LaunchUi(
                val sender: IntentSender?,
            ) : UiState()

            data class SignedIn(
                val user: FirebaseUser,
            ) : UiState()

            data class Error(
                val message: String?,
            ) : UiState()
        }

        // UI STATE
        private val _uiState = MutableStateFlow<UiState>(Idle)
        val uiState: StateFlow<UiState> = _uiState.asStateFlow()

        // UI EVENTS
        private val _uiEvents = MutableSharedFlow<MainMenuUiEvent>()
        val uiEvents: SharedFlow<MainMenuUiEvent> = _uiEvents

        init {
            getFirebaseUser()
        }

        private fun getFirebaseUser() {
            val curUser = authRepo.getCurrentFirebaseAuthUser()
            curUser?.let {
                _uiState.value = SignedIn(it)
            }
        }

        /** Llamar al click de tu GoogleButton */
        @Suppress("TooGenericExceptionCaught")
        fun onGoogleButtonClick() {
            viewModelScope.launch {
                _uiState.value = Loading
                when (val res = authRepo.requestGoogleIdToken()) {
                    is AuthResult.Failure -> {
                        val message = res.exception.message
                        // _uiState.value = Error(res.exception.message)
                        Log.e("signIn", "error $message")
                        _uiState.value = Idle
                        message?.let { msg ->
                            _uiEvents.emit(MainMenuUiEvent.ShowToast(msg))
                        }
                    }

                    is AuthResult.NeedsUi -> _uiState.value = LaunchUi(res.intentSender)
                    is AuthResult.Success -> {
                        try {
                            val user = authRepo.firebaseSignIn(res.idToken)
                            user?.let {
                                _uiState.value = SignedIn(it)
                            } ?: error("User is null")
                        } catch (t: Throwable) {
                            // _uiState.value = Error(t.message)
                            Log.e("signIn", "error ${t.message}")
                            _uiState.value = Idle
                        }
                    }
                }
            }
        }

        /** Llamar después de que el IntentSender devuelva RESULT_OK */
        fun onOneTapResult() {
            onGoogleButtonClick()
        }
    }
