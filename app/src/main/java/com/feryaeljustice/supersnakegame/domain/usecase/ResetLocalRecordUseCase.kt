package com.feryaeljustice.supersnakegame.domain.usecase

import com.feryaeljustice.supersnakegame.domain.repository.RecordRepository
import javax.inject.Inject

class ResetLocalRecordUseCase
    @Inject
    constructor(
        private val repo: RecordRepository,
    ) {
        suspend operator fun invoke() {
            repo.resetLocalRecord()
        }
    }
