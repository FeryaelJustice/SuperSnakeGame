package com.feryaeljustice.supersnakegame.domain.repository

interface RecordRepository {
    suspend fun getRecordForUser(userId: String): Int?

    suspend fun saveIfHigher(
        userId: String,
        newScore: Int,
    ): Int

    suspend fun getCachedRecordForUser(userId: String): Int

    suspend fun saveCachedRecordForUser(userId: String, score: Int)

    suspend fun getLocalRecord(): Int

    suspend fun saveLocalRecordIfHigher(newScore: Int): Int

    suspend fun resetLocalRecord()
}
