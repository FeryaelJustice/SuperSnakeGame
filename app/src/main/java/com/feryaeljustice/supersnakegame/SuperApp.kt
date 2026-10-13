package com.feryaeljustice.supersnakegame

import android.app.Application
import com.feryaeljustice.supersnakegame.data.manager.AdManager
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class SuperApp : Application() {

    @Inject
    lateinit var adManager: AdManager

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch {
            adManager.initialize()
        }
    }
}
