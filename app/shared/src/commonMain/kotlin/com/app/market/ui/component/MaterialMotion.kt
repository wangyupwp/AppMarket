package com.app.market.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.indication
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import top.yukonga.miuix.kmp.utils.SinkFeedback

internal val materialSink = SinkFeedback(sinkAmount = 0.975f)

internal fun Modifier.materialPressFeedback(source: InteractionSource): Modifier =
    indication(source, materialSink)

internal fun Modifier.clickCursor(enabled: Boolean = true): Modifier =
    if (enabled) pointerHoverIcon(PointerIcon.Hand) else this

@Composable
internal fun Modifier.materialClickable(
    enabled: Boolean = true,
    onClick: () -> Unit,
): Modifier {
    val source = remember { MutableInteractionSource() }
    val hovered = source.collectIsHoveredAsState()
    val hoverColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
    return clickCursor(enabled)
        .drawBehind { if (enabled && hovered.value) drawRect(hoverColor) }
        .clickable(
            interactionSource = source,
            indication = materialSink,
            enabled = enabled,
            onClick = onClick,
        )
}
