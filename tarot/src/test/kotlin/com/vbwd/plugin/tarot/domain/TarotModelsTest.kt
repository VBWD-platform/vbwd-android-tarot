package com.vbwd.plugin.tarot.domain

import com.vbwd.core.networking.ApiJson
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TarotModelsTest {
    private val json = ApiJson.instance

    @Test
    fun `daily limits decode snake_case`() {
        val body =
            """
            {"daily_total":3,"daily_remaining":2,"daily_used":1,"plan_name":"Pro","can_create":true}
            """.trimIndent()
        val limits = json.decodeFromString(DailyLimits.serializer(), body)
        assertEquals(2, limits.dailyRemaining)
        assertTrue(limits.canCreate)
    }

    @Test
    fun `session decodes nested cards and enums`() {
        val body =
            """
            {"session_id":"s1","status":"ACTIVE","created_at":"now",
             "cards":[{"card_id":"c1","position":"PRESENT","orientation":"REVERSED","arcana_id":"a1"}]}
            """.trimIndent()
        val session = json.decodeFromString(TaroSession.serializer(), body)
        assertEquals(TaroSessionStatus.ACTIVE, session.status)
        assertEquals(CardPosition.PRESENT, session.cards.single().position)
        assertEquals(CardOrientation.REVERSED, session.cards.single().orientation)
    }

    @Test
    fun `arcana type helpers classify major and minor arcana`() {
        val major =
            Arcana(
                id = "1",
                name = "The Fool",
                arcanaType = "MAJOR_ARCANA",
                uprightMeaning = "u",
                reversedMeaning = "r",
                imageUrl = "x",
            )
        assertTrue(major.isMajorArcana)
        assertEquals(null, major.suitName)

        val minor = major.copy(arcanaType = "CUPS")
        assertFalse(minor.isMajorArcana)
        assertEquals("CUPS", minor.suitName)
    }
}
