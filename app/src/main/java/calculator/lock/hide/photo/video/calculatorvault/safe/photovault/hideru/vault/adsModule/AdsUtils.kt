package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.adsModule

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.R
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.base.BaseActivity
import com.google.firebase.analytics.FirebaseAnalytics


object AdsUtils {

    fun logInterstitial(activity: Activity, source: String) {
        val params = Bundle()
        params.putString("ad_type", "interstitial")
        params.putString("source", activity.localClassName + "@" + source)
        FirebaseAnalytics.getInstance(activity).logEvent("ads_interstitial_track", params)
    }

    fun logBanner(activity: Activity, source: String) {
        val params = Bundle()
        params.putString("ad_type", "banner")
        params.putString("source", activity.localClassName + "@" + source)
        FirebaseAnalytics.getInstance(activity).logEvent("ads_interstitial_track", params)
    }

    fun logNative(activity: Activity, source: String) {
        val params = Bundle()
        params.putString("ad_type", "native")
        params.putString("source", activity.localClassName + "@" + source)
        FirebaseAnalytics.getInstance(activity).logEvent("ads_interstitial_track", params)
    }

    fun logAppOpen(activity: Activity, source: String) {
        val params = Bundle()
        params.putString("ad_type", "app_open")
        params.putString("source", activity.localClassName + "@" + source)
        FirebaseAnalytics.getInstance(activity).logEvent("ads_interstitial_track", params)
    }

    fun logItemClick(activity: Activity, screenType: String, source: String) {
        val params = Bundle()
        params.putString("click_type", screenType)
        params.putString("source", activity.localClassName + "@" + source)
        FirebaseAnalytics.getInstance(activity).logEvent("item_track", params)
    }

    fun logActivityOpen(activity: Activity, screenType: String, source: String) {
        val params = Bundle()
        params.putString("screen_type", screenType)
        params.putString("source", activity.localClassName + "@" + source)
        FirebaseAnalytics.getInstance(activity).logEvent("activity_track", params)
    }

    fun logFragmentOpen(activity: Activity, screenType: String, source: String) {
        val params = Bundle()
        params.putString("screen_type", screenType)
        params.putString("source", activity.localClassName + "@" + source)
        FirebaseAnalytics.getInstance(activity).logEvent("fragment_track", params)
    }
}