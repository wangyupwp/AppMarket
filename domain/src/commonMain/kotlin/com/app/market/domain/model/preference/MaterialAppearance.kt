package com.app.market.domain.model.preference

enum class ThemeMode { SYSTEM, LIGHT, DARK }
enum class MaterialPalette(val supports2025: Boolean = false) {
    TONAL_SPOT(true), NEUTRAL(true), VIBRANT(true), EXPRESSIVE(true),
    RAINBOW, FRUIT_SALAD, MONOCHROME, FIDELITY, CONTENT,
}
enum class MaterialColorSpec { STANDARD_2021, EXPRESSIVE_2025 }

data class MaterialAppearance(
    val mode: ThemeMode = ThemeMode.SYSTEM,
    val palette: MaterialPalette = MaterialPalette.TONAL_SPOT,
    val colorSpec: MaterialColorSpec = MaterialColorSpec.EXPRESSIVE_2025,
    val dynamicColors: Boolean = true,
) {
    fun normalized(): MaterialAppearance =
        if (!palette.supports2025 && colorSpec == MaterialColorSpec.EXPRESSIVE_2025)
            copy(colorSpec = MaterialColorSpec.STANDARD_2021) else this

    fun encode(): String = listOf(mode.name, palette.name, colorSpec.name, dynamicColors.toString()).joinToString("|")

    companion object {
        fun decode(value: String?): MaterialAppearance {
            val parts = value.orEmpty().split('|')
            return MaterialAppearance(
                mode = ThemeMode.entries.firstOrNull { it.name == parts.getOrNull(0) } ?: ThemeMode.SYSTEM,
                palette = MaterialPalette.entries.firstOrNull { it.name == parts.getOrNull(1) } ?: MaterialPalette.TONAL_SPOT,
                colorSpec = MaterialColorSpec.entries.firstOrNull { it.name == parts.getOrNull(2) } ?: MaterialColorSpec.EXPRESSIVE_2025,
                dynamicColors = parts.getOrNull(3)?.toBooleanStrictOrNull() ?: true,
            ).normalized()
        }
    }
}
