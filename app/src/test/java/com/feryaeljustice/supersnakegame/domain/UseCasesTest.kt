package com.feryaeljustice.supersnakegame.domain

import com.feryaeljustice.supersnakegame.domain.usecase.GetHighScoreUseCase
import com.feryaeljustice.supersnakegame.domain.usecase.ResetLocalRecordUseCase
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
    private lateinit var resetLocalRecordUseCase: ResetLocalRecordUseCase

    @Before
    fun setUp() {
        fakeRepo = FakeRecordRepository(
            initialRecords = mapOf("user_1" to 500),
        )
        getHighScoreUseCase = GetHighScoreUseCase(fakeRepo)
        saveHighScoreUseCase = SaveHighScoreUseCase(fakeRepo)
        resetLocalRecordUseCase = ResetLocalRecordUseCase(fakeRepo)
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

    @Test
    fun getHighScoreUseCase_offlineMode_returnsLocalRecord() = runTest {
        fakeRepo.localRecord = 350
        val score = getHighScoreUseCase(null)
        assertEquals(350, score)
    }

    @Test
    fun saveHighScoreUseCase_offlineMode_savesHigherLocalScore() = runTest {
        fakeRepo.localRecord = 200
        val result = saveHighScoreUseCase(null, 450)
        assertEquals(450, result)
        assertEquals(450, getHighScoreUseCase(null))
    }

    @Test
    fun saveHighScoreUseCase_offlineMode_ignoresLowerLocalScore() = runTest {
        fakeRepo.localRecord = 600
        val result = saveHighScoreUseCase(null, 250)
        assertEquals(600, result)
        assertEquals(600, getHighScoreUseCase(null))
    }

    @Test
    fun saveHighScoreUseCase_onlineMode_doesNotTouchLocalRecord() = runTest {
        fakeRepo.localRecord = 100
        saveHighScoreUseCase("user_1", 999)
        assertEquals(999, getHighScoreUseCase("user_1"))
        assertEquals(100, fakeRepo.getLocalRecord())
        assertEquals(100, getHighScoreUseCase(null))
    }

    @Test
    fun saveHighScoreUseCase_offlineMode_doesNotTouchCloudRecord() = runTest {
        fakeRepo.localRecord = 100
        saveHighScoreUseCase(null, 999)
        assertEquals(999, fakeRepo.getLocalRecord())
        assertEquals(500, getHighScoreUseCase("user_1"))
    }

    @Test
    fun getHighScoreUseCase_cloudFails_fallsBackToCachedUserRecord() = runTest {
        fakeRepo.cachedUserRecords["user_1"] = 450
        fakeRepo.localRecord = 120
        fakeRepo.simulateCloudFailure = true

        val score = getHighScoreUseCase("user_1")
        assertEquals(450, score)
    }

    @Test
    fun getHighScoreUseCase_cloudFailsWithoutCache_fallsBackToLocalRecord() = runTest {
        fakeRepo.localRecord = 120
        fakeRepo.simulateCloudFailure = true

        val score = getHighScoreUseCase("user_without_cache")
        assertEquals(120, score)
    }

    @Test
    fun saveHighScoreUseCase_cloudFails_savesToUserCacheWithoutTouchingLocalRecord() = runTest {
        fakeRepo.localRecord = 80
        fakeRepo.simulateCloudFailure = true

        val result = saveHighScoreUseCase("user_1", 750)
        assertEquals(750, result)
        assertEquals(750, fakeRepo.getCachedRecordForUser("user_1"))
        assertEquals(80, fakeRepo.getLocalRecord())
    }

    @Test
    fun resetLocalRecordUseCase_clearsLocalRecordToZero() = runTest {
        fakeRepo.localRecord = 650
        resetLocalRecordUseCase()
        assertEquals(0, fakeRepo.getLocalRecord())
        assertEquals(0, getHighScoreUseCase(null))
    }
}
