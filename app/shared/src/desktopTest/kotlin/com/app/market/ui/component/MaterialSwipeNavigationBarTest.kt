package com.app.market.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class MaterialSwipeNavigationBarTest {
    @Test
    fun draggingPreviewsSelectionAndChangesPageOnlyOnRelease() = runComposeUiTest {
        val selected = mutableIntStateOf(3)
        val changes = mutableListOf<Int>()
        setContent {
            MaterialTheme {
                MaterialSwipeNavigationBar(selected.intValue, 4, { selected.intValue = it; changes += it },
                    modifier = Modifier.width(400.dp).testTag("bar")) { highlighted ->
                    repeat(4) { index ->
                        NavigationBarItem(selected = highlighted == index, onClick = { selected.intValue = index },
                            icon = { Spacer(Modifier.size(24.dp)) }, label = { Text("Tab $index") })
                    }
                }
            }
        }
        onNodeWithTag("bar").performTouchInput {
            down(Offset(width * 0.88f, centerY))
            moveTo(Offset(width * 0.12f, centerY), delayMillis = 400)
        }
        onNodeWithText("Tab 0").assertIsSelected()
        runOnIdle { assertEquals(3, selected.intValue); assertEquals(emptyList(), changes) }
        onNodeWithTag("bar").performTouchInput { up() }
        runOnIdle { assertEquals(0, selected.intValue); assertEquals(listOf(0), changes) }
    }

    @Test
    fun ordinaryTapStillWorks() = runComposeUiTest {
        val selected = mutableIntStateOf(0)
        setContent {
            MaterialTheme {
                MaterialSwipeNavigationBar(selected.intValue, 4, { selected.intValue = it },
                    modifier = Modifier.width(400.dp).testTag("bar")) { highlighted ->
                    repeat(4) { index ->
                        NavigationBarItem(selected = highlighted == index, onClick = { selected.intValue = index },
                            icon = { Spacer(Modifier.size(24.dp)) }, label = { Text("Tab $index") })
                    }
                }
            }
        }
        onNodeWithText("Tab 2").performTouchInput { click() }
        runOnIdle { assertEquals(2, selected.intValue) }
    }

    @Test
    fun onlyPrimaryMouseDragChangesPage() = runComposeUiTest {
        val selected = mutableIntStateOf(0)
        setContent {
            MaterialTheme {
                MaterialSwipeNavigationBar(selected.intValue, 4, { selected.intValue = it },
                    modifier = Modifier.width(400.dp).testTag("bar")) { highlighted ->
                    repeat(4) { index ->
                        NavigationBarItem(selected = highlighted == index, onClick = { selected.intValue = index },
                            icon = { Spacer(Modifier.size(24.dp)) }, label = { Text("Tab $index") })
                    }
                }
            }
        }
        val bar = onNodeWithTag("bar")
        bar.performMouseInput {
            moveTo(Offset(width * 0.12f, centerY))
            press(MouseButton.Secondary)
            moveTo(Offset(width * 0.88f, centerY))
            release(MouseButton.Secondary)
        }
        runOnIdle { assertEquals(0, selected.intValue) }

        bar.performMouseInput {
            moveTo(Offset(width * 0.12f, centerY))
            press()
            moveTo(Offset(width * 0.88f, centerY))
            release()
        }
        runOnIdle { assertEquals(3, selected.intValue) }
    }

    @Test
    fun parentInterceptionCancelsDragAndKeepsOriginalPage() = runComposeUiTest {
        val selected = mutableIntStateOf(0)
        val intercept = mutableStateOf(false)
        setContent {
            MaterialTheme {
                Box(Modifier.pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent(PointerEventPass.Initial)
                            if (intercept.value) event.changes.forEach { it.consume() }
                        }
                    }
                }) {
                    MaterialSwipeNavigationBar(selected.intValue, 4, { selected.intValue = it },
                        modifier = Modifier.width(400.dp).testTag("bar")) { highlighted ->
                        repeat(4) { index ->
                            NavigationBarItem(selected = highlighted == index, onClick = { selected.intValue = index },
                                icon = { Spacer(Modifier.size(24.dp)) }, label = { Text("Tab $index") })
                        }
                    }
                }
            }
        }
        onNodeWithTag("bar").performTouchInput {
            down(Offset(width * 0.12f, centerY))
            moveTo(Offset(width * 0.85f, centerY), delayMillis = 400)
        }
        onNodeWithText("Tab 3").assertIsSelected()
        runOnIdle { intercept.value = true }
        onNodeWithTag("bar").performTouchInput {
            moveTo(Offset(width * 0.88f, centerY))
            up()
        }
        runOnIdle { assertEquals(0, selected.intValue) }
        onNodeWithText("Tab 0").assertIsSelected()
    }

    @Test
    fun floatingBarUsesItsVisibleWidth() = verifySwipe(LayoutDirection.Ltr, 4, floating = true, expected = 0)

    @Test
    fun threeTabsRespectRightToLeftLayout() = verifySwipe(LayoutDirection.Rtl, 3, floating = false, expected = 2)

    private fun verifySwipe(direction: LayoutDirection, count: Int, floating: Boolean, expected: Int) = runComposeUiTest {
        val selected = mutableIntStateOf(if (direction == LayoutDirection.Ltr) count - 1 else 0)
        setContent {
            MaterialTheme {
                CompositionLocalProvider(LocalLayoutDirection provides direction) {
                    MaterialSwipeNavigationBar(selected.intValue, count, { selected.intValue = it },
                        modifier = Modifier.width(400.dp).padding(horizontal = if (floating) 16.dp else 0.dp).testTag("bar")) { highlighted ->
                        repeat(count) { index ->
                            NavigationBarItem(selected = highlighted == index, onClick = { selected.intValue = index },
                                icon = { Spacer(Modifier.size(24.dp)) }, label = { Text("Tab $index") })
                        }
                    }
                }
            }
        }
        onNodeWithTag("bar").performTouchInput { swipeLeft() }
        runOnIdle { assertEquals(expected, selected.intValue) }
        onNodeWithText("Tab $expected").assertIsSelected()
    }
}
