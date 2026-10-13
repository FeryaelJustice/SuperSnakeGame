package com.feryaeljustice.supersnakegame.domain.usecase

import com.feryaeljustice.supersnakegame.domain.repository.RecordRepository
import javax.inject.Inject

class SaveHighScoreUseCase
    @Inject
    constructor(
        private val repo: RecordRepository,
    ) {
        suspend operator fun invoke(
            userId: String? = null,
            newScore: Int,
        ): Int {
            if (userId != null) {
                return try {
                    repo.saveIfHigher(userId, newScore)
                } catch (_: Exception) {
                    val currentCached = repo.getCachedRecordForUser(userId)
                    val best = maxOf(currentCached, newScore)
                    repo.saveCachedRecordForUser(userId, best)
                    best
                }
            }
            return repo.saveLocalRecordIfHigher(newScore)
        }
    }
