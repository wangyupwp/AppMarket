package com.app.market.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import com.app.market.domain.model.preference.UiTheme
import com.app.market.domain.model.preference.MaterialAppearance
import com.app.market.domain.model.preference.ThemeMode
import top.yukonga.miuix.kmp.theme.ColorSchemeMode
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.ThemeController

val LocalUiTheme = staticCompositionLocalOf { UiTheme.MIUIX }

@Composable
fun MarketTheme(theme: UiTheme, appearance: MaterialAppearance = MaterialAppearance(), content: @Composable () -> Unit) {
    val controller = remember { ThemeController(ColorSchemeMode.System) }
    val systemDark = isSystemInDarkTheme()
    val dark = when (appearance.mode) {
        ThemeMode.SYSTEM -> systemDark
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    val seed = materialSeedColor(appearance.dynamicColors)
    val materialColors = remember(seed, dark, appearance) { createMaterialColors(seed, dark, appearance) }
    CompositionLocalProvider(LocalUiTheme provides theme) {
        MaterialTheme(colorScheme = materialColors) {
            MiuixTheme(controller = controller) {
                // 自定义页面仍读取 miuix 色彩令牌，将它们映射到同一套 Material 配色。
                val base = if (dark) top.yukonga.miuix.kmp.theme.darkColorScheme() else top.yukonga.miuix.kmp.theme.lightColorScheme()
                val colors = base.copy(
                    error = materialColors.error,
                    onError = materialColors.onError,
                    errorContainer = materialColors.errorContainer,
                    onErrorContainer = materialColors.onErrorContainer,
                    primary = materialColors.primary,
                    onPrimary = materialColors.onPrimary,
                    primaryVariant = materialColors.primaryContainer,
                    onPrimaryVariant = materialColors.onPrimaryContainer,
                    primaryContainer = materialColors.primaryContainer,
                    onPrimaryContainer = materialColors.onPrimaryContainer,
                    secondary = materialColors.secondaryContainer,
                    onSecondary = materialColors.onSecondaryContainer,
                    secondaryVariant = materialColors.surfaceContainerHigh,
                    onSecondaryVariant = materialColors.onSurface,
                    secondaryContainer = materialColors.secondaryContainer,
                    onSecondaryContainer = materialColors.onSecondaryContainer,
                    tertiaryContainer = materialColors.tertiaryContainer,
                    onTertiaryContainer = materialColors.onTertiaryContainer,
                    background = materialColors.background,
                    onBackground = materialColors.onBackground,
                    surface = materialColors.surface,
                    onSurface = materialColors.onSurface,
                    surfaceVariant = materialColors.surfaceVariant,
                    onSurfaceSecondary = materialColors.onSurfaceVariant,
                    onSurfaceVariantSummary = materialColors.onSurfaceVariant,
                    onSurfaceVariantActions = materialColors.onSurfaceVariant,
                    surfaceContainer = materialColors.surfaceContainerHigh,
                    onSurfaceContainer = materialColors.onSurface,
                    onSurfaceContainerVariant = materialColors.onSurfaceVariant,
                    surfaceContainerHigh = materialColors.surfaceContainerHigh,
                    onSurfaceContainerHigh = materialColors.onSurface,
                    surfaceContainerHighest = materialColors.surfaceContainerHighest,
                    onSurfaceContainerHighest = materialColors.onSurface,
                    outline = materialColors.outline,
                    dividerLine = materialColors.outlineVariant,
                )
                val typography = MaterialTheme.typography
                MiuixTheme(
                    colors = if (theme == UiTheme.MATERIAL3) colors else MiuixTheme.colorScheme,
                    textStyles = if (theme == UiTheme.MIUIX) MiuixTheme.textStyles else top.yukonga.miuix.kmp.theme.defaultTextStyles(
                    main = typography.bodyLarge,
                    paragraph = typography.bodyLarge,
                    body1 = typography.bodyLarge,
                    body2 = typography.bodyMedium,
                    footnote1 = typography.bodySmall,
                    footnote2 = typography.labelSmall,
                    headline1 = typography.titleMedium,
                    title1 = typography.headlineLarge,
                    title2 = typography.headlineMedium,
                    title3 = typography.titleLarge,
                    title4 = typography.titleMedium,
                ), content = content)
            }
        }
    }
}
