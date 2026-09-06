package com.feryaeljustice.supersnakegame.domain

import com.feryaeljustice.supersnakegame.domain.usecase.GetHighScoreUseCase
import com.feryaeljustice.supersnakegame.domain.usecase.SaveHighScoreUseCase
import com.feryaeljustice.supersnakegame.fakes.FakeRecordRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class UseCasesTest {

    private lateinit var fakeRepo: FakeRecordRepository
    private lateinit var getHighScoreUseCase: GetHighScoreUseCase
    private lateinit var saveHighScoreUseCase: SaveHighScoreUseCase

    @Before
    fun setUp() {
        fakeRepo = FakeRecordRepository(
            initialRecords = mapOf("user_1" to 500),
        )
        getHighScoreUseCase = GetHighScoreUseCase(fakeRepo)
        saveHighScoreUseCase = SaveHighScoreUseCase(fakeRepo)
    }

    @Test
    fun getHighScoreUseCase_returnsExistingScore() = runTest {
        val score = getHighScoreUseCase("user_1")
        assertEquals(500, score)
    }

    @Test
    fun getHighScoreUseCase_returnsZeroWhenUserHasNoScore() = runTest {
        val score = getHighScoreUseCase("unknown_user")
        assertEquals(0, score)
    }

    @Test
    fun saveHighScoreUseCase_savesHigherScore() = runTest {
        val result = saveHighScoreUseCase("user_1", 800)
        assertEquals(800, result)
        assertEquals(800, getHighScoreUseCase("user_1"))
    }

    @Test
    fun saveHighScoreUseCase_ignoresLowerScore() = runTest {
        val result = saveHighScoreUseCase("user_1", 300)
        assertEquals(500, result)
        assertEquals(500, getHighScoreUseCase("user_1"))
    }

    @Test
    fun saveHighScoreUseCase_createsScoreForNewUser() = runTest {
        val result = saveHighScoreUseCase("new_user", 250)
        assertEquals(250, result)
        assertEquals(250, getHighScoreUseCase("new_user"))
    }
}
