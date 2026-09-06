package com.feryaeljustice.supersnakegame.ui.navigation

import android.os.Bundle
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GenericNavTypeTest {

    @Test
    fun serializeAndParse_roundTripPreservesData() {
        val navType = createNavType<GameScreenData>()
        val original = GameScreenData(gameId = "snake_arcade_999")

        val serialized = navType.serializeAsValue(original)
        assertNotNull(serialized)

        val parsed = navType.parseValue(serialized)
        assertEquals(original, parsed)
        assertEquals("snake_arcade_999", parsed.gameId)
    }

    @Test
    fun bundlePutAndGet_roundTripPreservesData() {
        val navType = createNavType<GameScreenData>()
        val original = GameScreenData(gameId = "session_abc")
        val bundle = Bundle()

        navType.put(bundle, "game_key", original)
        val retrieved = navType.get(bundle, "game_key")

        assertEquals(original, retrieved)
        assertEquals("session_abc", retrieved?.gameId)
    }
}
