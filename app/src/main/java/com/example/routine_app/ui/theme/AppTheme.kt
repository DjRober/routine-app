package com.example.routine_app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily

/** Los cuatro temas seleccionables (paleta + tipografía). */
enum class ThemeId(val displayName: String, val fontLabel: String) {
    DARK("Dark", "Verde / óxido"),
    MATRIX("Matrix", "Mono"),
    STEALTH("Stealth", "Neutro"),
    CUTE("Cute", "Rosa");

    companion object {
        fun from(name: String?): ThemeId =
            entries.firstOrNull { it.name == name } ?: DARK
    }
}

/** Paleta de colores personalizada de la app (más rica que el ColorScheme de Material). */
@Immutable
data class AppColors(
    val isDark: Boolean,
    val bg: Color,
    val surface: Color,
    val surfaceAlt: Color,
    val onBg: Color,
    val muted: Color,
    val line: Color,
    val accent: Color,
    val accentSoft: Color,
    val onAccent: Color,
    val meta: Color,
    val done: Color,
    val pillBg: Color,
    val swatches: List<Color>,
)

private val DarkColors = AppColors(
    isDark = true,
    bg = Color(0xFF181B1E),
    surface = Color(0xFF212429),
    surfaceAlt = Color(0xFF2A2E34),
    onBg = Color(0xFFECEAE4),
    muted = Color(0xFF9A968D),
    line = Color(0xFF343A42),
    accent = Color(0xFF8BA653),
    accentSoft = Color(0x268BA653),
    onAccent = Color(0xFF12140F),
    meta = Color(0xFFC57A4E),
    done = Color(0xFF8BA653),
    pillBg = Color(0xFF0F1113),
    swatches = listOf(Color(0xFF181B1E), Color(0xFF8BA653), Color(0xFFA8623A)),
)

private val MatrixColors = AppColors(
    isDark = true,
    bg = Color(0xFF000000),
    surface = Color(0xFF071007),
    surfaceAlt = Color(0xFF0C1A0C),
    onBg = Color(0xFF00FF41),
    muted = Color(0xFF12A82B),
    line = Color(0xFF00330E),
    accent = Color(0xFF00FF41),
    accentSoft = Color(0x2200FF41),
    onAccent = Color(0xFF001A00),
    meta = Color(0xFFB5FF3D),
    done = Color(0xFF00FF41),
    pillBg = Color(0xFF001100),
    swatches = listOf(Color(0xFF000000), Color(0xFF00FF41), Color(0xFF003B00)),
)

private val StealthColors = AppColors(
    isDark = true,
    bg = Color(0xFF0D1117),
    surface = Color(0xFF161B22),
    surfaceAlt = Color(0xFF1C2330),
    onBg = Color(0xFFC9D1D9),
    muted = Color(0xFF7A8BA0),
    line = Color(0xFF30363D),
    accent = Color(0xFF7A8BA0),
    accentSoft = Color(0x267A8BA0),
    onAccent = Color(0xFF0D1117),
    meta = Color(0xFFB08968),
    done = Color(0xFF6FB08A),
    pillBg = Color(0xFF090C10),
    swatches = listOf(Color(0xFF0D1117), Color(0xFF4A5568), Color(0xFF7A8BA0)),
)

private val CuteColors = AppColors(
    isDark = false,
    bg = Color(0xFFFFF2F7),
    surface = Color(0xFFFFFFFF),
    surfaceAlt = Color(0xFFFFE9F1),
    onBg = Color(0xFF4A2B38),
    muted = Color(0xFFB98BA0),
    line = Color(0xFFFAD3E4),
    accent = Color(0xFFFF8FB3),
    accentSoft = Color(0x33FF8FB3),
    onAccent = Color(0xFFFFFFFF),
    meta = Color(0xFFE86A9A),
    done = Color(0xFF7FB77E),
    pillBg = Color(0xFFFFE0EC),
    swatches = listOf(Color(0xFFFFD6E8), Color(0xFFFF8FB3), Color(0xFFFFF2F7)),
)

fun colorsFor(id: ThemeId): AppColors = when (id) {
    ThemeId.DARK -> DarkColors
    ThemeId.MATRIX -> MatrixColors
    ThemeId.STEALTH -> StealthColors
    ThemeId.CUTE -> CuteColors
}

fun fontFor(id: ThemeId): FontFamily = when (id) {
    ThemeId.MATRIX -> FontFamily.Monospace
    else -> FontFamily.SansSerif
}

val LocalAppColors = staticCompositionLocalOf { DarkColors }

private fun materialScheme(c: AppColors) =
    if (c.isDark) darkColorScheme(
        primary = c.accent, onPrimary = c.onAccent,
        secondary = c.meta, tertiary = c.done,
        background = c.bg, onBackground = c.onBg,
        surface = c.surface, onSurface = c.onBg,
        surfaceVariant = c.surfaceAlt, onSurfaceVariant = c.muted,
        outline = c.line, outlineVariant = c.line,
    ) else lightColorScheme(
        primary = c.accent, onPrimary = c.onAccent,
        secondary = c.meta, tertiary = c.done,
        background = c.bg, onBackground = c.onBg,
        surface = c.surface, onSurface = c.onBg,
        surfaceVariant = c.surfaceAlt, onSurfaceVariant = c.muted,
        outline = c.line, outlineVariant = c.line,
    )

/** Tema raíz de la app. Aplica la paleta y la tipografía del [themeId] elegido. */
@Composable
fun RoutineappTheme(
    themeId: ThemeId = ThemeId.DARK,
    content: @Composable () -> Unit,
) {
    val colors = colorsFor(themeId)
    val font = fontFor(themeId)
    val base = Typography()
    val typography = base.copy(
        displayLarge = base.displayLarge.copy(fontFamily = font),
        displayMedium = base.displayMedium.copy(fontFamily = font),
        displaySmall = base.displaySmall.copy(fontFamily = font),
        headlineLarge = base.headlineLarge.copy(fontFamily = font),
        headlineMedium = base.headlineMedium.copy(fontFamily = font),
        headlineSmall = base.headlineSmall.copy(fontFamily = font),
        titleLarge = base.titleLarge.copy(fontFamily = font),
        titleMedium = base.titleMedium.copy(fontFamily = font),
        titleSmall = base.titleSmall.copy(fontFamily = font),
        bodyLarge = base.bodyLarge.copy(fontFamily = font),
        bodyMedium = base.bodyMedium.copy(fontFamily = font),
        bodySmall = base.bodySmall.copy(fontFamily = font),
        labelLarge = base.labelLarge.copy(fontFamily = font),
        labelMedium = base.labelMedium.copy(fontFamily = font),
        labelSmall = base.labelSmall.copy(fontFamily = font),
    )

    CompositionLocalProvider(LocalAppColors provides colors) {
        MaterialTheme(
            colorScheme = materialScheme(colors),
            typography = typography,
            content = content,
        )
    }
}
