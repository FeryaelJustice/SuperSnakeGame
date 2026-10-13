package com.feryaeljustice.supersnakegame.di

import android.content.Context
import com.feryaeljustice.supersnakegame.data.manager.AdManager
import com.feryaeljustice.supersnakegame.data.manager.AdMobManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AdModule {

    @Provides
    @Singleton
    fun provideAdMobManager(
        @ApplicationContext context: Context,
    ): AdMobManager {
        return AdMobManager(context)
    }

    @Provides
    @Singleton
    fun provideAdManager(
        adMobManager: AdMobManager,
    ): AdManager {
        return adMobManager
    }
}
