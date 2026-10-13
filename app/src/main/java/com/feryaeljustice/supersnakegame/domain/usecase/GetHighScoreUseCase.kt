package com.feryaeljustice.supersnakegame.domain.usecase

import com.feryaeljustice.supersnakegame.domain.repository.RecordRepository
import javax.inject.Inject

class GetHighScoreUseCase
    @Inject
    constructor(
        private val repo: RecordRepository,
    ) {
        suspend operator fun invoke(userId: String? = null): Int {
            if (userId != null) {
                val cloudScore = repo.getRecordForUser(userId)
                if (cloudScore != null) {
                    return cloudScore
                }
                val cachedCloudScore = repo.getCachedRecordForUser(userId)
                if (cachedCloudScore > 0) {
                    return cachedCloudScore
                }
                return repo.getLocalRecord()
            }
            return repo.getLocalRecord()
        }
    }
