package com.app.market.viewmodel

import com.app.market.domain.model.preference.UiTheme
import com.app.market.domain.model.preference.MaterialAppearance
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.market.domain.repository.ThemePreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@Immutable
data class ThemeSettingsUiState(
    val materialAppearance: MaterialAppearance = MaterialAppearance(),
    val uiTheme: UiTheme = UiTheme.MIUIX,
    val enableBlur: Boolean = false,
    val enableFloatingBottomBar: Boolean = false,
    val enableFloatingBottomBarBlur: Boolean = false,
    val enableNavigationBadge: Boolean = true,
    val enablePredictiveBack: Boolean = false,
    val pageScale: Float = 1f,
)

class ThemeSettingsViewModel(
    private val preferences: ThemePreferencesRepository,
) : ViewModel() {
    private val appearance = combine(preferences.enableBlur, preferences.enableFloatingBottomBar, preferences.uiTheme) { blur, floating, theme ->
        Triple(blur, floating, theme)
    }

    private val baseState = combine(
        appearance,
        preferences.enableFloatingBottomBarBlur,
        preferences.enableNavigationBadge,
        preferences.enablePredictiveBack,
        preferences.pageScale,
    ) { appearance, glass, badge, predictiveBack, scale ->
        ThemeSettingsUiState(
            uiTheme = appearance.third,
            enableBlur = appearance.first,
            enableFloatingBottomBar = appearance.second,
            enableFloatingBottomBarBlur = glass,
            enableNavigationBadge = badge,
            enablePredictiveBack = predictiveBack,
            pageScale = scale,
        )
    }

    val uiState: StateFlow<ThemeSettingsUiState> = combine(baseState, preferences.materialAppearance) { state, material ->
        state.copy(materialAppearance = material)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, ThemeSettingsUiState())

    fun setMaterialAppearance(value: MaterialAppearance) = persist { preferences.setMaterialAppearance(value) }

    fun setUiTheme(value: UiTheme) = persist { preferences.setUiTheme(value) }

    fun setEnableBlur(value: Boolean) = persist { preferences.setEnableBlur(value) }
    fun setEnableFloatingBottomBar(value: Boolean) = persist { preferences.setEnableFloatingBottomBar(value) }
    fun setEnableFloatingBottomBarBlur(value: Boolean) = persist { preferences.setEnableFloatingBottomBarBlur(value) }
    fun setEnableNavigationBadge(value: Boolean) = persist { preferences.setEnableNavigationBadge(value) }
    fun setEnablePredictiveBack(value: Boolean) = persist { preferences.setEnablePredictiveBack(value) }
    fun setPageScale(value: Float) = persist { preferences.setPageScale(value) }

    private fun persist(block: suspend () -> Unit) {
        viewModelScope.launch { block() }
    }
}
