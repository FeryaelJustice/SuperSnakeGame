package com.feryaeljustice.supersnakegame.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.feryaeljustice.supersnakegame.ui.screens.game.SnakeGameScreen
import com.feryaeljustice.supersnakegame.ui.screens.menu.MainMenuScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = MenuScreen) {
        composable<MenuScreen> {
            MainMenuScreen {
                navController.navigate(GameScreen)
            }
        }
        composable<GameScreen>{
            SnakeGameScreen(
                navigateToMenu = {
                    navController.navigate(MenuScreen) {
                        popUpTo(MenuScreen) {
                            inclusive = true
                        }
                    }
                },
            )
        }
    }
}
