package com.vbwd.plugin.tarot.domain

import com.vbwd.core.networking.ApiClient
import com.vbwd.core.networking.ApiError
import com.vbwd.core.networking.get
import com.vbwd.core.networking.post
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Tarot API operations (DIP — testable). Port of the iOS protocol. */
interface TarotService {
    suspend fun fetchDailyLimits(): DailyLimits

    suspend fun createSession(): TaroSession

    suspend fun submitSituation(
        sessionId: String,
        text: String,
        language: String,
    ): String
}

/** Default impl backed by [ApiClient]. Port of `DefaultTarotService`. */
class DefaultTarotService(private val api: ApiClient) : TarotService {
    override suspend fun fetchDailyLimits(): DailyLimits {
        val response = api.get<LimitsResponse>(TarotEndpoints.LIMITS)
        return response.limits.takeIf { response.success } ?: failure("Failed to load tarot limits")
    }

    override suspend fun createSession(): TaroSession {
        val response = api.post<EmptyBody, SessionResponse>(TarotEndpoints.SESSION, EmptyBody())
        return response.session.takeIf { response.success }
            ?: failure(response.message ?: "Failed to create session")
    }

    override suspend fun submitSituation(
        sessionId: String,
        text: String,
        language: String,
    ): String {
        val response =
            api.post<SituationBody, SituationResponse>(
                TarotEndpoints.situation(sessionId),
                SituationBody(situationText = text, language = language),
            )
        return response.interpretation.takeIf { response.success }
            ?: failure(response.error ?: "Failed to submit situation")
    }

    private fun failure(message: String): Nothing = throw ApiError.Http(status = HTTP_OK, message = message)

    @Serializable
    private class EmptyBody

    @Serializable
    private data class SituationBody(
        @SerialName("situation_text") val situationText: String,
        val language: String,
    )

    private companion object {
        // The backend returns 200 with success=false on a logical failure.
        const val HTTP_OK = 200
    }
}
