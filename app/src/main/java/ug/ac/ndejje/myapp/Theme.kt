package ug.ac.ndejje.myapp

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

enum class AccentColor(val color: Color) {
    GREEN(Color(0xFF4CAF50)),
    BLUE(Color(0xFF2196F3)),
    PURPLE(Color(0xFF9C27B0))
}

val LocalAppTheme = staticCompositionLocalOf { ThemeMode.SYSTEM }
val LocalAccentColor = staticCompositionLocalOf { AccentColor.GREEN }

@Composable
fun FinTrackTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    accentColor: AccentColor = AccentColor.GREEN,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = accentColor.color,
            secondary = accentColor.color.copy(alpha = 0.7f),
            tertiary = Color(0xFF03DAC6)
        )
    } else {
        lightColorScheme(
            primary = accentColor.color,
            secondary = accentColor.color.copy(alpha = 0.7f),
            tertiary = Color(0xFF03DAC6)
        )
    }

    CompositionLocalProvider(
        LocalAppTheme provides themeMode,
        LocalAccentColor provides accentColor
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}
