package com.feryaeljustice.supersnakegame.domain.usecase

import com.feryaeljustice.supersnakegame.domain.repository.RecordRepository
import javax.inject.Inject

class GetHighScoreUseCase
    @Inject
    constructor(
        private val repo: RecordRepository,
    ) {
        suspend operator fun invoke(userId: String): Int = repo.getRecordForUser(userId) ?: 0
    }
