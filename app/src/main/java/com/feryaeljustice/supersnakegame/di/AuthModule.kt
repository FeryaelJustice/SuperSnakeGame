package com.feryaeljustice.supersnakegame.di

import android.content.Context
import androidx.credentials.CredentialManager
import com.feryaeljustice.supersnakegame.R
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideCredentialManager(
        @ApplicationContext ctx: Context,
    ): CredentialManager = CredentialManager.create(ctx)

    @Provides
    @Named("webClientId")
    fun provideWebClientId(
        @ApplicationContext ctx: Context,
    ) = ctx.getString(R.string.default_web_client_id)
}
