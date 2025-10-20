package com.example.lab8.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Colores
private val Primary = Color(0xFF6200EE)
private val PrimaryLight = Color(0xFF7C4DFF)
private val Secondary = Color(0xFF03DAC6)
private val Error = Color(0xFFB00020)
private val Background = Color(0xFFFFFFFF)
private val Surface = Color(0xFFFFFFFF)

private val LightColors = lightColorScheme(
    primary = Primary,
    secondary = Secondary,
    error = Error,
    background = Background,
    surface = Surface
)

private val DarkColors = darkColorScheme(
    primary = PrimaryLight,
    secondary = Secondary,
    error = Error,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E)
)

private val AppTypography = Typography(
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp
    )
)

@Composable
fun AppTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        typography = AppTypography,
        content = content
    )
}