package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.activity

import android.content.ComponentName
import android.content.pm.PackageManager
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.adsModule.setOnClickListener
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.databinding.ActivityDisguiseBinding
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DisguiseActivity : BaseActivity<ActivityDisguiseBinding>() {
    override fun getViewBinding() = ActivityDisguiseBinding.inflate(layoutInflater)

    private lateinit var inActive: List<String>
    override fun initData() {
        /*inActive = arrayListOf("SplashActivity11","SplashActivity10","ui.activity.PrivacyActivity")

        binding.iv1.setOnClickListener(1000L) {
            val active = "SplashActivity11"
            manageDisguise(inActive,active)
        }

        binding.iv2.setOnClickListener(1000L) {
            val active = "SplashActivity10"
            manageDisguise(inActive,active)
        }

        binding.iv3.setOnClickListener(1000L) {
            val active = "ui.activity.PrivacyActivity"
            manageDisguise(inActive,active)
        }

        binding.iv4.setOnClickListener(1000L) {

        }

        binding.iv5.setOnClickListener(1000L) {

        }

        binding.iv6.setOnClickListener(1000L) {

        }*/
    }

    private fun manageDisguise(inActive:List<String>, active:String){
        inActive.forEach{
            val componentName1 = ComponentName(this, "$packageName.$it")
            packageManager.setComponentEnabledSetting(componentName1, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
        }
        val componentName2 = ComponentName(this, "$packageName.$active")
        packageManager.setComponentEnabledSetting(componentName2, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP)

    }

    override fun onBackPressed() {
        backPressed()
    }
}