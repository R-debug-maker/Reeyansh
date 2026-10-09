package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val RoyalTaasColorScheme = darkColorScheme(
    primary = RoyalGold,
    onPrimary = SurfaceDark,
    primaryContainer = GoldContainer,
    onPrimaryContainer = RoyalGoldLight,
    secondary = EmeraldBorder,
    onSecondary = TextPrimary,
    secondaryContainer = SurfaceCardElevated,
    onSecondaryContainer = RoyalGoldLight,
    tertiary = RichBurgundy,
    onTertiary = TextPrimary,
    background = SurfaceDark,
    onBackground = TextPrimary,
    surface = SurfaceCard,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceCardElevated,
    onSurfaceVariant = TextSecondary,
    outline = SurfaceBorder,
    outlineVariant = EmeraldBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Royal Taas is crafted with a luxury dark emerald atmosphere
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = RoyalTaasColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            window?.let {
                it.statusBarColor = SurfaceDark.toArgb()
                it.navigationBarColor = SurfaceDark.toArgb()
                WindowCompat.getInsetsController(it, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(it, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
