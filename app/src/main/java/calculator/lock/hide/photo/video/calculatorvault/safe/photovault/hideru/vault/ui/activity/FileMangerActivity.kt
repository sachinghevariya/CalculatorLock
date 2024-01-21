package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.activity

import android.content.Intent
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.adsModule.setOnClickListener
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.databinding.ActivityFileMangerBinding
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FileMangerActivity : BaseActivity<ActivityFileMangerBinding>() {
    override fun getViewBinding() = ActivityFileMangerBinding.inflate(layoutInflater)
    override fun initData() {
        marqueEnable()
        binding.ivBack.setOnClickListener(1000L) {
            finish()
        }

        binding.cvPhoto.setOnClickListener(1000L) {
            startActivity(Intent(this@FileMangerActivity,HidePhotosActivity::class.java))
        }

        binding.cvVideo.setOnClickListener(1000L) {
            startActivity(Intent(this@FileMangerActivity,HideVideoActivity::class.java))
        }

        binding.cvAudio.setOnClickListener(1000L) {
            startActivity(Intent(this@FileMangerActivity,HideAudioActivity::class.java))
        }

        binding.cvDoc.setOnClickListener(1000L) {
            startActivity(Intent(this@FileMangerActivity,HideDocumentActivity::class.java))
        }

    }

    private fun marqueEnable() {
        binding.tvHeader.isSelected = true
        binding.tvPhotoTitle.isSelected = true
        binding.tvVideoTitle.isSelected = true
        binding.tvAudioTitle.isSelected = true
        binding.tvDocTitle.isSelected = true
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}