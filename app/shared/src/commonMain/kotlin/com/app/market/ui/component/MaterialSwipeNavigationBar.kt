package com.app.market.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitHorizontalTouchSlopOrCancellation
import androidx.compose.foundation.gestures.horizontalDrag
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.input.pointer.isPrimaryPressed
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.math.floor

/** 点击由原生 NavigationBarItem 处理；横向拖动预览目标页签，松手后切页。 */
@Composable
fun MaterialSwipeNavigationBar(
    selectedIndex: Int,
    itemCount: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    windowInsets: WindowInsets = NavigationBarDefaults.windowInsets,
    content: @Composable RowScope.(highlightedIndex: Int) -> Unit,
) {
    require(itemCount > 0)
    val direction = LocalLayoutDirection.current
    var dragPosition by remember(itemCount, direction) { mutableStateOf<Float?>(null) }
    val currentIndex by rememberUpdatedState(selectedIndex)
    val onSelectedUpdated by rememberUpdatedState(onSelected)
    fun indexAt(position: Float) = floor(position + 0.5f).toInt().coerceIn(0, itemCount - 1)
    val position = animateFloatAsState(
        targetValue = dragPosition ?: selectedIndex.toFloat(),
        animationSpec = spring(dampingRatio = 0.83f, stiffness = 480f, visibilityThreshold = 0.005f),
        label = "bottomBarIndicator",
    )
    val indicatorColor = MaterialTheme.colorScheme.secondaryContainer
    NavigationBar(modifier = modifier, containerColor = containerColor, windowInsets = windowInsets) {
        Row(
            Modifier.fillMaxWidth().drawBehind {
                val itemWidth = size.width / itemCount
                val progress = position.value.coerceIn(0f, itemCount - 1f)
                val visualIndex = if (direction == LayoutDirection.Rtl) itemCount - 1f - progress else progress
                val inset = minOf(6.dp.toPx(), itemWidth / 4f)
                val top = 9.dp.toPx()
                val height = (size.height - 2 * top).coerceAtLeast(0f)
                drawRoundRect(
                    color = indicatorColor,
                    topLeft = Offset(itemWidth * visualIndex + inset, top),
                    size = Size(itemWidth - 2 * inset, height),
                    cornerRadius = CornerRadius(height / 2),
                )
            }.pointerInput(itemCount, direction) {
                fun positionAt(x: Float): Float {
                    val width = size.width
                    if (width == 0) return currentIndex.toFloat()
                    val logicalX = if (direction == LayoutDirection.Rtl) width - x else x
                    return (logicalX / width * itemCount - 0.5f).coerceIn(0f, itemCount - 1f)
                }
                awaitEachGesture {
                    try {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        if (down.type == PointerType.Mouse && !currentEvent.buttons.isPrimaryPressed) {
                            return@awaitEachGesture
                        }
                        val start = awaitHorizontalTouchSlopOrCancellation(down.id) { change, _ ->
                            change.consume()
                        }
                        if (start != null) {
                            dragPosition = positionAt(start.position.x)
                            val completed = horizontalDrag(start.id) { change ->
                                change.consume()
                                dragPosition = positionAt(change.position.x)
                            }
                            val target = dragPosition?.let(::indexAt)
                            if (completed && target != null && target != currentIndex) onSelectedUpdated(target)
                        }
                    } finally {
                        // 在单次手势边界清理，覆盖系统取消及其他组件中断。
                        dragPosition = null
                    }
                }
            },
        ) {
            content(dragPosition?.let(::indexAt) ?: selectedIndex)
        }
    }
}
