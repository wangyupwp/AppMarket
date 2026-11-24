package com.app.market.ui.screen

import androidx.compose.foundation.Canvas
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.market.domain.model.preference.*
import com.app.market.platform.isBlurSettingSupported
import com.app.market.platform.isPredictiveBackSupported
import com.app.market.resources.*
import com.app.market.ui.component.MarketScaffold
import com.app.market.ui.component.PageVerticalPadding
import com.app.market.ui.component.ScaleDialog
import com.app.market.ui.component.materialClickable
import com.app.market.ui.theme.isMaterialDynamicColorSupported
import com.app.market.viewmodel.ThemeSettingsUiState
import com.app.market.viewmodel.ThemeSettingsViewModel
import org.jetbrains.compose.resources.stringResource
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.layout.DialogDefaults
import top.yukonga.miuix.kmp.squircle.squircleSurface

@Composable
internal fun MaterialAppearanceScreen(
    state: ThemeSettingsUiState,
    viewModel: ThemeSettingsViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val appearance = state.materialAppearance
    var picker by rememberSaveable { mutableStateOf<String?>(null) }
    var scaleDialog by rememberSaveable { mutableStateOf(false) }
    val modeNames = listOf(stringResource(Res.string.material_mode_system), stringResource(Res.string.material_mode_light), stringResource(Res.string.material_mode_dark))
    val paletteNames = listOf(Res.string.material_palette_tonal_spot, Res.string.material_palette_neutral, Res.string.material_palette_vibrant, Res.string.material_palette_expressive, Res.string.material_palette_rainbow, Res.string.material_palette_fruit_salad, Res.string.material_palette_monochrome, Res.string.material_palette_fidelity, Res.string.material_palette_content).map { stringResource(it) }
    val specNames = listOf(stringResource(Res.string.material_spec_2021), stringResource(Res.string.material_spec_2025))
    val dynamicSupported = isMaterialDynamicColorSupported()
    val blurSupported = isBlurSettingSupported()

    MarketScaffold(title = stringResource(Res.string.material_theme_settings), onBack = onBack, modifier = modifier) { padding, backdrop, behavior ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().then(backdrop).nestedScroll(behavior.nestedScrollConnection),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp,
                top = padding.calculateTopPadding() + PageVerticalPadding,
                bottom = padding.calculateBottomPadding() + PageVerticalPadding),
        ) {
            item { AppearanceHeading(stringResource(Res.string.theme_style)) }
            item(key = "material-choice") {
                AppearanceRow(stringResource(Res.string.material_google), stringResource(Res.string.material_standard_summary),
                    first = true, selected = true, onClick = { viewModel.setUiTheme(UiTheme.MATERIAL3) })
            }
            item(key = "miuix-choice") {
                AppearanceRow(stringResource(Res.string.theme_style_miuix), stringResource(Res.string.material_miuix_summary),
                    selected = false, onClick = { viewModel.setUiTheme(UiTheme.MIUIX) })
            }
            item(key = "material-floating") {
                AppearanceRow(stringResource(Res.string.theme_floating_bottom_bar), stringResource(Res.string.theme_floating_bottom_bar_summary),
                    last = true, checked = state.enableFloatingBottomBar,
                    onClick = { viewModel.setEnableFloatingBottomBar(!state.enableFloatingBottomBar) })
            }
            item { AppearanceHeading(stringResource(Res.string.material_google), spaced = true) }
            item(key = "material-blur") {
                AppearanceRow(stringResource(Res.string.theme_enable_blur),
                    stringResource(if (blurSupported) Res.string.theme_enable_blur_summary else Res.string.material_blur_unavailable),
                    first = true, icon = 0, checked = state.enableBlur && blurSupported, enabled = blurSupported,
                    onClick = { viewModel.setEnableBlur(!state.enableBlur) })
            }
            item(key = "material-mode") {
                AppearanceRow(stringResource(Res.string.material_mode), modeNames[appearance.mode.ordinal], icon = 1,
                    onClick = { picker = "mode" })
            }
            item(key = "material-palette") {
                AppearanceRow(stringResource(Res.string.material_palette), paletteNames[appearance.palette.ordinal], icon = 2,
                    onClick = { picker = "palette" })
            }
            item(key = "material-spec") {
                AppearanceRow(stringResource(Res.string.material_spec), specNames[appearance.colorSpec.ordinal], icon = 3,
                    onClick = { picker = "spec" })
            }
            item(key = "material-dynamic") {
                AppearanceRow(stringResource(Res.string.material_dynamic),
                    stringResource(if (dynamicSupported) Res.string.material_dynamic_summary else Res.string.material_dynamic_unavailable),
                    last = true, icon = 4, checked = appearance.dynamicColors && dynamicSupported, enabled = dynamicSupported,
                    onClick = { viewModel.setMaterialAppearance(appearance.copy(dynamicColors = !appearance.dynamicColors)) })
            }
            item { AppearanceHeading(stringResource(Res.string.general), spaced = true) }
            item(key = "material-badge") {
                AppearanceRow(stringResource(Res.string.theme_navigation_badge), stringResource(Res.string.theme_navigation_badge_summary),
                    first = true, checked = state.enableNavigationBadge,
                    onClick = { viewModel.setEnableNavigationBadge(!state.enableNavigationBadge) })
            }
            if (isPredictiveBackSupported()) item(key = "material-back") {
                AppearanceRow(stringResource(Res.string.theme_predictive_back), stringResource(Res.string.theme_predictive_back_summary),
                    checked = state.enablePredictiveBack,
                    onClick = { viewModel.setEnablePredictiveBack(!state.enablePredictiveBack) })
            }
            item(key = "material-scale") {
                AppearanceRow(stringResource(Res.string.theme_page_scale), "${(state.pageScale * 100).toInt()}%",
                    last = true, onClick = { scaleDialog = true })
            }
        }
        ScaleDialog(scaleDialog, { scaleDialog = false }, { state.pageScale }, viewModel::setPageScale)
    }

    val pickerTitle = when (picker) {
        "mode" -> stringResource(Res.string.material_mode)
        "palette" -> stringResource(Res.string.material_palette)
        else -> stringResource(Res.string.material_spec)
    }
    val choices = when (picker) { "mode" -> modeNames; "palette" -> paletteNames; else -> specNames }
    val selectedIndex = when (picker) { "mode" -> appearance.mode.ordinal; "palette" -> appearance.palette.ordinal; else -> appearance.colorSpec.ordinal }
    if (picker != null) AlertDialog(
        onDismissRequest = { picker = null },
        shape = RoundedCornerShape(DialogDefaults.CornerRadius),
        title = { Text(pickerTitle) },
        text = {
            LazyColumn {
                itemsIndexed(choices) { index, label ->
                    val enabled = picker != "spec" || index == 0 || appearance.palette.supports2025
                    Row(Modifier.fillMaxWidth().materialClickable(enabled = enabled) {
                        val next = when (picker) {
                            "mode" -> appearance.copy(mode = ThemeMode.entries[index])
                            "palette" -> appearance.copy(palette = MaterialPalette.entries[index])
                            else -> appearance.copy(colorSpec = MaterialColorSpec.entries[index])
                        }
                        viewModel.setMaterialAppearance(next.normalized())
                        picker = null
                    }.padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = index == selectedIndex, onClick = null, enabled = enabled)
                        Column(Modifier.padding(start = 12.dp)) {
                            Text(label, color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (enabled) 1f else 0.38f))
                            if (!enabled) Text(stringResource(Res.string.material_spec_unavailable), style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = { picker = null }) { Text(stringResource(Res.string.cancel)) } },
    )
}

@Composable
private fun AppearanceHeading(title: String, spaced: Boolean = false) {
    Text(title, Modifier.padding(start = 16.dp, top = if (spaced) 28.dp else 4.dp, bottom = 16.dp),
        color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleSmall)
}

@Composable
private fun AppearanceRow(
    title: String,
    summary: String,
    modifier: Modifier = Modifier,
    first: Boolean = false,
    last: Boolean = false,
    icon: Int? = null,
    selected: Boolean? = null,
    checked: Boolean? = null,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    val highlighted = selected == true
    val container by animateColorAsState(
        if (highlighted) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh,
        animationSpec = spring(stiffness = 450f),
        label = "appearanceSelection",
    )
    val topCorner = if (first) CardDefaults.CornerRadius else 4.dp
    val bottomCorner = if (last) CardDefaults.CornerRadius else 4.dp
    CompositionLocalProvider(
        LocalContentColor provides if (highlighted) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface,
    ) {
        Row(modifier.fillMaxWidth().padding(bottom = if (last) 0.dp else 2.dp)
            .squircleSurface(container, topCorner, topCorner, bottomCorner, bottomCorner)
            .materialClickable(enabled = enabled, onClick = onClick).padding(horizontal = 16.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            if (icon != null) AppearanceIcon(icon)
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp))
                Text(summary, style = MaterialTheme.typography.bodyMedium,
                    color = if (highlighted) MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.75f) else MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (selected != null) RadioButton(selected, onClick = null)
            if (checked != null) Switch(checked, onCheckedChange = null, enabled = enabled,
                thumbContent = if (checked) ({ Text("✓", fontSize = 18.sp) }) else null)
        }
    }
}

@Composable
private fun AppearanceIcon(kind: Int) {
    val color = MaterialTheme.colorScheme.onSurfaceVariant
    val background = MaterialTheme.colorScheme.surfaceContainerHigh
    Canvas(Modifier.size(24.dp)) {
        val unit = size.width / 24f
        when (kind) {
            0 -> for (x in 0..4) for (y in 0..4) {
                val distance = kotlin.math.abs(x - 2) + kotlin.math.abs(y - 2)
                drawCircle(color.copy(alpha = (1f - distance * 0.18f)), unit * (1.5f - distance * 0.2f), Offset((4 + x * 4) * unit, (4 + y * 4) * unit))
            }
            1 -> {
                drawCircle(color, 10 * unit)
                drawCircle(background, 9 * unit, Offset(17 * unit, 7 * unit))
            }
            2 -> {
                drawRoundRect(color, Offset(6 * unit, 2 * unit), androidx.compose.ui.geometry.Size(14 * unit, 20 * unit),
                    androidx.compose.ui.geometry.CornerRadius(2 * unit), style = Stroke(2 * unit))
                drawCircle(color, 1.5f * unit, Offset(13 * unit, 7 * unit))
                drawLine(color, Offset(2 * unit, 7 * unit), Offset(4 * unit, 22 * unit), 2 * unit)
            }
            3 -> {
                drawLine(color, Offset(3 * unit, 3 * unit), Offset(21 * unit, 21 * unit), 3 * unit)
                drawLine(color, Offset(3 * unit, 21 * unit), Offset(21 * unit, 3 * unit), 3 * unit)
                drawCircle(color, 4 * unit, Offset(6 * unit, 5 * unit), style = Stroke(2 * unit))
            }
            else -> {
                drawCircle(color, 10 * unit, style = Stroke(2 * unit))
                drawArc(color, 90f, 180f, useCenter = true, topLeft = Offset(2 * unit, 2 * unit), size = androidx.compose.ui.geometry.Size(20 * unit, 20 * unit))
            }
        }
    }
}
