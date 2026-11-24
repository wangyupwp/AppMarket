package com.app.market.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

internal actual fun isMaterialDynamicColorSupported(): Boolean = false

@Composable
internal actual fun materialSeedColor(dynamic: Boolean): Color = Color(0xFF52678E)
