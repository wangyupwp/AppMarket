package com.app.market.domain.repository

import kotlinx.coroutines.flow.StateFlow
import com.app.market.domain.model.preference.UiTheme
import com.app.market.domain.model.preference.MaterialAppearance

/** Persisted appearance and navigation preferences shared by all UI targets. */
interface ThemePreferencesRepository {
    val materialAppearance: StateFlow<MaterialAppearance>
    suspend fun setMaterialAppearance(value: MaterialAppearance)
    val uiTheme: StateFlow<UiTheme>
    suspend fun setUiTheme(value: UiTheme)
    val initialized: StateFlow<Boolean>
    val enableBlur: StateFlow<Boolean>
    val enableFloatingBottomBar: StateFlow<Boolean>
    val enableFloatingBottomBarBlur: StateFlow<Boolean>
    val enableNavigationBadge: StateFlow<Boolean>
    val navRailExpanded: StateFlow<Boolean>
    val enablePredictiveBack: StateFlow<Boolean>
    val pageScale: StateFlow<Float>

    suspend fun setEnableBlur(value: Boolean)
    suspend fun setEnableFloatingBottomBar(value: Boolean)
    suspend fun setEnableFloatingBottomBarBlur(value: Boolean)
    suspend fun setEnableNavigationBadge(value: Boolean)
    suspend fun setNavRailExpanded(value: Boolean)
    suspend fun setEnablePredictiveBack(value: Boolean)
    suspend fun setPageScale(value: Float)
}
