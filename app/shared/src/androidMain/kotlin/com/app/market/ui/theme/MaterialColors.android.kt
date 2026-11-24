package com.app.market.ui.theme

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext

internal actual fun isMaterialDynamicColorSupported(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

@Composable
internal actual fun materialSeedColor(dynamic: Boolean): Color {
    LocalConfiguration.current
    return if (dynamic && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        Color(LocalContext.current.getColor(android.R.color.system_accent1_500))
    } else Color(0xFF52678E)
}
