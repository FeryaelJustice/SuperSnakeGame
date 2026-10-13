package com.feryaeljustice.supersnakegame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.feryaeljustice.supersnakegame.data.manager.AdManager
import com.feryaeljustice.supersnakegame.data.manager.LocalAdManager
import com.feryaeljustice.supersnakegame.domain.repository.SettingsRepository
import com.feryaeljustice.supersnakegame.ui.navigation.AppNavigation
import com.feryaeljustice.supersnakegame.ui.theme.SuperSnakeGameTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    @Inject
    lateinit var adManager: AdManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settings by settingsRepository.settingsFlow.collectAsStateWithLifecycle()
            SuperSnakeGameTheme(themeMode = settings.themeMode) {
                CompositionLocalProvider(LocalAdManager provides adManager) {
                    AppNavigation()
                }
            }
        }
    }
}
