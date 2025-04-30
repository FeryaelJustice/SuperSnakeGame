package com.feryaeljustice.supersnakegame.domain.repository

interface RecordRepository {
    suspend fun getRecordForUser(userId: String): Int?

    suspend fun saveIfHigher(
        userId: String,
        newScore: Int,
    ): Int
}
