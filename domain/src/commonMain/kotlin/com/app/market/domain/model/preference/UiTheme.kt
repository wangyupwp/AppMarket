package com.app.market.domain.model.preference

enum class UiTheme(val token: String) {
    MIUIX("miuix"),
    MATERIAL3("material3");

    companion object {
        fun fromToken(token: String?): UiTheme = entries.firstOrNull { it.token == token } ?: MIUIX
    }
}
