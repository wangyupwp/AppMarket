package com.app.market.ui.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.test.swipeDown
import androidx.compose.ui.test.swipeUp
import androidx.compose.ui.unit.dp
import com.app.market.domain.model.preference.UiTheme
import com.app.market.ui.theme.MarketTheme
import kotlin.test.Test
import kotlin.test.assertTrue
import top.yukonga.miuix.kmp.basic.ScrollBehavior

@OptIn(ExperimentalTestApi::class)
class ThemeScrollingTest {
    @Test
    fun materialSettingsListScrolls() = verifyScrolling(UiTheme.MATERIAL3, 0)

    @Test
    fun materialSecondaryPageScrolls() = verifyScrolling(UiTheme.MATERIAL3, 1)

    @Test
    fun materialFeedScrolls() = verifyScrolling(UiTheme.MATERIAL3, 2)

    @Test
    fun miuixSettingsListStillScrolls() = verifyScrolling(UiTheme.MIUIX, 0)

    @Test
    fun materialSettingsListScrollsWithMouseWheel() = verifyScrolling(UiTheme.MATERIAL3, 0, mouse = true)

    private fun verifyScrolling(theme: UiTheme, scaffold: Int, mouse: Boolean = false) = runComposeUiTest {
        val listState = LazyListState()
        setContent {
            MarketTheme(theme) {
                val content: @Composable (ScrollBehavior) -> Unit = { behavior ->
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize().testTag("list")
                            .nestedScroll(behavior.nestedScrollConnection),
                    ) {
                        items(100) { index ->
                            androidx.compose.material3.Text("Row $index", Modifier.height(48.dp))
                        }
                    }
                }
                when (scaffold) {
                    0 -> MainTabScaffold("Settings") { _, _, behavior -> content(behavior) }
                    1 -> MarketScaffold("Theme", onBack = {}) { _, _, behavior -> content(behavior) }
                    else -> HidingMainTabScaffold("Today") { _, _, behavior -> content(behavior) }
                }
            }
        }
        val list = onNodeWithTag("list")
        if (mouse) list.performMouseInput { moveTo(center); scroll(600f) }
        else list.performTouchInput { swipeUp() }
        waitForIdle()
        runOnIdle {
            assertTrue(listState.firstVisibleItemIndex > 0, "The drag must reach the list beneath the app bar")
        }
        val scrolledIndex = runOnIdle { listState.firstVisibleItemIndex }
        // Scaffold 的顶栏覆盖列表顶部，回滑手势从列表可见区域开始。
        if (mouse) list.performMouseInput { scroll(-600f) }
        else list.performTouchInput { swipeDown(startY = height * 0.35f, endY = height * 0.85f) }
        waitForIdle()
        runOnIdle {
            assertTrue(listState.firstVisibleItemIndex < scrolledIndex, "The list must also scroll back")
        }
    }
}
