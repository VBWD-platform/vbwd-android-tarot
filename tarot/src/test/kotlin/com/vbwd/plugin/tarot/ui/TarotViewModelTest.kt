package com.vbwd.plugin.tarot.ui

import com.vbwd.plugin.tarot.domain.DailyLimits
import com.vbwd.plugin.tarot.domain.TaroSession
import com.vbwd.plugin.tarot.domain.TaroSessionStatus
import com.vbwd.plugin.tarot.domain.TarotService
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

private class FakeTarotService(
    private val limits: DailyLimits = DailyLimits(3, 2, 1, "Pro", true),
    private val session: TaroSession =
        TaroSession(sessionId = "s1", status = TaroSessionStatus.ACTIVE, createdAt = "now"),
    private val interpretation: String = "Insight.",
) : TarotService {
    override suspend fun fetchDailyLimits(): DailyLimits = limits
    override suspend fun createSession(): TaroSession = session
    override suspend fun submitSituation(sessionId: String, text: String, language: String): String = interpretation
}

class TarotViewModelTest {
    @Test
    fun `load sets the daily limits`() = runTest {
        val vm = TarotViewModel(FakeTarotService())
        vm.load()
        assertEquals(2, vm.uiState.value.limits?.dailyRemaining)
    }

    @Test
    fun `createSession then submitSituation populates the session and interpretation`() = runTest {
        val vm = TarotViewModel(FakeTarotService())
        vm.createSession()
        assertEquals("s1", vm.uiState.value.session?.sessionId)
        vm.submitSituation("help")
        assertEquals("Insight.", vm.uiState.value.interpretation)
    }
}
