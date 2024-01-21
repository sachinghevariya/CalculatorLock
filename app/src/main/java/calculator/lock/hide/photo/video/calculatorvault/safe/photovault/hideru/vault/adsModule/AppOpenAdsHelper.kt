package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.adsModule

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.R
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.activity.CalculatorActivity
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.activity.PrivacyActivity
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.activity.SelectLanguageActivity
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.activity.SplashActivity
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.activity.ThemeActivity
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.MyPreferences
import com.google.android.gms.ads.AdActivity

class AppOpenAdsHelper() : LifecycleObserver,
    ActivityLifecycleCallbacks {

    private var backgroundView: View? = null
    private var currentActivity: Activity? = null

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        if (currentActivity is SplashActivity || currentActivity is CalculatorActivity || currentActivity is PrivacyActivity || currentActivity is ThemeActivity|| currentActivity is AdActivity) {
            hideCustomBackground()
        } else {
            showLockScreen()
//            showAdIfAvailable()
        }
    }


    private fun showLockScreen() {
        if(currentActivity is SelectLanguageActivity && !MyPreferences(currentActivity!!).languageShown){
            return
        }
        currentActivity?.startActivity(
            Intent(
                currentActivity,
                CalculatorActivity::class.java
            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("isFromLock", true)
        )
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
        if (currentActivity is CalculatorActivity){
            hideCustomBackground()
        }else{
            hideCustomBackground()
        }
    }

    override fun onActivityPaused(activity: Activity) {
        if (currentActivity is SplashActivity || currentActivity is CalculatorActivity || currentActivity is PrivacyActivity || currentActivity is ThemeActivity || currentActivity is AdActivity) {
            Handler(Looper.getMainLooper()).postDelayed({
                hideCustomBackground()
            }, 200)
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                showCustomBackground(activity)
            }, 200)
        }
    }

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {
        currentActivity = null
    }

    private fun showCustomBackground(activity: Activity) {
        if (backgroundView == null) {
            // Inflate the custom background layout
            backgroundView = LayoutInflater.from(activity).inflate(R.layout.layout_background, null)
            val params = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            activity.window.addContentView(backgroundView, params)
        }
    }

    fun hideCustomBackground() {
        backgroundView?.let {
            val parent = it.parent as? ViewGroup
            parent?.removeView(it)
            backgroundView = null
        }
    }
}