package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.activity

import android.app.Activity
import android.content.Intent
import android.widget.FrameLayout
import android.widget.ImageView
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.R
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.adsModule.setOnClickListener
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.databinding.ActivityThemeBinding
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.base.BaseActivity
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.gone
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import plugin.adsdk.extras.NetworkChangeReceiver

@AndroidEntryPoint
class ThemeActivity : BaseActivity<ActivityThemeBinding>() {

    override fun getViewBinding() = ActivityThemeBinding.inflate(layoutInflater)
    private var selectedItem = 0

    private fun marqueEnable() {
        binding.tvHeader.isSelected = true
        binding.tvConfirm.isSelected = true
    }

    override fun initData() {
        bannerAd()
        defaultSelection()
        marqueEnable()

        binding.ivBack.setOnClickListener(1000L) {
            finish()
        }

        binding.btnTheme0.setOnClickListener(1000L) {
            selectedItem = 0
            manageSelection(binding.btnTheme0)
        }
        binding.btnTheme1.setOnClickListener(1000L) {
            selectedItem = 1
            manageSelection(binding.btnTheme1)
        }
        binding.btnTheme2.setOnClickListener(1000L) {
            selectedItem = 2
            manageSelection(binding.btnTheme2)
        }
        binding.btnTheme3.setOnClickListener(1000L) {
            selectedItem = 3
            manageSelection(binding.btnTheme3)
        }

        binding.btnConfirm.setOnClickListener(1000L) {
            myPreferences.themeSelected = "Theme" + selectedItem
            setResult(Activity.RESULT_OK, Intent())
            finish()
        }

    }
    private fun defaultSelection() {
        binding.btnTheme0.setImageResource(R.drawable.img_theme_0_unselected)
        binding.btnTheme1.setImageResource(R.drawable.img_theme_1_unselected)
        binding.btnTheme2.setImageResource(R.drawable.img_theme_2_unselected)
        binding.btnTheme3.setImageResource(R.drawable.img_theme_3_unselected)

        when (myPreferences.themeSelected) {
            "Theme0" -> {
                selectedItem = 0
                binding.btnTheme0.setImageResource(R.drawable.img_theme_0_selected)
            }

            "Theme1" -> {
                selectedItem = 1
                binding.btnTheme1.setImageResource(R.drawable.img_theme_1_selected)
            }

            "Theme2" -> {
                selectedItem = 2
                binding.btnTheme2.setImageResource(R.drawable.img_theme_2_selected)
            }

            "Theme3" -> {
                selectedItem = 3
                binding.btnTheme3.setImageResource(R.drawable.img_theme_3_selected)
            }

            else -> {
                selectedItem = 0
                binding.btnTheme0.setImageResource(R.drawable.img_theme_0_selected)
            }
        }
    }
    private fun manageSelection(ivSelected: ImageView) {
        binding.btnTheme0.setImageResource(R.drawable.img_theme_0_unselected)
        binding.btnTheme1.setImageResource(R.drawable.img_theme_1_unselected)
        binding.btnTheme2.setImageResource(R.drawable.img_theme_2_unselected)
        binding.btnTheme3.setImageResource(R.drawable.img_theme_3_unselected)

        when (selectedItem) {
            0 -> {
                ivSelected.setImageResource(R.drawable.img_theme_0_selected)
            }

            1 -> {
                ivSelected.setImageResource(R.drawable.img_theme_1_selected)
            }

            2 -> {
                ivSelected.setImageResource(R.drawable.img_theme_2_selected)
            }

            3 -> {
                ivSelected.setImageResource(R.drawable.img_theme_3_selected)
            }

            else -> {
                selectedItem = 0
                ivSelected.setImageResource(R.drawable.img_theme_0_selected)
            }
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