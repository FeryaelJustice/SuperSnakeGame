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
     * Initializes the Mobile Ads SDK and preloads ads.
     */
    fun initialize()

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
