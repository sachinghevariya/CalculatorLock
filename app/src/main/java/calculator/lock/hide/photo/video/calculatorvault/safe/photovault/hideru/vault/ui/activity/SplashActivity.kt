package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.activity

import android.content.Intent
import android.os.Build
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.R
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.MyPreferences
import dagger.hilt.android.AndroidEntryPoint
import plugin.adsdk.extras.BaseLauncherActivity
import plugin.adsdk.service.AppOpenManager

@AndroidEntryPoint
class SplashActivity : BaseLauncherActivity(R.layout.activity_splash) {
    override fun onBackPressed() {}

    override fun init() {
        AppOpenManager.blockAppOpen(this)
//        installSplashScreen()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            installSplashScreen()

            installSplashScreen().setKeepOnScreenCondition { true }
        }
    }

    override fun destinationIntent(): Intent {
        val myPreferences = MyPreferences(this)
        return if (myPreferences.isPrivacyAccepted) {
            if (!myPreferences.languageShown) {
                Intent(
                    this@SplashActivity,
                    SelectLanguageActivity::class.java
                ).putExtra("isFromSplash", true)
            } else {
                Intent(this@SplashActivity, CalculatorActivity::class.java).putExtra(
                    "isFromLock",
                    false
                )
            }
        } else {
            Intent(this@SplashActivity, PrivacyActivity::class.java)
        }
    }

    override fun baseURL(): String {
        return "https://ht.askforad.com/"
    }

}