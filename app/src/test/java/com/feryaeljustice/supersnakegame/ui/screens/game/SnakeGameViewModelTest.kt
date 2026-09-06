package com.feryaeljustice.supersnakegame.ui.screens.game

import com.feryaeljustice.supersnakegame.domain.Direction
import com.feryaeljustice.supersnakegame.domain.GameSettings
import com.feryaeljustice.supersnakegame.domain.GameSpeed
import com.feryaeljustice.supersnakegame.domain.ThemeMode
import com.feryaeljustice.supersnakegame.domain.usecase.GetHighScoreUseCase
import com.feryaeljustice.supersnakegame.domain.usecase.SaveHighScoreUseCase
import com.feryaeljustice.supersnakegame.fakes.FakeAuthRepository
import com.feryaeljustice.supersnakegame.fakes.FakeRecordRepository
import com.feryaeljustice.supersnakegame.fakes.FakeSettingsRepository
import com.feryaeljustice.supersnakegame.util.MainDispatcherRule
import com.google.firebase.auth.FirebaseUser
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SnakeGameViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeAuthRepo: FakeAuthRepository
    private lateinit var fakeRecordRepo: FakeRecordRepository
    private lateinit var fakeSettingsRepo: FakeSettingsRepository
    private lateinit var getHighScoreUseCase: GetHighScoreUseCase
    private lateinit var saveHighScoreUseCase: SaveHighScoreUseCase
    private lateinit var mockUser: FirebaseUser

    private fun createViewModel(): SnakeGameViewModel {
        return SnakeGameViewModel(
            authRepo = fakeAuthRepo,
            getHighScore = getHighScoreUseCase,
            saveHighScore = saveHighScoreUseCase,
            settingsRepo = fakeSettingsRepo,
        )
    }

    @Before
    fun setUp() {
        mockUser = mockk(relaxed = true)
        every { mockUser.uid } returns "test_user_42"

        fakeAuthRepo = FakeAuthRepository(initialUser = mockUser)
        fakeRecordRepo = FakeRecordRepository(initialRecords = mapOf("test_user_42" to 700))
        fakeSettingsRepo = FakeSettingsRepository()
        getHighScoreUseCase = GetHighScoreUseCase(fakeRecordRepo)
        saveHighScoreUseCase = SaveHighScoreUseCase(fakeRecordRepo)
    }

    @Test
    fun initialState_loadsUserRecordAndSettings() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(700, viewModel.record.value)
        assertTrue(viewModel.gameRunning.value)
        assertFalse(viewModel.isPaused.value)
        assertEquals(100L, viewModel.moveDelayMs.value)
    }

    @Test
    fun pauseAndResumeGame_togglesIsPausedState() = runTest {
        val viewModel = createViewModel()

        viewModel.pauseGame()
        assertTrue(viewModel.isPaused.value)

        viewModel.resumeGame()
        assertFalse(viewModel.isPaused.value)
    }

    @Test
    fun setNewDirection_preventsOppositeDirection180Degrees() = runTest {
        val viewModel = createViewModel()

        // Default initial direction is RIGHT
        assertEquals(Direction.RIGHT, viewModel.snakeState.value.direction)

        // Try turning 180 degrees to LEFT - should be ignored
        viewModel.setNewDirection(Direction.LEFT)
        assertEquals(Direction.RIGHT, viewModel.snakeState.value.direction)

        // Turn UP - valid
        viewModel.setNewDirection(Direction.UP)
        assertEquals(Direction.UP, viewModel.snakeState.value.direction)

        // Try turning 180 degrees to DOWN - should be ignored
        viewModel.setNewDirection(Direction.DOWN)
        assertEquals(Direction.UP, viewModel.snakeState.value.direction)

        // Turn LEFT - valid
        viewModel.setNewDirection(Direction.LEFT)
        assertEquals(Direction.LEFT, viewModel.snakeState.value.direction)
    }

    @Test
    fun moveSnakeTo_doesNothingWhenPausedOrGameOver() = runTest {
        val viewModel = createViewModel()
        viewModel.pauseGame()

        val ate = viewModel.moveSnakeTo()
        assertFalse(ate)
    }

    @Test
    fun setGameSpeed_updatesMoveDelayMs() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.setGameSpeed(GameSpeed.FAST)
        advanceUntilIdle()

        val expectedDelay = 1000L / GameSpeed.FAST.fps
        assertEquals(expectedDelay, viewModel.moveDelayMs.value)
    }

    @Test
    fun setSettings_propagatesToSettingsRepository() = runTest {
        val viewModel = createViewModel()

        viewModel.setThemeMode(ThemeMode.DARK)
        assertEquals(ThemeMode.DARK, fakeSettingsRepo.getSettings().themeMode)

        viewModel.setShowGrid(false)
        assertFalse(fakeSettingsRepo.getSettings().showGrid)

        viewModel.setHapticsEnabled(false)
        assertFalse(fakeSettingsRepo.getSettings().hapticsEnabled)
    }

    @Test
    fun restartGame_resetsGameStateAndUnpauses() = runTest {
        val viewModel = createViewModel()
        viewModel.setGridSize(20, 20)
        viewModel.pauseGame()

        viewModel.restartGame()

        assertFalse(viewModel.isPaused.value)
        assertTrue(viewModel.gameRunning.value)
        assertFalse(viewModel.snakeState.value.isGameOver)
        assertEquals(0, viewModel.snakeState.value.score)
    }

    @Test
    fun signOut_triggersSignOutAndCallback() = runTest {
        val viewModel = createViewModel()
        var callbackCalled = false

        viewModel.signOut {
            callbackCalled = true
        }
        advanceUntilIdle()

        assertTrue(fakeAuthRepo.signOutCalled)
        assertTrue(callbackCalled)
    }
}
