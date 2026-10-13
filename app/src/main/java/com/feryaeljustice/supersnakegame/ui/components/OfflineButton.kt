package com.feryaeljustice.supersnakegame.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.feryaeljustice.supersnakegame.ui.theme.ArcadeDarkCard
import com.feryaeljustice.supersnakegame.ui.theme.NeonCyan

@Composable
fun OfflineButton(
    modifier: Modifier = Modifier,
    text: String = "MODO OFFLINE",
    subtitle: String = "Jugar sin conexión (Récord local)",
    icon: ImageVector = Icons.Default.SportsEsports,
    shape: Shape = RoundedCornerShape(14.dp),
    borderColor: Color = NeonCyan.copy(alpha = 0.8f),
    backgroundColor: Color = ArcadeDarkCard,
    contentColor: Color = NeonCyan,
    onClicked: () -> Unit,
) {
    Surface(
        onClick = onClicked,
        modifier = modifier.fillMaxWidth(),
        shape = shape,
        border = BorderStroke(width = 1.5.dp, color = borderColor),
        color = backgroundColor,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = contentColor,
                modifier = Modifier.size(28.dp),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Black,
                    color = contentColor,
                    letterSpacing = 1.sp,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 11.sp,
                )
            }
        }
    }
}

@Preview
@Composable
private fun OfflineButtonPreview() {
    OfflineButton(onClicked = {})
}
