package com.feryaeljustice.supersnakegame.ui.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import com.feryaeljustice.supersnakegame.domain.Direction

@Composable
fun DirectionController(
    modifier: Modifier = Modifier,
    onDirectionChange: (Direction) -> Unit,
) {
    val currentOnDirectionChange by rememberUpdatedState(onDirectionChange)

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val x = offset.x
                        val y = offset.y
                        val w = size.width
                        val h = size.height
                        when {
                            // left third
                            x < w / 3f -> currentOnDirectionChange(Direction.LEFT)
                            // right third
                            x > 2f * w / 3f -> currentOnDirectionChange(Direction.RIGHT)
                            // top third
                            y < h / 3f -> currentOnDirectionChange(Direction.UP)
                            // bottom third
                            y > 2f * h / 3f -> currentOnDirectionChange(Direction.DOWN)
                            // else ignore (center)
                        }
                    }
                },
    )
}
