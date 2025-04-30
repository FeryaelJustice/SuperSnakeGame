package com.feryaeljustice.supersnakegame.domain.usecase

import com.feryaeljustice.supersnakegame.domain.repository.RecordRepository
import javax.inject.Inject

class SaveHighScoreUseCase
    @Inject
    constructor(
        private val repo: RecordRepository,
    ) {
        suspend operator fun invoke(
            userId: String,
            newScore: Int,
        ): Int = repo.saveIfHigher(userId, newScore)
    }
