package com.vbwd.plugin.tarot.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Response of `GET /taro/limits`. Port of the iOS `DailyLimits`. */
@Serializable
data class DailyLimits(
    @SerialName("daily_total") val dailyTotal: Int,
    @SerialName("daily_remaining") val dailyRemaining: Int,
    @SerialName("daily_used") val dailyUsed: Int,
    @SerialName("plan_name") val planName: String,
    @SerialName("can_create") val canCreate: Boolean,
)

@Serializable
internal data class LimitsResponse(val success: Boolean, val limits: DailyLimits? = null)

@Serializable
enum class CardPosition { PAST, PRESENT, FUTURE, ADDITIONAL }

@Serializable
enum class CardOrientation { UPRIGHT, REVERSED }

@Serializable
data class Arcana(
    val id: String,
    val number: Int? = null,
    val name: String,
    val suit: String? = null,
    val rank: String? = null,
    @SerialName("arcana_type") val arcanaType: String,
    @SerialName("upright_meaning") val uprightMeaning: String,
    @SerialName("reversed_meaning") val reversedMeaning: String,
    @SerialName("image_url") val imageUrl: String,
) {
    val isMajorArcana: Boolean get() = arcanaType == "MAJOR_ARCANA"
    val suitName: String?
        get() =
            when (arcanaType) {
                "CUPS", "PENTACLES", "SWORDS", "WANDS" -> arcanaType
                else -> null
            }
}

@Serializable
data class TaroCard(
    @SerialName("card_id") val cardId: String,
    val position: CardPosition,
    val orientation: CardOrientation,
    @SerialName("arcana_id") val arcanaId: String,
    val arcana: Arcana? = null,
    @SerialName("ai_interpretation") val aiInterpretation: String? = null,
    val interpretation: String? = null,
)

@Serializable
enum class TaroSessionStatus { ACTIVE, EXPIRED, CLOSED }

@Serializable
data class TaroSession(
    @SerialName("session_id") val sessionId: String,
    @SerialName("user_id") val userId: String? = null,
    val status: TaroSessionStatus,
    val cards: List<TaroCard> = emptyList(),
    @SerialName("created_at") val createdAt: String,
    @SerialName("expires_at") val expiresAt: String? = null,
    @SerialName("ended_at") val endedAt: String? = null,
    @SerialName("tokens_consumed") val tokensConsumed: Int = 0,
    @SerialName("follow_up_count") val followUpCount: Int = 0,
    @SerialName("max_follow_ups") val maxFollowUps: Int? = null,
    @SerialName("spread_id") val spreadId: String? = null,
)

@Serializable
internal data class SessionResponse(
    val success: Boolean,
    val session: TaroSession? = null,
    val message: String? = null,
)

@Serializable
internal data class SituationResponse(
    val success: Boolean,
    val interpretation: String? = null,
    val error: String? = null,
)
