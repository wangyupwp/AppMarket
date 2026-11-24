@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.app.market.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.*
import com.app.market.domain.model.preference.UiTheme
import com.app.market.ui.theme.LocalUiTheme
import top.yukonga.miuix.kmp.basic.CardColors
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.IconButtonDefaults
import top.yukonga.miuix.kmp.basic.PopupPositionProvider
import top.yukonga.miuix.kmp.basic.TextFieldDefaults
import top.yukonga.miuix.kmp.squircle.squircleSurface
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.preference.RadioButtonLocation
import top.yukonga.miuix.kmp.layout.BottomSheetDefaults
import top.yukonga.miuix.kmp.layout.DialogDefaults
import kotlin.math.roundToInt

@Composable
internal fun useMaterial3() = LocalUiTheme.current == UiTheme.MATERIAL3

internal val LocalMaterialAppBarBehavior = staticCompositionLocalOf<androidx.compose.material3.TopAppBarScrollBehavior?> { null }

@Composable
fun Card(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = CardDefaults.CornerRadius,
    insideMargin: PaddingValues = CardDefaults.InsideMargin,
    colors: CardColors = CardDefaults.defaultColors(),
    onClick: (() -> Unit)? = null,
    onLongPress: (() -> Unit)? = null,
    showIndication: Boolean = false,
    holdDownState: Boolean = false,
    content: @Composable ColumnScope.() -> Unit,
) {
    if (useMaterial3()) {
        val clickModifier = if (onClick != null || onLongPress != null) Modifier.clickCursor().combinedClickable(
            interactionSource = null,
            indication = if (showIndication) LocalIndication.current else materialSink,
            onClick = { onClick?.invoke() },
            onLongClick = onLongPress,
        ) else Modifier
        CompositionLocalProvider(
            androidx.compose.material3.LocalContentColor provides colors.contentColor,
            top.yukonga.miuix.kmp.theme.LocalContentColor provides colors.contentColor,
        ) {
            Column(modifier.squircleSurface(colors.color, cornerRadius).then(clickModifier)
                .padding(insideMargin), content = content)
        }
    } else if (onClick == null && onLongPress == null) {
        top.yukonga.miuix.kmp.basic.Card(modifier = modifier, cornerRadius = cornerRadius,
            insideMargin = insideMargin, colors = colors, content = content)
    } else {
        top.yukonga.miuix.kmp.basic.Card(modifier = modifier, cornerRadius = cornerRadius,
            insideMargin = insideMargin, colors = colors, onClick = onClick, onLongPress = onLongPress,
            showIndication = showIndication, holdDownState = holdDownState, content = content)
    }
}

@Composable
private fun PreferenceRow(
    title: String,
    summary: String?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    insideMargin: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
    onClick: (() -> Unit)? = null,
    trailing: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier.fillMaxWidth().then(if (onClick != null) Modifier.materialClickable(enabled = enabled, onClick = onClick) else Modifier)
            .padding(insideMargin),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(Modifier.weight(1f)) {
            androidx.compose.material3.Text(title, style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (enabled) 1f else 0.38f))
            if (!summary.isNullOrEmpty()) androidx.compose.material3.Text(summary,
                style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        trailing()
    }
}

@Composable
fun SwitchPreference(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    summary: String? = null,
    enabled: Boolean = true,
) {
    if (useMaterial3()) {
        PreferenceRow(title, summary, modifier, enabled, onClick = { onCheckedChange(!checked) }) {
            androidx.compose.material3.Switch(checked = checked, onCheckedChange = null, enabled = enabled,
                thumbContent = if (checked) ({ androidx.compose.material3.Text("✓", fontSize = 18.sp) }) else null)
        }
    } else top.yukonga.miuix.kmp.preference.SwitchPreference(title = title, summary = summary,
        checked = checked, onCheckedChange = onCheckedChange, modifier = modifier, enabled = enabled)
}

@Composable
fun ArrowPreference(
    title: String,
    modifier: Modifier = Modifier,
    summary: String? = null,
    endActions: @Composable RowScope.() -> Unit = {},
    bottomAction: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    holdDownState: Boolean = false,
    enabled: Boolean = true,
) {
    if (useMaterial3()) {
        Column(modifier) {
            PreferenceRow(title, summary, enabled = enabled, onClick = onClick) {
                endActions()
                androidx.compose.material3.Text("›", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (bottomAction != null) Box(Modifier.padding(horizontal = 16.dp)) { bottomAction() }
        }
    } else top.yukonga.miuix.kmp.preference.ArrowPreference(title = title, summary = summary,
        modifier = modifier, endActions = endActions, bottomAction = bottomAction,
        onClick = onClick, holdDownState = holdDownState, enabled = enabled)
}

@Composable
fun WindowDropdownPreference(
    title: String,
    items: List<String>,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    summary: String? = null,
    enabled: Boolean = true,
) {
    if (useMaterial3()) {
        var expanded by remember { mutableStateOf(false) }
        var tapPosition by remember { mutableStateOf<Offset?>(null) }
        var menuPosition by remember { mutableStateOf<Offset?>(null) }
        Box(modifier) {
            PreferenceRow(
                title = title,
                summary = summary,
                modifier = Modifier.pointerInput(Unit) {
                    awaitEachGesture {
                        awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                        val up = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                        if (up != null) {
                            // Make the position available to clickable for this event only.
                            tapPosition = up.position
                            try {
                                awaitPointerEvent(PointerEventPass.Final)
                            } finally {
                                tapPosition = null
                            }
                        }
                    }
                },
                enabled = enabled,
                onClick = {
                    menuPosition = tapPosition
                    expanded = true
                },
            ) {
                androidx.compose.material3.Text(items.getOrNull(selectedIndex).orEmpty(),
                    modifier = Modifier.widthIn(max = 160.dp), color = MaterialTheme.colorScheme.primary)
            }
            val point = menuPosition
            val anchorModifier = if (point == null) Modifier.align(Alignment.BottomStart)
            else Modifier.align(AbsoluteAlignment.TopLeft).absoluteOffset {
                IntOffset(point.x.roundToInt(), point.y.roundToInt())
            }
            // A zero-size anchor lets Material place the popup around the tap and fit it to the window.
            Box(anchorModifier.size(0.dp)) {
                androidx.compose.material3.DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    shape = RoundedCornerShape(CardDefaults.CornerRadius),
                ) {
                    items.forEachIndexed { index, label ->
                        androidx.compose.material3.DropdownMenuItem(
                            text = { androidx.compose.material3.Text(label) },
                            modifier = Modifier.clickCursor(),
                            onClick = { expanded = false; onSelectedIndexChange(index) },
                            trailingIcon = { androidx.compose.material3.RadioButton(selectedIndex == index, onClick = null) },
                        )
                    }
                }
            }
        }
    } else top.yukonga.miuix.kmp.preference.WindowDropdownPreference(title = title, summary = summary,
        items = items, selectedIndex = selectedIndex, onSelectedIndexChange = onSelectedIndexChange,
        modifier = modifier, enabled = enabled)
}

@Composable
fun RadioButtonPreference(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    summary: String? = null,
    radioButtonLocation: RadioButtonLocation = RadioButtonLocation.End,
    insideMargin: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
) {
    if (useMaterial3()) PreferenceRow(title, summary, modifier, insideMargin = insideMargin, onClick = onClick) {
        androidx.compose.material3.RadioButton(selected, onClick = null)
    } else top.yukonga.miuix.kmp.preference.RadioButtonPreference(title = title, selected = selected,
        onClick = onClick, modifier = modifier, summary = summary,
        radioButtonLocation = radioButtonLocation, insideMargin = insideMargin)
}

@Composable
fun InfiniteProgressIndicator(
    modifier: Modifier = Modifier,
    color: Color = MiuixTheme.colorScheme.primary,
    size: Dp? = null,
) {
    if (useMaterial3()) androidx.compose.material3.CircularProgressIndicator(modifier.size(size ?: 40.dp), color = color)
    else if (size == null) top.yukonga.miuix.kmp.basic.InfiniteProgressIndicator(modifier = modifier, color = color)
    else top.yukonga.miuix.kmp.basic.InfiniteProgressIndicator(modifier = modifier, color = color, size = size)
}

@Composable
fun WindowDialog(
    show: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    summary: String? = null,
    insideMargin: DpSize = DialogDefaults.insideMargin,
    content: @Composable () -> Unit,
) {
    if (useMaterial3()) {
        val visibility = remember { MutableTransitionState(false) }
        visibility.targetState = show
        if (show || visibility.currentState || !visibility.isIdle) androidx.compose.material3.BasicAlertDialog(
            onDismissRequest = { if (show) onDismissRequest() },
            modifier = modifier,
        ) {
            AnimatedVisibility(
                visibleState = visibility,
                enter = fadeIn(tween(140)) + scaleIn(spring(dampingRatio = 0.82f, stiffness = 450f), initialScale = 0.92f),
                exit = fadeOut(tween(120)) + scaleOut(tween(120), targetScale = 0.96f),
            ) {
                Column(Modifier.squircleSurface(MaterialTheme.colorScheme.surfaceContainerHigh, DialogDefaults.CornerRadius)
                    .padding(horizontal = insideMargin.width, vertical = insideMargin.height),
                    verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    CompositionLocalProvider(androidx.compose.material3.LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
                        if (title != null) androidx.compose.material3.Text(title, style = MaterialTheme.typography.headlineSmall)
                        if (summary != null) androidx.compose.material3.Text(summary, style = MaterialTheme.typography.bodyMedium)
                        content()
                    }
                }
            }
        }
    } else top.yukonga.miuix.kmp.window.WindowDialog(show = show, onDismissRequest = onDismissRequest,
        modifier = modifier, title = title, summary = summary, insideMargin = insideMargin, content = content)
}

@Composable
fun TextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "",
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    trailingIcon: (@Composable () -> Unit)? = null,
) {
    if (useMaterial3()) androidx.compose.material3.OutlinedTextField(
        value = value, onValueChange = onValueChange, modifier = modifier,
        label = if (label.isEmpty()) null else ({ androidx.compose.material3.Text(label) }),
        singleLine = singleLine, maxLines = maxLines, keyboardOptions = keyboardOptions, trailingIcon = trailingIcon,
        shape = RoundedCornerShape(TextFieldDefaults.CornerRadius),
    ) else top.yukonga.miuix.kmp.basic.TextField(value = value, onValueChange = onValueChange,
        modifier = modifier, label = label, singleLine = singleLine, maxLines = maxLines,
        keyboardOptions = keyboardOptions, trailingIcon = trailingIcon)
}

@Composable
fun InputField(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "",
    interactionSource: MutableInteractionSource? = null,
) {
    if (useMaterial3()) androidx.compose.material3.OutlinedTextField(
        value = query, onValueChange = onQueryChange,
        modifier = modifier.onFocusChanged { onExpandedChange(it.isFocused) },
        placeholder = { androidx.compose.material3.Text(label) }, singleLine = true,
        shape = RoundedCornerShape(TextFieldDefaults.CornerRadius),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch(query) }),
        interactionSource = interactionSource,
    ) else top.yukonga.miuix.kmp.basic.InputField(query = query, onQueryChange = onQueryChange,
        onSearch = onSearch, expanded = expanded, onExpandedChange = onExpandedChange,
        modifier = modifier, label = label, interactionSource = interactionSource)
}

@Composable
fun Scaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    contentWindowInsets: WindowInsets = WindowInsets.systemBars.union(WindowInsets.displayCutout),
    materialLargeTitle: Boolean = false,
    content: @Composable (PaddingValues) -> Unit,
) {
    if (useMaterial3()) {
        val behavior = if (materialLargeTitle) androidx.compose.material3.TopAppBarDefaults.exitUntilCollapsedScrollBehavior() else null
        CompositionLocalProvider(LocalMaterialAppBarBehavior provides behavior) {
            androidx.compose.material3.Scaffold(
                modifier = modifier.then(if (behavior != null) Modifier.nestedScroll(behavior.nestedScrollConnection) else Modifier),
                topBar = topBar, bottomBar = bottomBar, contentWindowInsets = contentWindowInsets, content = content,
            )
        }
    } else top.yukonga.miuix.kmp.basic.Scaffold(modifier = modifier, topBar = topBar,
        bottomBar = bottomBar, contentWindowInsets = contentWindowInsets, content = content)
}

@Composable
fun IconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    holdDownState: Boolean = false,
    backgroundColor: Color = Color.Unspecified,
    cornerRadius: Dp = IconButtonDefaults.CornerRadius,
    minHeight: Dp = IconButtonDefaults.MinHeight,
    minWidth: Dp = IconButtonDefaults.MinWidth,
    content: @Composable () -> Unit,
) {
    if (useMaterial3()) {
        val source = remember { MutableInteractionSource() }
        androidx.compose.material3.IconButton(onClick = onClick,
            modifier = modifier.sizeIn(minWidth = minWidth, minHeight = minHeight)
                .clickCursor(enabled).materialPressFeedback(source),
            enabled = enabled,
            interactionSource = source,
            shape = RoundedCornerShape(cornerRadius),
            colors = androidx.compose.material3.IconButtonDefaults.iconButtonColors(
                containerColor = if (backgroundColor == Color.Unspecified) MaterialTheme.colorScheme.surfaceContainerHigh else backgroundColor,
            ), content = content)
    }
    else top.yukonga.miuix.kmp.basic.IconButton(onClick = onClick, modifier = modifier, enabled = enabled,
        holdDownState = holdDownState, backgroundColor = backgroundColor, cornerRadius = cornerRadius,
        minHeight = minHeight, minWidth = minWidth, content = content)
}

@Composable
fun OverlayListPopup(
    show: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    alignment: PopupPositionProvider.Align = PopupPositionProvider.Align.End,
    content: @Composable () -> Unit,
) {
    if (useMaterial3()) androidx.compose.material3.DropdownMenu(expanded = show,
        onDismissRequest = onDismissRequest, modifier = modifier) { content() }
    else top.yukonga.miuix.kmp.overlay.OverlayListPopup(show = show,
        onDismissRequest = onDismissRequest, alignment = alignment, content = content)
}

@Composable
fun DropdownImpl(
    text: String,
    optionSize: Int,
    isSelected: Boolean,
    index: Int,
    onSelectedIndexChange: (Int) -> Unit,
) {
    if (useMaterial3()) androidx.compose.material3.DropdownMenuItem(
        text = { androidx.compose.material3.Text(text) }, modifier = Modifier.clickCursor(),
        onClick = { onSelectedIndexChange(index) },
    ) else top.yukonga.miuix.kmp.basic.DropdownImpl(text = text, optionSize = optionSize,
        isSelected = isSelected, index = index, onSelectedIndexChange = onSelectedIndexChange)
}

@Composable
fun OverlayBottomSheet(
    show: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    sheetMaxWidth: Dp = BottomSheetDefaults.maxWidth,
    insideMargin: DpSize = BottomSheetDefaults.insideMargin,
    dragHandleColor: Color = BottomSheetDefaults.dragHandleColor(),
    allowDismiss: Boolean = true,
    enableNestedScroll: Boolean = true,
    content: @Composable () -> Unit,
) {
    if (useMaterial3()) {
        if (show) androidx.compose.material3.ModalBottomSheet(
            onDismissRequest = { if (allowDismiss) onDismissRequest() },
            modifier = modifier,
            sheetMaxWidth = sheetMaxWidth,
            shape = RoundedCornerShape(topStart = BottomSheetDefaults.cornerRadius, topEnd = BottomSheetDefaults.cornerRadius),
            sheetState = androidx.compose.material3.rememberModalBottomSheetState(
                skipPartiallyExpanded = true,
                confirmValueChange = { allowDismiss || it != androidx.compose.material3.SheetValue.Hidden },
            ),
            dragHandle = { androidx.compose.material3.BottomSheetDefaults.DragHandle(color = dragHandleColor) },
        ) {
            Column(Modifier.padding(horizontal = insideMargin.width, vertical = insideMargin.height),
                verticalArrangement = Arrangement.spacedBy(16.dp)) {
                if (title != null) androidx.compose.material3.Text(title, style = MaterialTheme.typography.titleLarge)
                content()
            }
        }
    } else top.yukonga.miuix.kmp.overlay.OverlayBottomSheet(show = show, onDismissRequest = onDismissRequest,
        modifier = modifier, title = title, sheetMaxWidth = sheetMaxWidth, insideMargin = insideMargin,
        dragHandleColor = dragHandleColor, allowDismiss = allowDismiss, enableNestedScroll = enableNestedScroll,
        content = content)
}
