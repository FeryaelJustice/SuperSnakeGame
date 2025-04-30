package com.feryaeljustice.supersnakegame.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.feryaeljustice.supersnakegame.ui.navigation.GameScreen
import com.feryaeljustice.supersnakegame.ui.screens.game.SnakeGameScreen
import com.feryaeljustice.supersnakegame.ui.screens.menu.MainMenuScreen
import kotlin.reflect.typeOf

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = MenuScreen) {
        composable<MenuScreen> {
            MainMenuScreen { gameScreenData ->
                navController.navigate(GameScreen(gameScreenData))
            }
        }
        composable<GameScreen>(
            typeMap = mapOf(typeOf<GameScreenData>() to createNavType<GameScreenData>()),
        ) { backStackEntry ->
            val gameScreen: GameScreen = backStackEntry.toRoute()
            SnakeGameScreen(data = gameScreen.data, navigateToMenu = {
                navController.navigate(MenuScreen) {
                    popUpTo(MenuScreen) {
                        inclusive = true
                    }
                }
            })
        }
    }
}
