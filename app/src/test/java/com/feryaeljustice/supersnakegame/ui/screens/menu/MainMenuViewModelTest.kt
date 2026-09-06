package com.feryaeljustice.supersnakegame.ui.screens.menu

import android.content.Context
import android.content.IntentSender
import com.feryaeljustice.supersnakegame.domain.AuthResult
import com.feryaeljustice.supersnakegame.domain.GameSpeed
import com.feryaeljustice.supersnakegame.domain.ThemeMode
import com.feryaeljustice.supersnakegame.fakes.FakeAuthRepository
import com.feryaeljustice.supersnakegame.fakes.FakeSettingsRepository
import com.feryaeljustice.supersnakegame.util.MainDispatcherRule
import com.google.firebase.auth.FirebaseUser
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainMenuViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeAuthRepo: FakeAuthRepository
    private lateinit var fakeSettingsRepo: FakeSettingsRepository
    private lateinit var mockContext: Context
    private lateinit var mockUser: FirebaseUser

    @Before
    fun setUp() {
        mockContext = mockk(relaxed = true)
        mockUser = mockk(relaxed = true)
        fakeAuthRepo = FakeAuthRepository(initialUser = null)
        fakeSettingsRepo = FakeSettingsRepository()
    }

    private fun createViewModel(): MainMenuViewModel {
        return MainMenuViewModel(
            authRepo = fakeAuthRepo,
            settingsRepo = fakeSettingsRepo,
        )
    }

    @Test
    fun initialState_whenNoUser_isIdle() {
        val viewModel = createViewModel()
        assertTrue(viewModel.uiState.value is MainMenuViewModel.UiState.Idle)
    }

    @Test
    fun initialState_whenUserAlreadySignedIn_isSignedIn() {
        fakeAuthRepo.initialUser = mockUser
        val viewModel = createViewModel()
        val state = viewModel.uiState.value
        assertTrue(state is MainMenuViewModel.UiState.SignedIn)
        assertEquals(mockUser, (state as MainMenuViewModel.UiState.SignedIn).user)
    }

    @Test
    fun onGoogleButtonClick_success_transitionsToSignedIn() = runTest {
        fakeAuthRepo.googleIdTokenResult = AuthResult.Success("sample_token")
        fakeAuthRepo.signInUserResult = mockUser

        val viewModel = createViewModel()
        viewModel.onGoogleButtonClick(mockContext)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is MainMenuViewModel.UiState.SignedIn)
        assertEquals(mockUser, (state as MainMenuViewModel.UiState.SignedIn).user)
    }

    @Test
    fun onGoogleButtonClick_needsUi_transitionsToLaunchUi() = runTest {
        val mockSender = mockk<IntentSender>(relaxed = true)
        fakeAuthRepo.googleIdTokenResult = AuthResult.NeedsUi(mockSender)

        val viewModel = createViewModel()
        viewModel.onGoogleButtonClick(mockContext)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is MainMenuViewModel.UiState.LaunchUi)
        assertEquals(mockSender, (state as MainMenuViewModel.UiState.LaunchUi).sender)
    }

    @Test
    fun onGoogleButtonClick_failure_emitsToastAndReturnsToIdle() = runTest {
        val errorMessage = "Google Sign-In failed network timeout"
        fakeAuthRepo.googleIdTokenResult = AuthResult.Failure(RuntimeException(errorMessage))

        val viewModel = createViewModel()

        val emittedEvents = mutableListOf<MainMenuUiEvent>()
        val job = launch(kotlinx.coroutines.test.UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiEvents.collect { emittedEvents.add(it) }
        }

        viewModel.onGoogleButtonClick(mockContext)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is MainMenuViewModel.UiState.Idle)
        assertEquals(1, emittedEvents.size)
        val event = emittedEvents.first()
        assertTrue(event is MainMenuUiEvent.ShowToast)
        assertEquals(errorMessage, (event as MainMenuUiEvent.ShowToast).message)

        job.cancel()
    }

    @Test
    fun settingsMethods_updateSettingsRepository() {
        val viewModel = createViewModel()

        viewModel.setThemeMode(ThemeMode.LIGHT)
        assertEquals(ThemeMode.LIGHT, fakeSettingsRepo.getSettings().themeMode)

        viewModel.setGameSpeed(GameSpeed.CHILL)
        assertEquals(GameSpeed.CHILL, fakeSettingsRepo.getSettings().gameSpeed)

        viewModel.setShowGrid(false)
        assertFalse(fakeSettingsRepo.getSettings().showGrid)

        viewModel.setHapticsEnabled(false)
        assertFalse(fakeSettingsRepo.getSettings().hapticsEnabled)
    }
}
