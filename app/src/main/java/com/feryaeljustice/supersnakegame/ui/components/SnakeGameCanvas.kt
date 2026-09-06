package com.feryaeljustice.supersnakegame.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import com.feryaeljustice.supersnakegame.domain.Direction
import com.feryaeljustice.supersnakegame.ui.screens.game.SnakeGameState
import com.feryaeljustice.supersnakegame.ui.theme.ArcadeGridLine
import com.feryaeljustice.supersnakegame.ui.theme.NeonGreen
import com.feryaeljustice.supersnakegame.ui.theme.NeonGreenDark
import com.feryaeljustice.supersnakegame.ui.theme.NeonRed
import com.feryaeljustice.supersnakegame.ui.theme.NeonYellow
import kotlin.math.sin

@Composable
fun BoxScope.SnakeGameCanvas(
    state: SnakeGameState,
    cols: Int,
    rows: Int,
    frame: Long,
    showGrid: Boolean = true,
) {
    Canvas(
        modifier =
            Modifier
                .matchParentSize()
                .clipToBounds(),
    ) {
        val cellW = size.width / cols
        val cellH = size.height / rows

        // 1. Líneas de cuadrícula sutiles si está habilitado
        if (showGrid) {
            for (c in 1 until cols) {
                val x = c * cellW
                drawLine(
                    color = ArcadeGridLine,
                    start = Offset(x, 0f),
                    end = Offset(x, size.height),
                    strokeWidth = 1f,
                )
            }
            for (r in 1 until rows) {
                val y = r * cellH
                drawLine(
                    color = ArcadeGridLine,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1f,
                )
            }
        }

        // 2. Dibujar comida brillante y pulsante
        val foodX = state.food.first * cellW
        val foodY = state.food.second * cellH
        val foodCenterX = foodX + cellW / 2f
        val foodCenterY = foodY + cellH / 2f
        val pulse = 1f + 0.12f * sin(frame / 150_000_000.0).toFloat()
        val foodRadius = (cellW.coerceAtMost(cellH) / 2.3f) * pulse

        // Halo exterior de comida
        drawCircle(
            color = NeonRed.copy(alpha = 0.25f),
            radius = foodRadius * 1.5f,
            center = Offset(foodCenterX, foodCenterY),
        )
        // Núcleo de comida
        drawCircle(
            brush =
                Brush.radialGradient(
                    colors = listOf(NeonYellow, NeonRed),
                    center = Offset(foodCenterX - foodRadius * 0.3f, foodCenterY - foodRadius * 0.3f),
                    radius = foodRadius,
                ),
            radius = foodRadius,
            center = Offset(foodCenterX, foodCenterY),
        )
        // Brillo blanco en la fruta
        drawCircle(
            color = Color.White.copy(alpha = 0.8f),
            radius = foodRadius * 0.25f,
            center = Offset(foodCenterX - foodRadius * 0.35f, foodCenterY - foodRadius * 0.35f),
        )

        // 3. Dibujar cuerpo de la serpiente
        val snakeSegments = state.snake
        val cornerRadius = CornerRadius(cellW * 0.3f, cellH * 0.3f)

        snakeSegments.forEachIndexed { index, (x, y) ->
            val segX = x * cellW
            val segY = y * cellH

            if (index == 0) {
                // Cabeza de la serpiente con esquinas redondeadas y ojos
                val headPadding = cellW * 0.05f
                val headRect =
                    RoundRect(
                        left = segX + headPadding,
                        top = segY + headPadding,
                        right = segX + cellW - headPadding,
                        bottom = segY + cellH - headPadding,
                        cornerRadius = CornerRadius(cellW * 0.4f, cellH * 0.4f),
                    )
                val headPath = Path().apply { addRoundRect(headRect) }
                drawPath(
                    path = headPath,
                    brush =
                        Brush.linearGradient(
                            colors = listOf(NeonGreen, NeonGreenDark),
                            start = Offset(segX, segY),
                            end = Offset(segX + cellW, segY + cellH),
                        ),
                )

                // Ojos direccionales de la serpiente
                val eyeRadius = cellW * 0.12f
                val pupilRadius = cellW * 0.06f

                val (eye1Offset, eye2Offset) =
                    when (state.direction) {
                        Direction.UP -> {
                            Pair(
                                Offset(segX + cellW * 0.3f, segY + cellH * 0.3f),
                                Offset(segX + cellW * 0.7f, segY + cellH * 0.3f),
                            )
                        }

                        Direction.DOWN -> {
                            Pair(
                                Offset(segX + cellW * 0.3f, segY + cellH * 0.7f),
                                Offset(segX + cellW * 0.7f, segY + cellH * 0.7f),
                            )
                        }

                        Direction.LEFT -> {
                            Pair(
                                Offset(segX + cellW * 0.3f, segY + cellH * 0.3f),
                                Offset(segX + cellW * 0.3f, segY + cellH * 0.7f),
                            )
                        }

                        Direction.RIGHT -> {
                            Pair(
                                Offset(segX + cellW * 0.7f, segY + cellH * 0.3f),
                                Offset(segX + cellW * 0.7f, segY + cellH * 0.7f),
                            )
                        }
                    }

                // Fondo blanco de los ojos
                drawCircle(color = Color.White, radius = eyeRadius, center = eye1Offset)
                drawCircle(color = Color.White, radius = eyeRadius, center = eye2Offset)
                // Pupilas negras orientadas
                drawCircle(color = Color.Black, radius = pupilRadius, center = eye1Offset)
                drawCircle(color = Color.Black, radius = pupilRadius, center = eye2Offset)
            } else {
                // Segmento del cuerpo con esquinas suaves y gradiente neón
                val bodyPadding = cellW * 0.08f
                drawRoundRect(
                    brush =
                        Brush.linearGradient(
                            colors = listOf(NeonGreen.copy(alpha = 0.95f), NeonGreenDark.copy(alpha = 0.85f)),
                            start = Offset(segX, segY),
                            end = Offset(segX + cellW, segY + cellH),
                        ),
                    topLeft = Offset(segX + bodyPadding, segY + bodyPadding),
                    size = Size(cellW - bodyPadding * 2f, cellH - bodyPadding * 2f),
                    cornerRadius = cornerRadius,
                )
            }
        }
    }
}
