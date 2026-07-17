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

/** Los temas seleccionables (paleta + tipografía). */
enum class ThemeId(val displayName: String, val fontLabel: String) {
    DARK("Dark", "Verde / óxido"),
    MATRIX("Matrix", "Mono terminal"),
    STEALTH("Stealth", "Grises fríos"),
    CUTE("Cute", "Rosa suave"),
    PAPER("Paper", "Serif · crema y tinta"),
    NEON("Neon Arcade", "Morado y cian"),
    SEPIA("Sepia", "Bajo estímulo"),
    BLUEPRINT("Blueprint", "Mono técnico"),
    OCEAN("Midnight Ocean", "Azul marino y teal"),
    EMBER("Ember", "Carbón y ámbar"),
    NOIR("Noir", "Blanco y negro puro"),
    DRACULA("Dracula", "Púrpura, rosa y verde"),
    BLOODMOON("Bloodmoon", "Carmesí y oro"),
    COBALT("Cobalt", "Azul eléctrico"),
    OBSIDIAN("Obsidian", "Negro y oro"),
    FUCHSIA("Fuchsia", "Fucsia y violeta"),
    VIRIDIAN("Viridian", "Esmeralda y lima");

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

private val PaperColors = AppColors(
    isDark = false,
    bg = Color(0xFFF3EDE0),
    surface = Color(0xFFFAF6EC),
    surfaceAlt = Color(0xFFE8DFCC),
    onBg = Color(0xFF2B2620),
    muted = Color(0xFF8A7F68),
    line = Color(0xFFE0D5BD),
    accent = Color(0xFFA33B2E),
    accentSoft = Color(0x22A33B2E),
    onAccent = Color(0xFFFAF6EC),
    meta = Color(0xFF6B5A42),
    done = Color(0xFF5E7A4E),
    pillBg = Color(0xFFE8DFCC),
    swatches = listOf(Color(0xFFF3EDE0), Color(0xFFA33B2E), Color(0xFF6B5A42)),
)

private val NeonColors = AppColors(
    isDark = true,
    bg = Color(0xFF1A0B2E),
    surface = Color(0xFF241141),
    surfaceAlt = Color(0xFF2A1650),
    onBg = Color(0xFFF0E6FF),
    muted = Color(0xFF8A6BB0),
    line = Color(0xFF3A2060),
    accent = Color(0xFF00E5FF),
    accentSoft = Color(0x2200E5FF),
    onAccent = Color(0xFF1A0B2E),
    meta = Color(0xFFFF2FD0),
    done = Color(0xFF00E5FF),
    pillBg = Color(0xFF140823),
    swatches = listOf(Color(0xFF1A0B2E), Color(0xFF00E5FF), Color(0xFFFF2FD0)),
)

private val SepiaColors = AppColors(
    isDark = false,
    bg = Color(0xFFEFE9DF),
    surface = Color(0xFFF7F4EE),
    surfaceAlt = Color(0xFFE3DDD0),
    onBg = Color(0xFF4A4238),
    muted = Color(0xFFA39A8C),
    line = Color(0xFFDDD5C5),
    accent = Color(0xFF7A8768),
    accentSoft = Color(0x227A8768),
    onAccent = Color(0xFFEFE9DF),
    meta = Color(0xFF9A7B5A),
    done = Color(0xFF7A8768),
    pillBg = Color(0xFFE3DDD0),
    swatches = listOf(Color(0xFFEFE9DF), Color(0xFF7A8768), Color(0xFFA39A8C)),
)

private val BlueprintColors = AppColors(
    isDark = false,
    bg = Color(0xFFFFFFFF),
    surface = Color(0xFFFFFFFF),
    surfaceAlt = Color(0xFFF4F8FC),
    onBg = Color(0xFF0F2A4A),
    muted = Color(0xFF6D90B8),
    line = Color(0xFFCFE0F0),
    accent = Color(0xFF1565C0),
    accentSoft = Color(0x221565C0),
    onAccent = Color(0xFFFFFFFF),
    meta = Color(0xFF1565C0),
    done = Color(0xFF2E7D32),
    pillBg = Color(0xFFF4F8FC),
    swatches = listOf(Color(0xFFFFFFFF), Color(0xFF1565C0), Color(0xFFCFE0F0)),
)

private val OceanColors = AppColors(
    isDark = true,
    bg = Color(0xFF071A2B),
    surface = Color(0xFF0F2740),
    surfaceAlt = Color(0xFF123049),
    onBg = Color(0xFFE6F3F7),
    muted = Color(0xFF5A8AA0),
    line = Color(0xFF1E3A52),
    accent = Color(0xFF2DD4BF),
    accentSoft = Color(0x222DD4BF),
    onAccent = Color(0xFF071A2B),
    meta = Color(0xFF5AB0C9),
    done = Color(0xFF2DD4BF),
    pillBg = Color(0xFF0A2138),
    swatches = listOf(Color(0xFF071A2B), Color(0xFF2DD4BF), Color(0xFF1E3A52)),
)

private val EmberColors = AppColors(
    isDark = true,
    bg = Color(0xFF1A1614),
    surface = Color(0xFF241E1A),
    surfaceAlt = Color(0xFF2E2621),
    onBg = Color(0xFFF0E6DC),
    muted = Color(0xFF7A6E64),
    line = Color(0xFF3A322C),
    accent = Color(0xFFFF8A3D),
    accentSoft = Color(0x22FF8A3D),
    onAccent = Color(0xFF1A1614),
    meta = Color(0xFFE0A050),
    done = Color(0xFFFF8A3D),
    pillBg = Color(0xFF161210),
    swatches = listOf(Color(0xFF1A1614), Color(0xFFFF8A3D), Color(0xFF3A322C)),
)

private val NoirColors = AppColors(
    isDark = true,
    bg = Color(0xFF0A0A0A),
    surface = Color(0xFF141414),
    surfaceAlt = Color(0xFF1E1E1E),
    onBg = Color(0xFFE8E8E8),
    muted = Color(0xFF6B6B6B),
    line = Color(0xFF2A2A2A),
    accent = Color(0xFFFFFFFF),
    accentSoft = Color(0x22FFFFFF),
    onAccent = Color(0xFF0A0A0A),
    meta = Color(0xFFBFBFBF),
    done = Color(0xFFFFFFFF),
    pillBg = Color(0xFF000000),
    swatches = listOf(Color(0xFF0A0A0A), Color(0xFFFFFFFF), Color(0xFF6B6B6B)),
)

private val DraculaColors = AppColors(
    isDark = true,
    bg = Color(0xFF282A36),
    surface = Color(0xFF343746),
    surfaceAlt = Color(0xFF3C4051),
    onBg = Color(0xFFF8F8F2),
    muted = Color(0xFF6272A4),
    line = Color(0xFF44475A),
    accent = Color(0xFFBD93F9),
    accentSoft = Color(0x22BD93F9),
    onAccent = Color(0xFF1A1B24),
    meta = Color(0xFFFF79C6),
    done = Color(0xFF50FA7B),
    pillBg = Color(0xFF1E1F29),
    swatches = listOf(Color(0xFF282A36), Color(0xFFBD93F9), Color(0xFFFF79C6)),
)

private val BloodmoonColors = AppColors(
    isDark = true,
    bg = Color(0xFF140A0D),
    surface = Color(0xFF201016),
    surfaceAlt = Color(0xFF2B1620),
    onBg = Color(0xFFF3E3E7),
    muted = Color(0xFF9E6B78),
    line = Color(0xFF38202A),
    accent = Color(0xFFE23E57),
    accentSoft = Color(0x22E23E57),
    onAccent = Color(0xFF140A0D),
    meta = Color(0xFFE8B04B),
    done = Color(0xFFE23E57),
    pillBg = Color(0xFF0C0508),
    swatches = listOf(Color(0xFF140A0D), Color(0xFFE23E57), Color(0xFFE8B04B)),
)

private val CobaltColors = AppColors(
    isDark = true,
    bg = Color(0xFF060B16),
    surface = Color(0xFF0E1626),
    surfaceAlt = Color(0xFF152036),
    onBg = Color(0xFFE6EEFB),
    muted = Color(0xFF5A6E8C),
    line = Color(0xFF1E2C44),
    accent = Color(0xFF3B82F6),
    accentSoft = Color(0x223B82F6),
    onAccent = Color(0xFF060B16),
    meta = Color(0xFF60A5FA),
    done = Color(0xFF3B82F6),
    pillBg = Color(0xFF030711),
    swatches = listOf(Color(0xFF060B16), Color(0xFF3B82F6), Color(0xFF60A5FA)),
)

private val ObsidianColors = AppColors(
    isDark = true,
    bg = Color(0xFF0B0B0D),
    surface = Color(0xFF151517),
    surfaceAlt = Color(0xFF1D1D20),
    onBg = Color(0xFFEDE9E0),
    muted = Color(0xFF7C766A),
    line = Color(0xFF2A2A2E),
    accent = Color(0xFFE8B923),
    accentSoft = Color(0x22E8B923),
    onAccent = Color(0xFF0B0B0D),
    meta = Color(0xFFC7873A),
    done = Color(0xFFE8B923),
    pillBg = Color(0xFF050506),
    swatches = listOf(Color(0xFF0B0B0D), Color(0xFFE8B923), Color(0xFFC7873A)),
)

private val FuchsiaColors = AppColors(
    isDark = true,
    bg = Color(0xFF160718),
    surface = Color(0xFF221029),
    surfaceAlt = Color(0xFF2D1636),
    onBg = Color(0xFFF6E4F5),
    muted = Color(0xFFA06BA8),
    line = Color(0xFF3A1E42),
    accent = Color(0xFFEC4899),
    accentSoft = Color(0x22EC4899),
    onAccent = Color(0xFF160718),
    meta = Color(0xFFA855F7),
    done = Color(0xFFEC4899),
    pillBg = Color(0xFF0E040F),
    swatches = listOf(Color(0xFF160718), Color(0xFFEC4899), Color(0xFFA855F7)),
)

private val ViridianColors = AppColors(
    isDark = true,
    bg = Color(0xFF06120E),
    surface = Color(0xFF0E1F18),
    surfaceAlt = Color(0xFF152B21),
    onBg = Color(0xFFE2F5EC),
    muted = Color(0xFF5C8A75),
    line = Color(0xFF1D3A2E),
    accent = Color(0xFF10B981),
    accentSoft = Color(0x2210B981),
    onAccent = Color(0xFF06120E),
    meta = Color(0xFFA3E635),
    done = Color(0xFF10B981),
    pillBg = Color(0xFF030B08),
    swatches = listOf(Color(0xFF06120E), Color(0xFF10B981), Color(0xFFA3E635)),
)

fun colorsFor(id: ThemeId): AppColors = when (id) {
    ThemeId.DARK -> DarkColors
    ThemeId.MATRIX -> MatrixColors
    ThemeId.STEALTH -> StealthColors
    ThemeId.CUTE -> CuteColors
    ThemeId.PAPER -> PaperColors
    ThemeId.NEON -> NeonColors
    ThemeId.SEPIA -> SepiaColors
    ThemeId.BLUEPRINT -> BlueprintColors
    ThemeId.OCEAN -> OceanColors
    ThemeId.EMBER -> EmberColors
    ThemeId.NOIR -> NoirColors
    ThemeId.DRACULA -> DraculaColors
    ThemeId.BLOODMOON -> BloodmoonColors
    ThemeId.COBALT -> CobaltColors
    ThemeId.OBSIDIAN -> ObsidianColors
    ThemeId.FUCHSIA -> FuchsiaColors
    ThemeId.VIRIDIAN -> ViridianColors
}

fun fontFor(id: ThemeId): FontFamily = when (id) {
    ThemeId.MATRIX, ThemeId.BLUEPRINT -> FontFamily.Monospace
    ThemeId.PAPER -> FontFamily.Serif
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
