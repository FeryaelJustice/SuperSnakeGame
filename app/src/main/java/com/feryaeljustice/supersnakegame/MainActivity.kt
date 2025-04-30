package com.feryaeljustice.supersnakegame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.feryaeljustice.supersnakegame.ui.navigation.AppNavigation
import com.feryaeljustice.supersnakegame.ui.theme.SuperSnakeGameTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SuperSnakeGameTheme {
                AppNavigation()
            }
        }
    }
}
