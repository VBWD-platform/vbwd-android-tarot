package com.vbwd.plugin.tarot

import androidx.compose.runtime.remember
import com.vbwd.core.plugins.PlatformSdk
import com.vbwd.core.plugins.Plugin
import com.vbwd.core.plugins.PluginMetadata
import com.vbwd.core.plugins.PluginRoute
import com.vbwd.core.plugins.SemanticVersion
import com.vbwd.core.plugins.registries.MenuItem
import com.vbwd.plugin.tarot.domain.DefaultTarotService
import com.vbwd.plugin.tarot.ui.TarotScreen
import com.vbwd.plugin.tarot.ui.TarotViewModel

/**
 * Tarot card reading with AI-powered interpretations. Port of the iOS
 * `TarotPlugin`: registers `/tarot` + a side-menu entry. SRP — wiring only;
 * logic lives in the service/VM.
 */
class TarotPlugin : Plugin {
    override val metadata =
        PluginMetadata(
            name = "tarot",
            version = SemanticVersion(0, 1, 0),
            description = "Tarot card reading with AI-powered interpretations.",
            author = "VBWD",
            keywords = listOf("tarot", "taro", "oracle", "divination"),
            translations = mapOf("en" to TRANSLATIONS),
        )

    override suspend fun install(sdk: PlatformSdk) {
        val service = DefaultTarotService(sdk.api)
        sdk.addRoute(
            PluginRoute(path = "/tarot", name = "tarot", requiresAuth = true) {
                TarotScreen(remember { TarotViewModel(service) })
            },
        )
        sdk.addMenuItem(
            MenuItem(
                id = "tarot",
                icon = "sparkles",
                title = "Tarot",
                routePath = "/tarot",
                order = MENU_ORDER,
                section = "top",
            ),
        )
        sdk.addTranslations("en", TRANSLATIONS)
    }

    private companion object {
        const val MENU_ORDER = 50

        val TRANSLATIONS =
            mapOf(
                "nav.tarot" to "Tarot",
                "tarot.title" to "Tarot Card Reading",
                "tarot.subtitle" to "Get AI-powered tarot interpretations and insights",
                "tarot.createSession" to "Start Reading",
                "tarot.dailyLimitReached" to "You've reached your daily session limit. Try again tomorrow.",
            )
    }
}
