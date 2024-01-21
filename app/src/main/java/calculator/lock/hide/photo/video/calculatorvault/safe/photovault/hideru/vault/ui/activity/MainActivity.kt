package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.activity

import android.content.Intent
import android.graphics.LinearGradient
import android.graphics.Shader
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.R
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.adsModule.setOnClickListener
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.databinding.ActivityMainBinding
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.base.BaseActivity
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.viewmodels.FileManagerViewModel
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.gone
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import plugin.adsdk.extras.NetworkChangeReceiver

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    private val viewModel: FileManagerViewModel by viewModels()


    override fun getViewBinding() = ActivityMainBinding.inflate(layoutInflater)

    override fun initData() {
        bannerAd()
        marqueEnable()
        binding.btnPhotos.setOnClickListener(1000L) {
            startActivity(Intent(this@MainActivity, HidePhotosActivity::class.java))
        }
        binding.btnVideos.setOnClickListener(1000L) {
            startActivity(Intent(this@MainActivity, HideVideoActivity::class.java))
        }
        binding.btnSettings.setOnClickListener(1000L) {
            startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
        }
        binding.btnTrash.setOnClickListener(1000L) {
            startActivity(Intent(this@MainActivity, TrashActivity::class.java))
        }
        binding.btnNotes.setOnClickListener(1000L) {
            startActivity(Intent(this@MainActivity, NotesActivity::class.java))
        }
        binding.btnAudio.setOnClickListener(1000L) {
            startActivity(Intent(this@MainActivity, HideAudioActivity::class.java))
        }
        binding.btnDoc.setOnClickListener(1000L) {
            startActivity(Intent(this@MainActivity, HideDocumentActivity::class.java))
        }

        viewModel.trashData()
        lifecycleScope.launch {
            viewModel.getCount.observe(this@MainActivity) {
                binding.tvNotesCount.text = "${it.first} ${getString(R.string.files)}"
                binding.tvAudioCount.text = "${it.second} ${getString(R.string.files)}"
            }
            viewModel.mergedTrashItems.observe(this@MainActivity) {
                binding.tvTrashCount.text = "${it.size} ${getString(R.string.files)}"
            }
        }

    }

    private fun marqueEnable() {
        binding.tvNotes.isSelected = true
        binding.tvTrash.isSelected = true
        binding.tvPhotos.isSelected = true
        binding.tvVideos.isSelected = true
        binding.tvSettings.isSelected = true
        binding.tvNotesCount.isSelected = true
        binding.tvTrashCount.isSelected = true
        binding.tvAudioCount.isSelected = true
    }

    private fun setGradientText() {
        val startColor = ContextCompat.getColor(this, R.color.colorStart)
        val endColor = ContextCompat.getColor(this, R.color.colorEnd)
        val text = getString(R.string.lock)
        val spannable = SpannableString(text)
        val shader = LinearGradient(
            0f,
            0f,
            0f,
            binding.tvHeaderSub.height.toFloat(),
            startColor,
            endColor,
            Shader.TileMode.CLAMP
        )
        spannable.setSpan(
            ForegroundColorSpan(startColor),
            0,
            text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.tvHeaderSub.paint.shader = shader
        binding.tvHeaderSub.text = spannable
    }

    override fun onBackPressed() {
        showExitDialog()
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