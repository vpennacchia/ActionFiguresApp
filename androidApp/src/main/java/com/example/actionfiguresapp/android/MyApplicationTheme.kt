package com.example.actionfiguresapp.android

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Neon palette
val NeonCyan    = Color(0xFF00E5FF)
val NeonPurple  = Color(0xFFBF5FFF)
val NeonGold    = Color(0xFFFFD700)
val NeonGreen   = Color(0xFF39FF14)
val NeonPink    = Color(0xFFFF2D78)

// Dark backgrounds
val SpaceBlack  = Color(0xFF080C14)
val DarkPanel   = Color(0xFF0F1624)
val DarkPanel2  = Color(0xFF162236)
val GridLine    = Color(0xFF1A2E4A)

// Text
val TextPrimary   = Color(0xFFE8F4FD)
val TextSecondary = Color(0xFF7A9CC0)

private val NerdColorScheme = darkColorScheme(
    primary          = NeonCyan,
    onPrimary        = SpaceBlack,
    primaryContainer = Color(0xFF003D52),
    onPrimaryContainer = NeonCyan,
    secondary        = NeonPurple,
    onSecondary      = Color.White,
    tertiary         = NeonGold,
    onTertiary       = SpaceBlack,
    background       = SpaceBlack,
    onBackground     = TextPrimary,
    surface          = DarkPanel,
    onSurface        = TextPrimary,
    surfaceVariant   = DarkPanel2,
    onSurfaceVariant = TextSecondary,
    error            = NeonPink,
    outline          = GridLine
)

private val NerdShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small      = RoundedCornerShape(6.dp),
    medium     = RoundedCornerShape(8.dp),
    large      = RoundedCornerShape(12.dp),
    extraLarge = RoundedCornerShape(16.dp)
)

private val NerdTypography = Typography(
    headlineLarge  = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold,   fontSize = 28.sp, letterSpacing = 2.sp),
    headlineMedium = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold,   fontSize = 22.sp, letterSpacing = 1.5.sp),
    headlineSmall  = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold,   fontSize = 18.sp, letterSpacing = 1.sp),
    titleLarge     = TextStyle(fontFamily = FontFamily.Default,   fontWeight = FontWeight.Bold,   fontSize = 20.sp, letterSpacing = 0.5.sp),
    titleMedium    = TextStyle(fontFamily = FontFamily.Default,   fontWeight = FontWeight.SemiBold, fontSize = 16.sp),
    bodyLarge      = TextStyle(fontFamily = FontFamily.Default,   fontWeight = FontWeight.Normal, fontSize = 16.sp),
    bodyMedium     = TextStyle(fontFamily = FontFamily.Default,   fontWeight = FontWeight.Normal, fontSize = 14.sp),
    bodySmall      = TextStyle(fontFamily = FontFamily.Default,   fontWeight = FontWeight.Normal, fontSize = 12.sp),
    labelLarge     = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Medium, fontSize = 13.sp, letterSpacing = 1.sp),
    labelSmall     = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Normal, fontSize = 10.sp, letterSpacing = 0.8.sp)
)

@Composable
fun MyApplicationTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = NerdColorScheme,
        typography  = NerdTypography,
        shapes      = NerdShapes,
        content     = content
    )
}
