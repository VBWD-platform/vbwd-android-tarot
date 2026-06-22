package com.vbwd.plugin.tarot.domain

/** API endpoint paths for the tarot plugin. Port of the iOS `TarotEndpoints`. */
internal object TarotEndpoints {
    const val LIMITS = "/taro/limits"
    const val SESSION = "/taro/session"
    const val HISTORY = "/taro/history"
    fun situation(sessionId: String): String = "/taro/session/$sessionId/situation"
    fun followUp(sessionId: String): String = "/taro/session/$sessionId/follow-up"
}
