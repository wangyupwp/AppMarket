package com.app.market.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.app.market.domain.model.preference.MaterialAppearance
import com.app.market.domain.model.preference.MaterialColorSpec
import com.app.market.domain.model.preference.MaterialPalette
import com.materialkolor.dynamiccolor.ColorSpec
import com.materialkolor.hct.Hct
import com.materialkolor.scheme.*

@Composable
internal expect fun materialSeedColor(dynamic: Boolean): Color

internal expect fun isMaterialDynamicColorSupported(): Boolean

internal fun createMaterialColors(seed: Color, dark: Boolean, appearance: MaterialAppearance): ColorScheme {
    val hct = Hct.fromInt(seed.toArgb())
    val spec = if (appearance.normalized().colorSpec == MaterialColorSpec.EXPRESSIVE_2025)
        ColorSpec.SpecVersion.SPEC_2025 else ColorSpec.SpecVersion.SPEC_2021
    val scheme: DynamicScheme = when (appearance.palette) {
        MaterialPalette.TONAL_SPOT -> SchemeTonalSpot(hct, dark, 0.0, specVersion = spec)
        MaterialPalette.NEUTRAL -> SchemeNeutral(hct, dark, 0.0, specVersion = spec)
        MaterialPalette.VIBRANT -> SchemeVibrant(hct, dark, 0.0, specVersion = spec)
        MaterialPalette.EXPRESSIVE -> SchemeExpressive(hct, dark, 0.0, specVersion = spec)
        MaterialPalette.RAINBOW -> SchemeRainbow(hct, dark, 0.0, specVersion = spec)
        MaterialPalette.FRUIT_SALAD -> SchemeFruitSalad(hct, dark, 0.0, specVersion = spec)
        MaterialPalette.MONOCHROME -> SchemeMonochrome(hct, dark, 0.0, specVersion = spec)
        MaterialPalette.FIDELITY -> SchemeFidelity(hct, dark, 0.0, specVersion = spec)
        MaterialPalette.CONTENT -> SchemeContent(hct, dark, 0.0, specVersion = spec)
    }
    val base = if (dark) darkColorScheme() else lightColorScheme()
    return base.copy(
        primary = Color(scheme.primary), onPrimary = Color(scheme.onPrimary),
        primaryContainer = Color(scheme.primaryContainer), onPrimaryContainer = Color(scheme.onPrimaryContainer),
        inversePrimary = Color(scheme.inversePrimary),
        secondary = Color(scheme.secondary), onSecondary = Color(scheme.onSecondary),
        secondaryContainer = Color(scheme.secondaryContainer), onSecondaryContainer = Color(scheme.onSecondaryContainer),
        tertiary = Color(scheme.tertiary), onTertiary = Color(scheme.onTertiary),
        tertiaryContainer = Color(scheme.tertiaryContainer), onTertiaryContainer = Color(scheme.onTertiaryContainer),
        background = Color(scheme.background), onBackground = Color(scheme.onBackground),
        surface = Color(scheme.surface), onSurface = Color(scheme.onSurface),
        surfaceVariant = Color(scheme.surfaceVariant), onSurfaceVariant = Color(scheme.onSurfaceVariant),
        surfaceDim = Color(scheme.surfaceDim), surfaceBright = Color(scheme.surfaceBright),
        surfaceContainerLowest = Color(scheme.surfaceContainerLowest), surfaceContainerLow = Color(scheme.surfaceContainerLow),
        surfaceContainer = Color(scheme.surfaceContainer), surfaceContainerHigh = Color(scheme.surfaceContainerHigh),
        surfaceContainerHighest = Color(scheme.surfaceContainerHighest),
        inverseSurface = Color(scheme.inverseSurface), inverseOnSurface = Color(scheme.inverseOnSurface),
        outline = Color(scheme.outline), outlineVariant = Color(scheme.outlineVariant),
        error = Color(scheme.error), onError = Color(scheme.onError),
        errorContainer = Color(scheme.errorContainer), onErrorContainer = Color(scheme.onErrorContainer),
        scrim = Color(scheme.scrim), surfaceTint = Color(scheme.primary),
    )
}
