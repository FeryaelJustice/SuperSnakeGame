package com.feryaeljustice.supersnakegame.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.feryaeljustice.supersnakegame.domain.ThemeMode

private val DarkColorScheme =
    darkColorScheme(
        primary = NeonGreen,
        onPrimary = Color.Black,
        primaryContainer = NeonGreenDark,
        onPrimaryContainer = Color.White,
        secondary = NeonCyan,
        onSecondary = Color.Black,
        tertiary = NeonPink,
        background = ArcadeDarkBg,
        onBackground = Color(0xFFE2E8F0),
        surface = ArcadeDarkSurface,
        onSurface = Color(0xFFF1F5F9),
        surfaceVariant = ArcadeDarkCard,
        onSurfaceVariant = Color(0xFF94A3B8),
        outline = ArcadeBorder,
    )

private val LightColorScheme =
    lightColorScheme(
        primary = Color(0xFF008744),
        onPrimary = Color.White,
        primaryContainer = Color(0xFFD4F7DD),
        onPrimaryContainer = Color(0xFF00391A),
        secondary = Color(0xFF007799),
        onSecondary = Color.White,
        tertiary = Color(0xFFC2185B),
        background = Color(0xFFF8FAFC),
        onBackground = Color(0xFF0F172A),
        surface = Color(0xFFFFFFFF),
        onSurface = Color(0xFF0F172A),
        surfaceVariant = Color(0xFFE2E8F0),
        onSurfaceVariant = Color(0xFF475569),
        outline = Color(0xFFCBD5E1),
    )

@Composable
fun SuperSnakeGameTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    darkTheme: Boolean =
        when (themeMode) {
            ThemeMode.SYSTEM -> isSystemInDarkTheme()
            ThemeMode.DARK -> true
            ThemeMode.LIGHT -> false
        },
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme =
        when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }

            darkTheme -> DarkColorScheme
            else -> LightColorScheme
        }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
