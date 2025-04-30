package com.feryaeljustice.supersnakegame.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import com.feryaeljustice.supersnakegame.ui.screens.game.SnakeGameState

@Composable
fun BoxScope.SnakeGameCanvas(
    state: SnakeGameState,
    cols: Int,
    rows: Int,
    frame: Long,
) {
    Canvas(
        modifier =
            Modifier
                .matchParentSize()
                .clipToBounds(),
    ) {
        val cellW = size.width / cols
        val cellH = size.height / rows

        // Snake picture
        state.snake.forEach { (x, y) ->
            drawRect(
                color = Color.Green,
                topLeft = Offset(x * cellW, y * cellH),
                size = Size(cellW, cellH),
            )
        }

        // Picture of food
        drawRect(
            color = Color.Red,
            topLeft = Offset(state.food.first * cellW, state.food.second * cellH),
            size = Size(cellW, cellH),
        )
    }
}
