package com.feryaeljustice.supersnakegame.data.manager

import android.app.Activity
import androidx.compose.runtime.compositionLocalOf

/**
 * CompositionLocal providing access to the current [AdManager] across Composable screens.
 */
val LocalAdManager = compositionLocalOf<AdManager?> { null }

/**
 * Contract for managing mobile ads lifecycle and display.
 */
interface AdManager {
    /**
     * Initializes the Mobile Ads SDK and preloads ads if consent allows.
     */
    fun initialize()

    /**
     * Requests UMP consent information update and presents the consent form if required.
     * Initializes Mobile Ads SDK as soon as consent is confirmed (canRequestAds is true).
     */
    fun gatherConsentAndInitialize(
        activity: Activity,
        onConsentCompleted: () -> Unit = {},
    )

    /**
     * Returns true if the user is in a jurisdiction where privacy options must be exposed.
     */
    fun isPrivacyOptionsRequired(): Boolean

    /**
     * Presents the privacy options form so the user can modify their consent preferences at any time.
     */
    fun showPrivacyOptionsForm(
        activity: Activity,
        onDismissed: () -> Unit = {},
    )

    /**
     * Loads an interstitial ad in the background.
     */
    fun loadInterstitialAd()

    /**
     * Returns true if an interstitial ad is loaded and ready.
     */
    fun isInterstitialLoaded(): Boolean

    /**
     * Displays an interstitial ad if available and interval policy allows it.
     * Always calls [onAdDismissed] when done (or immediately if not shown).
     */
    fun showInterstitial(
        activity: Activity,
        force: Boolean = false,
        onAdDismissed: () -> Unit = {},
    )
}
