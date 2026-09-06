package com.feryaeljustice.supersnakegame.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.feryaeljustice.supersnakegame.domain.Direction
import com.feryaeljustice.supersnakegame.ui.theme.NeonGreen

@Composable
fun ButtonsDirectionController(
    modifier: Modifier = Modifier,
    onDirectionChange: (Direction) -> Unit,
) {
    // Tamaño total del D-pad
    val padSize = 180.dp
    val buttonSize = 82.dp
    val padding = 10.dp

    Box(modifier = modifier.size(padSize)) {
        DirectionButton(
            alignment = Alignment.TopCenter,
            buttonSize = buttonSize,
            padding = padding,
            icon = Icons.Default.KeyboardArrowUp,
            contentDescription = "Up",
            onClick = { onDirectionChange(Direction.UP) },
        )

        DirectionButton(
            alignment = Alignment.BottomCenter,
            buttonSize = buttonSize,
            padding = padding,
            icon = Icons.Default.KeyboardArrowDown,
            contentDescription = "Down",
            onClick = { onDirectionChange(Direction.DOWN) },
        )

        DirectionButton(
            alignment = Alignment.CenterStart,
            buttonSize = buttonSize,
            padding = padding,
            icon = Icons.AutoMirrored.Default.ArrowBack,
            contentDescription = "Left",
            onClick = { onDirectionChange(Direction.LEFT) },
        )

        DirectionButton(
            alignment = Alignment.CenterEnd,
            buttonSize = buttonSize,
            padding = padding,
            icon = Icons.AutoMirrored.Default.ArrowForward,
            contentDescription = "Right",
            onClick = { onDirectionChange(Direction.RIGHT) },
        )
    }
}

@Composable
private fun BoxScope.DirectionButton(
    alignment: Alignment,
    buttonSize: Dp,
    padding: Dp,
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .align(alignment)
                .size(buttonSize)
                .padding(padding)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.65f))
                .border(1.5.dp, NeonGreen.copy(alpha = 0.6f), CircleShape)
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = NeonGreen,
            modifier = Modifier.size(32.dp),
        )
    }
}

