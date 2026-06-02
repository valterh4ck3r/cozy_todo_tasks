package com.valternegreiros.cozy_todo_task.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
internal data class CozyColorScheme(
    val background: Color,
    val backgroundLight: Color,
    val orange: Color,
    val orangeDark: Color,
    val green: Color,
    val greenDark: Color,
    val textBrown: Color,
    val cardCream: Color,
    val softBorder: Color,
    val red: Color,
    val gold: Color,
    val completed: Color,
    val switchTrack: Color
)

internal val LightCozyColors = CozyColorScheme(
    background = Color(0xFFFFE7B8),
    backgroundLight = Color(0xFFFFF4D8),
    orange = Color(0xFFF6A23A),
    orangeDark = Color(0xFFC96F22),
    green = Color(0xFF5E9D32),
    greenDark = Color(0xFF326B24),
    textBrown = Color(0xFF4A2A12),
    cardCream = Color(0xFFFFF7E6),
    softBorder = Color(0xFFE7B76A),
    red = Color(0xFFE85D2A),
    gold = Color(0xFFF7C64A),
    completed = Color(0xFFE7F4D7),
    switchTrack = Color(0xFFCFE8B7)
)

private val DarkCozyColors = CozyColorScheme(
    background = Color(0xFF26170E),
    backgroundLight = Color(0xFF3A2416),
    orange = Color(0xFFF6A23A),
    orangeDark = Color(0xFFFFBD69),
    green = Color(0xFF8BC85C),
    greenDark = Color(0xFFB7E894),
    textBrown = Color(0xFFFFEFD2),
    cardCream = Color(0xFF332014),
    softBorder = Color(0xFF8F6232),
    red = Color(0xFFFF7A4A),
    gold = Color(0xFFFFD766),
    completed = Color(0xFF263D1F),
    switchTrack = Color(0xFF425D32)
)

internal val LocalCozyColors = staticCompositionLocalOf { LightCozyColors }

internal object CozyTheme {
    val colors: CozyColorScheme
        @Composable get() = LocalCozyColors.current
}

@Composable
internal fun CozyThemeProvider(darkTheme: Boolean, content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalCozyColors provides if (darkTheme) DarkCozyColors else LightCozyColors,
        content = content
    )
}
