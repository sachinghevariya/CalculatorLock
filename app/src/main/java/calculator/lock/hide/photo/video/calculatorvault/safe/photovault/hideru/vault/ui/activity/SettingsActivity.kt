package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.activity

import android.content.Intent
import android.widget.FrameLayout
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.R
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.adsModule.AdsUtils
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.adsModule.setOnClickListener
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.databinding.ActivitySettingsBinding
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.base.BaseActivity
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.gone
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import plugin.adsdk.extras.NetworkChangeReceiver
import plugin.adsdk.service.AdsUtility

@AndroidEntryPoint
class SettingsActivity : BaseActivity<ActivitySettingsBinding>() {

    override fun getViewBinding() = ActivitySettingsBinding.inflate(layoutInflater)

    private fun marqueEnable() {
        binding.tvHeader.isSelected = true
        binding.tvSecurity.isSelected = true
        binding.tvSetSecurity.isSelected = true
        binding.tvResetPass.isSelected = true
        binding.tvOther.isSelected = true
        binding.tvLanguage.isSelected = true
        binding.tvRate.isSelected = true
        binding.tvFeedback.isSelected = true
        binding.tvShare.isSelected = true
        binding.tvPrivacyTerms.isSelected = true
    }

    override fun initData() {
        marqueEnable()
        bannerAd()

        binding.ivBack.setOnClickListener(1000L) {
            finish()
        }

        binding.btnSetSecurity.setOnClickListener(1000L) {
            startActivity(Intent(this@SettingsActivity, SetSecurityQuestionActivity::class.java))
        }

        binding.btnDisguise.setOnClickListener(1000L) {
            startActivity(Intent(this@SettingsActivity, DisguiseActivity::class.java))
        }

        binding.btnResetPass.setOnClickListener(1000L) {
            startActivity(Intent(this@SettingsActivity, CalculatorActivity::class.java).putExtra("isReset",true))
        }

        binding.btnLanguage.setOnClickListener(1000L) {
            startActivity(Intent(this@SettingsActivity, SelectLanguageActivity::class.java))
        }

        binding.btnRate.setOnClickListener(1000L) {
            AdsUtility.rateUs(this@SettingsActivity)
        }

        binding.btnFeedback.setOnClickListener(1000L) {
            startActivity(Intent(this@SettingsActivity,FeedbackActivity::class.java))
        }

        binding.btnShare.setOnClickListener(1000L) {
            AdsUtility.shareApp(this@SettingsActivity)
        }

        binding.btnPrivacy.setOnClickListener(1000L) {
            AdsUtility.privacyPolicy(this@SettingsActivity)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun networkStateChanged(state: NetworkChangeReceiver.NetworkState?) {
        super.networkStateChanged(state)
        if (state == NetworkChangeReceiver.NetworkState.CONNECTED) {
            binding.root.findViewById<FrameLayout>(R.id.banner_ad_container).visible()
        } else if (state == NetworkChangeReceiver.NetworkState.NOT_CONNECTED) {
            binding.root.findViewById<FrameLayout>(R.id.banner_ad_container).gone()
        }
    }
}