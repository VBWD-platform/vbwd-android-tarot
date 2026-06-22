package com.vbwd.plugin.tarot

import com.vbwd.core.events.DefaultEventBus
import com.vbwd.core.networking.ApiClient
import com.vbwd.core.networking.ApiClientConfig
import com.vbwd.core.networking.ApiEvent
import com.vbwd.core.networking.EmptyResponse
import com.vbwd.core.networking.HttpMethod
import com.vbwd.core.plugins.DefaultPlatformSdk
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.DeserializationStrategy
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

private class FakeApi : ApiClient {
    @Suppress("UNCHECKED_CAST")
    override suspend fun <T> request(
        method: HttpMethod,
        path: String,
        jsonBody: String?,
        deserializer: DeserializationStrategy<T>,
    ): T = EmptyResponse() as T
    override fun setToken(token: String?) = Unit
    override fun on(event: ApiEvent, handler: () -> Unit) = Unit
}

class TarotPluginContractTest {
    private fun sdk() = DefaultPlatformSdk(FakeApi(), ApiClientConfig("http://x"), DefaultEventBus(FakeApi()))

    @Test
    fun `install registers the tarot route, menu item and translations`() = runTest {
        val platform = sdk()
        TarotPlugin().install(platform)
        assertEquals(listOf("/tarot"), platform.getRoutes().map { it.path })
        assertEquals(listOf("tarot"), platform.getMenuItems().map { it.id })
        assertEquals("Tarot Card Reading", platform.getTranslations()["en"]?.get("tarot.title"))
    }
}
