package com.vbwd.plugin.tarot.screenshot

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import com.vbwd.core.theme.InMemoryThemeStore
import com.vbwd.core.theme.ThemeManager
import com.vbwd.core.theme.ThemeRegistry
import com.vbwd.core.theme.VbwdTheme
import com.vbwd.plugin.tarot.domain.DailyLimits
import com.vbwd.plugin.tarot.domain.TaroSession
import com.vbwd.plugin.tarot.domain.TaroSessionStatus
import com.vbwd.plugin.tarot.domain.TarotService
import com.vbwd.plugin.tarot.ui.TarotScreen
import com.vbwd.plugin.tarot.ui.TarotViewModel
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

private class DemoTarotService : TarotService {
    override suspend fun fetchDailyLimits() =
        DailyLimits(dailyTotal = 3, dailyRemaining = 2, dailyUsed = 1, planName = "Pro", canCreate = true)

    override suspend fun createSession() =
        TaroSession(sessionId = "s1", status = TaroSessionStatus.ACTIVE, createdAt = "now")

    override suspend fun submitSituation(
        sessionId: String,
        text: String,
        language: String,
    ) = "The cards favour a bold move this week."
}

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel5)
class TarotScreenshotTest {
    private val theme = ThemeManager(ThemeRegistry(), InMemoryThemeStore())

    @Test
    fun tarot() {
        captureRoboImage("screenshots/tarot.png") {
            VbwdTheme(theme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    TarotScreen(TarotViewModel(DemoTarotService()))
                }
            }
        }
    }
}
