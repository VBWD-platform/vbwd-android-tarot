package com.vbwd.plugin.tarot.domain

import com.vbwd.core.networking.ApiClient
import com.vbwd.core.networking.ApiError
import com.vbwd.core.networking.HttpMethod
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Test

class TarotServiceTest {
    private val client = mockk<ApiClient>(relaxed = true)
    private val service = DefaultTarotService(client)

    @Test
    fun `fetchDailyLimits returns the limits on success`() =
        runTest {
            coEvery { client.request<LimitsResponse>(HttpMethod.GET, "/taro/limits", any(), any()) } returns
                LimitsResponse(
                    success = true,
                    limits =
                        DailyLimits(
                            dailyTotal = 3,
                            dailyRemaining = 2,
                            dailyUsed = 1,
                            planName = "Pro",
                            canCreate = true,
                        ),
                )
            assertEquals(2, service.fetchDailyLimits().dailyRemaining)
        }

    @Test
    fun `fetchDailyLimits surfaces a failure (success=false) instead of a false success`() =
        runTest {
            coEvery { client.request<LimitsResponse>(HttpMethod.GET, "/taro/limits", any(), any()) } returns
                LimitsResponse(success = false, limits = null)
            val error = runCatching { service.fetchDailyLimits() }.exceptionOrNull()
            assertInstanceOf(ApiError.Http::class.java, error)
        }

    @Test
    fun `submitSituation returns the interpretation`() =
        runTest {
            coEvery {
                client.request<SituationResponse>(HttpMethod.POST, "/taro/session/s1/situation", any(), any())
            } returns SituationResponse(success = true, interpretation = "The cards say yes.")
            assertEquals("The cards say yes.", service.submitSituation("s1", "help", "en"))
        }
}
