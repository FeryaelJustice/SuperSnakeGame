package com.feryaeljustice.supersnakegame.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.feryaeljustice.supersnakegame.R

@Composable
fun GoogleButton(
    text: String = "Iniciar sesión con Google",
    loading: Boolean = false,
    loadingText: String = "Iniciando sesión...",
    icon: Painter = painterResource(id = R.drawable.google_logo),
    shape: Shape = ShapeDefaults.Medium,
    borderColor: Color = Color.LightGray,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    progressIndicatorColor: Color = MaterialTheme.colorScheme.primary,
    onClicked: () -> Unit,
) {
    Surface(
        onClick = onClicked,
        shape = shape,
        border = BorderStroke(width = 1.dp, color = borderColor),
        color = backgroundColor,
    ) {
        Row(
            modifier =
                Modifier
                    .padding(start = 12.dp, end = 16.dp, top = 12.dp, bottom = 12.dp)
                    .animateContentSize(
                        animationSpec =
                            tween(
                                durationMillis = 300,
                                easing = LinearOutSlowInEasing,
                            ),
                    ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = icon,
                contentDescription = "Google Button",
                tint = Color.Unspecified,
                modifier = Modifier.size(30.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = if (loading) loadingText else text)
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = progressIndicatorColor,
                )
            }
        }
    }
}

@Composable
@Preview
private fun GoogleButtonPreview() {
    GoogleButton(loading = false, onClicked = {})
}
