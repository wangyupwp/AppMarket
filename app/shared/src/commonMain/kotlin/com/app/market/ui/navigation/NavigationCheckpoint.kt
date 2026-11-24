package com.app.market.ui.navigation

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/** Lightweight state needed to reopen the last screen after a memory-related process death. */
@Serializable
data class NavigationCheckpoint(
    val routes: List<Route>,
    val mainTab: String? = null,
) {
    fun encode(): String = Json.encodeToString(this)

    companion object {
        fun decode(value: String): NavigationCheckpoint? =
            runCatching { Json.decodeFromString<NavigationCheckpoint>(value) }.getOrNull()
                ?.takeIf { it.routes.firstOrNull() == Route.Main }
    }
}
