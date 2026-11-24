package com.app.market.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.app.market.domain.model.preference.MaterialAppearance
import com.app.market.domain.model.preference.MaterialPalette
import com.app.market.domain.model.preference.MaterialColorSpec
import kotlin.test.Test
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class MaterialColorsTest {
    @Test
    fun paletteSpecificationAndSeedActuallyChangeTheGeneratedColors() {
        val settings = MaterialAppearance(dynamicColors = false)
        val blue = Color(0xFF52678E)
        val base = createMaterialColors(blue, false, settings)
        val expressive = createMaterialColors(blue, false, settings.copy(palette = MaterialPalette.EXPRESSIVE))
        val standard = createMaterialColors(blue, false, settings.copy(colorSpec = MaterialColorSpec.STANDARD_2021))
        val wallpaper = createMaterialColors(Color(0xFF388754), false, settings)
        assertNotEquals(base.primary, expressive.primary)
        assertNotEquals(base.primaryContainer, standard.primaryContainer)
        assertNotEquals(base.primary, wallpaper.primary)
    }

    @Test
    fun allPalettesProduceReadableLightAndDarkSurfaces() {
        for (palette in MaterialPalette.entries) {
            val appearance = MaterialAppearance(palette = palette)
            val light = createMaterialColors(Color(0xFF52678E), false, appearance)
            val dark = createMaterialColors(Color(0xFF52678E), true, appearance)
            assertTrue(light.surface.luminance() > dark.surface.luminance())
            assertTrue(light.onSurface.luminance() < light.surface.luminance())
            assertTrue(dark.onSurface.luminance() > dark.surface.luminance())
        }
    }
}
