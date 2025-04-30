package com.feryaeljustice.supersnakegame.di

import com.feryaeljustice.supersnakegame.data.repository.AuthRepositoryImpl
import com.feryaeljustice.supersnakegame.data.repository.RecordRepositoryImpl
import com.feryaeljustice.supersnakegame.domain.repository.AuthRepository
import com.feryaeljustice.supersnakegame.domain.repository.RecordRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {
    @Binds
    fun bindAuthRepository(authRepo: AuthRepositoryImpl): AuthRepository

    @Binds
    fun bindRecordsRepository(recordRepository: RecordRepositoryImpl): RecordRepository
}
