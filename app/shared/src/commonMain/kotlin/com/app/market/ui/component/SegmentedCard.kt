package com.app.market.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.squircle.squircleSurface
import top.yukonga.miuix.kmp.theme.LocalContentColor

/**
 * One slice of a visual card that has been split into independent lazy items: the first
 * segment carries the top corners, the last one the bottom corners, and middle segments a
 * plain background, so consecutive items read as a single [top.yukonga.miuix.kmp.basic.Card].
 * Splitting keeps composition/measure incremental per row instead of one big card item.
 *
 * Uses [squircleSurface] (not `squircleBackground`) so full-row click indication is masked
 * to the rounded corners, matching how content is clipped inside a real Card.
 */
@Composable
fun CardSegmentContainer(
    isFirst: Boolean,
    isLast: Boolean,
    modifier: Modifier = Modifier,
    horizontalPadding: Dp = 12.dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = CardDefaults.defaultColors()
    val segmented = useMaterial3()
    val innerCorner = if (segmented) 4.dp else 0.dp
    val topCorner = if (isFirst) CardDefaults.CornerRadius else innerCorner
    val bottomCorner = if (isLast) CardDefaults.CornerRadius else innerCorner
    val surfaceModifier = if (topCorner == 0.dp && bottomCorner == 0.dp) {
        Modifier.background(colors.color)
    } else {
        Modifier.squircleSurface(colors.color, topCorner, topCorner, bottomCorner, bottomCorner)
    }
    CompositionLocalProvider(LocalContentColor provides colors.contentColor) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = horizontalPadding)
                .padding(bottom = if (segmented && !isLast) 2.dp else 0.dp)
                .then(surfaceModifier),
            content = content,
        )
    }
}
