package com.feryaeljustice.supersnakegame.data.manager

import android.app.Activity
import com.feryaeljustice.supersnakegame.fakes.FakeAdManager
import io.mockk.mockk
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AdManagerTest {

    private lateinit var adManager: FakeAdManager
    private val mockActivity = mockk<Activity>(relaxed = true)

    @Before
    fun setup() {
        adManager = FakeAdManager()
    }

    @Test
    fun testInitialization() {
        assertFalse(adManager.initializeCalled)
        adManager.initialize()
        assertTrue(adManager.initializeCalled)
    }

    @Test
    fun testGatherConsentAndInitialize() {
        var callbackInvoked = false
        adManager.gatherConsentAndInitialize(mockActivity) {
            callbackInvoked = true
        }
        assertTrue(adManager.gatherConsentCalled)
        assertTrue(callbackInvoked)
    }

    @Test
    fun testPrivacyOptionsRequired() {
        adManager.isPrivacyOptionsRequiredResult = true
        assertTrue(adManager.isPrivacyOptionsRequired())

        adManager.isPrivacyOptionsRequiredResult = false
        assertFalse(adManager.isPrivacyOptionsRequired())
    }

    @Test
    fun testShowPrivacyOptionsForm() {
        var dismissed = false
        adManager.showPrivacyOptionsForm(mockActivity) {
            dismissed = true
        }
        assertTrue(adManager.showPrivacyOptionsCalled)
        assertTrue(dismissed)
    }

    @Test
    fun testLoadAndShowInterstitial() {
        adManager.loadInterstitialAd()
        assertTrue(adManager.loadInterstitialCalled)

        var dismissed = false
        adManager.showInterstitial(mockActivity) {
            dismissed = true
        }
        assertTrue(adManager.showInterstitialCalled)
        assertTrue(dismissed)
    }
}
