package com.app.market.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.click
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import com.app.market.domain.model.preference.UiTheme
import com.app.market.ui.theme.MarketTheme
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class MaterialDropdownMouseTest {
    @Test
    fun mouseCanDismissAndSelectDropdownOptions() = runComposeUiTest {
        val selected = mutableIntStateOf(0)
        setContent {
            MarketTheme(UiTheme.MATERIAL3) {
                Box(Modifier.fillMaxSize().testTag("window")) {
                    WindowDropdownPreference(
                        title = "Theme",
                        items = listOf("miuix", "Material 3"),
                        selectedIndex = selected.intValue,
                        onSelectedIndexChange = { selected.intValue = it },
                        modifier = Modifier.width(320.dp).testTag("preference"),
                    )
                }
            }
        }

        val preference = onNodeWithTag("preference")
        preference.performMouseInput { click(Offset(width * 0.2f, centerY)) }
        onNodeWithText("Material 3").assertExists()
        onNodeWithTag("window").performMouseInput { click(Offset(width * 0.9f, height * 0.9f)) }
        onNodeWithText("Material 3").assertDoesNotExist()
        runOnIdle { assertEquals(0, selected.intValue) }

        preference.performMouseInput { click(Offset(width * 0.8f, centerY)) }
        onNodeWithText("Material 3").performMouseInput { click() }
        runOnIdle { assertEquals(1, selected.intValue) }
    }
}
