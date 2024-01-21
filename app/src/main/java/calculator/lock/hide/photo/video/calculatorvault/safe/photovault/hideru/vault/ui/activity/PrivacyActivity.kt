package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.activity

import android.content.Intent
import android.util.Log
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.R
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.adsModule.setOnClickListener
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.databinding.ActivityPrivacyBinding
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.base.BaseActivity
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.dialog.GrantPermissionDialog
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.showMyDialog
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.showToast
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import dagger.hilt.android.AndroidEntryPoint
import kotlin.system.exitProcess

@AndroidEntryPoint
class PrivacyActivity : BaseActivity<ActivityPrivacyBinding>() {
    override fun getViewBinding() = ActivityPrivacyBinding.inflate(layoutInflater)

    private fun marqueEnable() {
        binding.tvWelcome.isSelected = true
        binding.tvApp.isSelected = true
        binding.tvTitle.isSelected = true
        binding.tvConfirm.isSelected = true
        binding.btnDisagree.isSelected = true
    }

    override fun initData() {
        if (myPreferences.isPrivacyAccepted) {
            if (!myPreferences.languageShown) {
                startActivity(
                    Intent(
                        this@PrivacyActivity,
                        SelectLanguageActivity::class.java
                    ).putExtra("isFromSplash", true)
                )
                finish()
                return
            }else {
                startActivity(Intent(this@PrivacyActivity, CalculatorActivity::class.java))
                finish()
                return
            }
        }
        marqueEnable()
        binding.btnAgree.setOnClickListener(1000L) {
            myPreferences.isPrivacyAccepted = true
            if (!isFinishing && !isDestroyed) {
                showMyDialog(this@PrivacyActivity, GrantPermissionDialog {
                    XXPermissions.with(this@PrivacyActivity)
                        .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                        .request(object : OnPermissionCallback {
                            override fun onGranted(
                                permissions: List<String?>,
                                allGranted: Boolean
                            ) {
                                if (!allGranted) {
                                    return
                                }

                                if (!myPreferences.languageShown) {
                                    startActivity(
                                        Intent(
                                            this@PrivacyActivity,
                                            SelectLanguageActivity::class.java
                                        ).putExtra("isFromSplash", true)
                                    )
                                    finish()
                                    return
                                }

                                startActivity(
                                    Intent(
                                        this@PrivacyActivity,
                                        CalculatorActivity::class.java
                                    )
                                )
                                finish()
                            }
                        })
                })
            }
        }

        binding.btnDisagree.setOnClickListener(1000L) {
            myPreferences.isPrivacyAccepted = false
            showToast(getString(R.string.msg_privacy))
        }

    }

    override fun onBackPressed() {
        finishAffinity()
        exitProcess(0)
    }

}