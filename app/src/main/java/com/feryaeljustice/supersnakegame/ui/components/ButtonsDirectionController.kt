package com.feryaeljustice.supersnakegame.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.feryaeljustice.supersnakegame.domain.Direction

@Composable
fun ButtonsDirectionController(
    modifier: Modifier,
    onDirectionChange: (Direction) -> Unit,
) {
    // Tamaño total del D-pad
    val padSize = 180.dp
    // Diámetro de cada botón
    val buttonSize = 82.dp
    // Alpha de cada botón
    val buttonAlpha = 0.5f
    // Espacio para que el botón no quede justo en el borde
    val padding = 12.dp

    Box(modifier = modifier.size(padSize)) {
        // UP
        Box(
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .size(buttonSize)
                    .padding(padding) // expande área de toque
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = buttonAlpha))
                    .clickable { onDirectionChange(Direction.UP) },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowUp,
                contentDescription = "Up",
                tint = Color.White,
            )
        }

        // DOWN
        Box(
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .size(buttonSize)
                    .padding(padding)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = buttonAlpha))
                    .clickable { onDirectionChange(Direction.DOWN) },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Down",
                tint = Color.White,
            )
        }

        // LEFT
        Box(
            modifier =
                Modifier
                    .align(Alignment.CenterStart)
                    .size(buttonSize)
                    .padding(padding)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = buttonAlpha))
                    .clickable { onDirectionChange(Direction.LEFT) },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                contentDescription = "Left",
                tint = Color.White,
            )
        }

        // RIGHT
        Box(
            modifier =
                Modifier
                    .align(Alignment.CenterEnd)
                    .size(buttonSize)
                    .padding(padding)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = buttonAlpha))
                    .clickable { onDirectionChange(Direction.RIGHT) },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowForward,
                contentDescription = "Right",
                tint = Color.White,
            )
        }
    }
}
