package com.example.promptxo.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object AdMobConfig {
    // Production AdMob Ad Unit IDs
    var INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-8106043013088313/2147203021"
    var REWARDED_AD_UNIT_ID = "ca-app-pub-8106043013088313/2881750523"
    var NATIVE_AD_UNIT_ID = "ca-app-pub-8106043013088313/5440801967"
    var BANNER_AD_UNIT_ID = "ca-app-pub-8106043013088313/5440801967"
}

class AdMobManager(private val context: Context) {
    private val TAG = "AdMobManager"

    private var interstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null

    private var isAdMobInitialized = false
    private var postClickCount = 0

    // State for interactive ad simulation overlay if SDK ad has not loaded yet
    private val _adDialogState = MutableStateFlow<AdDialogState?>(null)
    val adDialogState: StateFlow<AdDialogState?> = _adDialogState.asStateFlow()

    sealed class AdDialogState {
        data class Interstitial(val onDismiss: () -> Unit) : AdDialogState()
        data class Rewarded(val title: String, val onRewarded: () -> Unit, val onDismiss: () -> Unit) : AdDialogState()
    }

    init {
        try {
            MobileAds.initialize(context) { status ->
                Log.d(TAG, "MobileAds initialized: $status")
                isAdMobInitialized = true
                loadInterstitialAd()
                loadRewardedAd()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize MobileAds", e)
        }
    }

    fun loadInterstitialAd() {
        try {
            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(
                context,
                AdMobConfig.INTERSTITIAL_AD_UNIT_ID,
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: InterstitialAd) {
                        interstitialAd = ad
                        Log.d(TAG, "Interstitial Ad loaded")
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        interstitialAd = null
                        Log.w(TAG, "Interstitial failed to load: ${loadAdError.message}")
                    }
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Exception loading interstitial", e)
        }
    }

    fun loadRewardedAd() {
        try {
            val adRequest = AdRequest.Builder().build()
            RewardedAd.load(
                context,
                AdMobConfig.REWARDED_AD_UNIT_ID,
                adRequest,
                object : RewardedAdLoadCallback() {
                    override fun onAdLoaded(ad: RewardedAd) {
                        rewardedAd = ad
                        Log.d(TAG, "Rewarded Ad loaded")
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        rewardedAd = null
                        Log.w(TAG, "Rewarded ad failed to load: ${loadAdError.message}")
                    }
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Exception loading rewarded ad", e)
        }
    }

    /**
     * Requirement:
     * "jab hum pehli bar ek post pe click karenge to post open ho jayenge,
     * jab lekin jab second bar hum ek post pe click karenge to humein interstitial ad show hoga"
     * Every 2nd click triggers the interstitial ad!
     */
    fun handlePostClick(activity: Activity?, onProceed: () -> Unit) {
        postClickCount++
        if (postClickCount % 2 == 0) {
            // Show interstitial ad
            showInterstitialAd(activity, onProceed)
        } else {
            // First/odd click opens directly
            onProceed()
        }
    }

    fun showInterstitialAd(activity: Activity?, onAdClosed: () -> Unit) {
        val currentAd = interstitialAd
        if (activity != null && !activity.isFinishing && !activity.isDestroyed && currentAd != null) {
            currentAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    loadInterstitialAd()
                    onAdClosed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.w(TAG, "Interstitial failed to show: ${adError.message}")
                    interstitialAd = null
                    loadInterstitialAd()
                    onAdClosed()
                }
            }
            currentAd.show(activity)
        } else {
            // Ad not ready in cache yet: immediately proceed so user is NEVER delayed!
            // Start background preload for future clicks
            loadInterstitialAd()
            onAdClosed()
        }
    }

    fun showRewardedAd(activity: Activity?, title: String = "Watch Ad to Unlock", onRewarded: () -> Unit) {
        val currentAd = rewardedAd
        if (activity != null && !activity.isFinishing && !activity.isDestroyed && currentAd != null) {
            currentAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    rewardedAd = null
                    loadRewardedAd()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.w(TAG, "Rewarded ad failed to show: ${adError.message}")
                    rewardedAd = null
                    loadRewardedAd()
                    onRewarded()
                }
            }
            currentAd.show(activity) { _ ->
                onRewarded()
            }
        } else {
            // Ad was not preloaded yet: request real AdMob rewarded ad immediately
            try {
                val adRequest = AdRequest.Builder().build()
                RewardedAd.load(
                    context,
                    AdMobConfig.REWARDED_AD_UNIT_ID,
                    adRequest,
                    object : RewardedAdLoadCallback() {
                        override fun onAdLoaded(ad: RewardedAd) {
                            rewardedAd = ad
                            if (activity != null && !activity.isFinishing && !activity.isDestroyed) {
                                ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                                    override fun onAdDismissedFullScreenContent() {
                                        rewardedAd = null
                                        loadRewardedAd()
                                    }

                                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                        rewardedAd = null
                                        loadRewardedAd()
                                        onRewarded()
                                    }
                                }
                                ad.show(activity) {
                                    onRewarded()
                                }
                            } else {
                                onRewarded()
                            }
                        }

                        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                            Log.w(TAG, "Rewarded ad failed to load on-demand: ${loadAdError.message}")
                            rewardedAd = null
                            onRewarded()
                        }
                    }
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error showing rewarded ad", e)
                onRewarded()
            }
        }
    }

    fun dismissAdDialog() {
        _adDialogState.value = null
    }
}
