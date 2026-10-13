package com.feryaeljustice.supersnakegame.data.manager

import android.app.Activity
import android.content.Context
import android.os.SystemClock
import android.util.Log
import com.feryaeljustice.supersnakegame.BuildConfig
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Clean and lifecycle-aware AdMob manager implementing [AdManager].
 * Controls initialization, UMP consent gathering, background preloading, and safe presentation of interstitial ads.
 */
class AdMobManager(
    private val context: Context,
) : AdManager {

    private companion object {
        private const val TAG = "AdMobManager"
        // Minimum interval in milliseconds between interstitial ad impressions (45 seconds)
        private const val MIN_INTERSTITIAL_INTERVAL_MS = 45_000L
    }

    private val consentInformation: ConsentInformation by lazy {
        UserMessagingPlatform.getConsentInformation(context)
    }

    private val isMobileAdsInitializeCalled = AtomicBoolean(false)
    private var interstitialAd: InterstitialAd? = null
    private val isLoadingInterstitial = AtomicBoolean(false)
    private var lastAdShownTimestamp: Long = 0L

    private val adUnitId: String
        get() = BuildConfig.ADMOB_INTERSTITIAL_AD_UNIT_ID

    override fun initialize() {
        if (consentInformation.canRequestAds()) {
            initializeMobileAdsSdk()
        } else {
            Log.d(TAG, "AdMob SDK initialization deferred: consent not gathered or canRequestAds is false.")
        }
    }

    private fun initializeMobileAdsSdk() {
        if (adUnitId.isBlank()) {
            Log.w(TAG, "AdMob Interstitial Ad Unit ID is blank. Initialization skipped.")
            return
        }

        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return
        }

        try {
            if (BuildConfig.DEBUG) {
                val requestConfiguration = RequestConfiguration.Builder()
                    .setTestDeviceIds(listOf(AdRequest.DEVICE_ID_EMULATOR))
                    .build()
                MobileAds.setRequestConfiguration(requestConfiguration)
            }

            MobileAds.initialize(context) { status ->
                Log.d(TAG, "AdMob SDK initialized: ${status.adapterStatusMap}")
                loadInterstitialAd()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception initializing AdMob SDK", e)
        }
    }

    override fun gatherConsentAndInitialize(
        activity: Activity,
        onConsentCompleted: () -> Unit,
    ) {
        val paramsBuilder = ConsentRequestParameters.Builder()
        if (BuildConfig.DEBUG) {
            val debugSettings = ConsentDebugSettings.Builder(activity).build()
            paramsBuilder.setConsentDebugSettings(debugSettings)
        }
        val params = paramsBuilder.build()

        consentInformation.requestConsentInfoUpdate(
            activity,
            params,
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { formError ->
                    if (formError != null) {
                        Log.w(TAG, "Consent form error: ${formError.errorCode} - ${formError.message}")
                    }
                    if (consentInformation.canRequestAds()) {
                        initializeMobileAdsSdk()
                    }
                    onConsentCompleted()
                }
            },
            { requestConsentError ->
                Log.w(
                    TAG,
                    "Consent info update failed: ${requestConsentError.errorCode} - ${requestConsentError.message}",
                )
                if (consentInformation.canRequestAds()) {
                    initializeMobileAdsSdk()
                }
                onConsentCompleted()
            },
        )

        if (consentInformation.canRequestAds()) {
            initializeMobileAdsSdk()
        }
    }

    override fun isPrivacyOptionsRequired(): Boolean {
        return consentInformation.privacyOptionsRequirementStatus ==
            ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED
    }

    override fun showPrivacyOptionsForm(
        activity: Activity,
        onDismissed: () -> Unit,
    ) {
        UserMessagingPlatform.showPrivacyOptionsForm(activity) { formError ->
            if (formError != null) {
                Log.w(TAG, "Privacy options form error: ${formError.errorCode} - ${formError.message}")
            }
            if (consentInformation.canRequestAds()) {
                initializeMobileAdsSdk()
                if (interstitialAd == null) {
                    loadInterstitialAd()
                }
            }
            onDismissed()
        }
    }

    override fun loadInterstitialAd() {
        if (adUnitId.isBlank()) return
        if (!consentInformation.canRequestAds()) {
            Log.d(TAG, "Ad preloading skipped: canRequestAds is false.")
            return
        }
        if (interstitialAd != null) {
            Log.d(TAG, "Interstitial ad already cached in memory.")
            return
        }
        if (!isLoadingInterstitial.compareAndSet(false, true)) {
            Log.d(TAG, "Interstitial ad request already in-flight.")
            return
        }

        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            adUnitId,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isLoadingInterstitial.set(false)
                    Log.d(TAG, "Interstitial ad loaded successfully.")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    interstitialAd = null
                    isLoadingInterstitial.set(false)
                    Log.w(TAG, "Interstitial ad failed to load: ${loadAdError.message} (code: ${loadAdError.code})")
                }
            },
        )
    }

    override fun isInterstitialLoaded(): Boolean = interstitialAd != null

    override fun showInterstitial(
        activity: Activity,
        force: Boolean,
        onAdDismissed: () -> Unit,
    ) {
        if (!consentInformation.canRequestAds()) {
            Log.d(TAG, "Ad display skipped: canRequestAds is false.")
            onAdDismissed()
            return
        }

        val currentAd = interstitialAd
        val currentTime = SystemClock.elapsedRealtime()
        val elapsedSinceLastAd = currentTime - lastAdShownTimestamp

        if (!force && lastAdShownTimestamp > 0L && elapsedSinceLastAd < MIN_INTERSTITIAL_INTERVAL_MS) {
            Log.d(
                TAG,
                "Frequency capping active ($elapsedSinceLastAd ms < $MIN_INTERSTITIAL_INTERVAL_MS ms). Skipping ad.",
            )
            onAdDismissed()
            return
        }

        if (currentAd == null) {
            Log.d(TAG, "Interstitial ad not ready. Preloading and continuing game flow.")
            loadInterstitialAd()
            onAdDismissed()
            return
        }

        currentAd.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "Interstitial ad displayed on screen.")
                lastAdShownTimestamp = SystemClock.elapsedRealtime()
                interstitialAd = null
            }

            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "Interstitial ad dismissed by user.")
                interstitialAd = null
                loadInterstitialAd()
                onAdDismissed()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.w(TAG, "Interstitial ad failed to show: ${adError.message}")
                interstitialAd = null
                loadInterstitialAd()
                onAdDismissed()
            }

            override fun onAdClicked() {
                Log.d(TAG, "Interstitial ad clicked.")
            }

            override fun onAdImpression() {
                Log.d(TAG, "Interstitial ad impression recorded.")
            }
        }

        currentAd.show(activity)
    }
}
