package com.example.promptxo.ui.components

import android.content.Context
import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.promptxo.ads.AdMobConfig
import com.example.ui.theme.PromptXoAdBg
import com.example.ui.theme.PromptXoBorder
import com.example.ui.theme.PromptXoPrimary
import com.example.ui.theme.PromptXoTextMuted
import com.example.ui.theme.PromptXoTextSecondary
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView

@Composable
fun PromptXoNativeAd(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var loadedNativeAd by remember { mutableStateOf<NativeAd?>(null) }
    var isFailedToLoad by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        fun requestAd() {
            val adLoader = AdLoader.Builder(context, AdMobConfig.NATIVE_AD_UNIT_ID)
                .forNativeAd { ad: NativeAd ->
                    loadedNativeAd = ad
                    isFailedToLoad = false
                }
                .withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(error: LoadAdError) {
                        android.util.Log.w("PromptXoAd", "Native ad failed to load: ${error.code} - ${error.message}")
                        isFailedToLoad = true
                    }
                })
                .withNativeAdOptions(NativeAdOptions.Builder().build())
                .build()

            adLoader.loadAd(AdRequest.Builder().build())
        }

        requestAd()

        onDispose {
            loadedNativeAd?.destroy()
        }
    }

    val currentAd = loadedNativeAd

    if (currentAd != null) {
        // Render AdMob NativeAdView with dark theme styling
        AndroidView(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            factory = { ctx ->
                createNativeAdView(ctx, currentAd)
            },
            update = { view ->
                populateNativeAd(view, currentAd)
            }
        )
    } else if (!isFailedToLoad) {
        // Subtle loading box while Google AdMob server fetches your real ad
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(64.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(PromptXoAdBg)
                .border(1.dp, PromptXoBorder, RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFFEAB308))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Ad",
                        color = Color.Black,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Sponsored",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Google AdMob",
                        color = PromptXoTextMuted,
                        fontSize = 11.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(PromptXoPrimary.copy(alpha = 0.3f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Ad",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

private fun createNativeAdView(context: Context, nativeAd: NativeAd): NativeAdView {
    val nativeAdView = NativeAdView(context).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    val rootCard = LinearLayout(context).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        setBackgroundColor(android.graphics.Color.parseColor("#0F1420"))
        val pad = dpToPx(context, 12)
        setPadding(pad, pad, pad, pad)
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    // App Icon
    val iconView = ImageView(context).apply {
        id = View.generateViewId()
        val iconSize = dpToPx(context, 44)
        layoutParams = LinearLayout.LayoutParams(iconSize, iconSize).apply {
            marginEnd = dpToPx(context, 10)
        }
        scaleType = ImageView.ScaleType.FIT_CENTER
    }
    nativeAdView.iconView = iconView
    rootCard.addView(iconView)

    // Center Info Column
    val textCol = LinearLayout(context).apply {
        orientation = LinearLayout.VERTICAL
        layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
    }

    // Top row with "Ad" badge and Headline
    val topRow = LinearLayout(context).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
    }

    val adBadge = TextView(context).apply {
        text = "Ad"
        setTextColor(android.graphics.Color.BLACK)
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
        setTypeface(null, Typeface.BOLD)
        setBackgroundColor(android.graphics.Color.parseColor("#EAB308"))
        val p = dpToPx(context, 3)
        setPadding(p * 2, p, p * 2, p)
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            marginEnd = dpToPx(context, 6)
        }
    }
    topRow.addView(adBadge)

    val headlineView = TextView(context).apply {
        id = View.generateViewId()
        setTextColor(android.graphics.Color.WHITE)
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
        setTypeface(null, Typeface.BOLD)
        maxLines = 1
        ellipsize = android.text.TextUtils.TruncateAt.END
    }
    nativeAdView.headlineView = headlineView
    topRow.addView(headlineView)
    textCol.addView(topRow)

    // Body
    val bodyView = TextView(context).apply {
        id = View.generateViewId()
        setTextColor(android.graphics.Color.parseColor("#94A3B8"))
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
        maxLines = 1
        ellipsize = android.text.TextUtils.TruncateAt.END
    }
    nativeAdView.bodyView = bodyView
    textCol.addView(bodyView)

    rootCard.addView(textCol)

    // Call to action button
    val ctaButton = Button(context).apply {
        id = View.generateViewId()
        setTextColor(android.graphics.Color.WHITE)
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
        setTypeface(null, Typeface.BOLD)
        setBackgroundColor(android.graphics.Color.parseColor("#7C3AED"))
        val btnPadH = dpToPx(context, 10)
        val btnPadV = dpToPx(context, 4)
        setPadding(btnPadH, btnPadV, btnPadH, btnPadV)
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            dpToPx(context, 36)
        ).apply {
            marginStart = dpToPx(context, 8)
        }
    }
    nativeAdView.callToActionView = ctaButton
    rootCard.addView(ctaButton)

    nativeAdView.addView(rootCard)
    populateNativeAd(nativeAdView, nativeAd)

    return nativeAdView
}

private fun populateNativeAd(nativeAdView: NativeAdView, nativeAd: NativeAd) {
    (nativeAdView.headlineView as? TextView)?.text = nativeAd.headline ?: "Sponsored"
    (nativeAdView.bodyView as? TextView)?.text = nativeAd.body ?: ""

    val cta = nativeAdView.callToActionView as? Button
    if (nativeAd.callToAction != null && cta != null) {
        cta.visibility = View.VISIBLE
        cta.text = nativeAd.callToAction
    } else {
        cta?.visibility = View.GONE
    }

    val iconView = nativeAdView.iconView as? ImageView
    if (nativeAd.icon != null && iconView != null) {
        iconView.visibility = View.VISIBLE
        iconView.setImageDrawable(nativeAd.icon?.drawable)
    } else {
        iconView?.visibility = View.GONE
    }

    nativeAdView.setNativeAd(nativeAd)
}

private fun dpToPx(context: Context, dp: Int): Int {
    return (dp * context.resources.displayMetrics.density).toInt()
}
