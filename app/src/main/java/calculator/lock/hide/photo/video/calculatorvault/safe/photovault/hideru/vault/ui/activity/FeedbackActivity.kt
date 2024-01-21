package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.activity

import android.content.Intent
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.widget.FrameLayout
import android.widget.Toast
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.R
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.adsModule.setOnClickListener
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.databinding.ActivityFeedbackBinding
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.base.BaseActivity
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.gone
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.visible

import dagger.hilt.android.AndroidEntryPoint
import plugin.adsdk.extras.NetworkChangeReceiver

@AndroidEntryPoint
class FeedbackActivity : BaseActivity<ActivityFeedbackBinding>() {

    override fun getViewBinding() = ActivityFeedbackBinding.inflate(layoutInflater)

    var mBody = ""

    private fun marqueEnable() {
        binding.tvHeader.isSelected = true
        binding.textView2.isSelected = true
    }

    override fun initData() {

        try {
            bannerAd()
            marqueEnable()
        } catch (e: Exception) {
            e.printStackTrace()
        }


        binding.ivBack.setOnClickListener(1000L) {
            finish()
        }

        binding.etFeedback.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                try {
                    if (s.toString().length >= 2) {
                        mBody = s.toString()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        })
        binding.btnSend.setOnClickListener(1000L) {
            if (mBody.length > 2) {
                val email = Uri.encode("runner11privatelimited@gmail.com")
                val subject = "Report for issue"
                val body = Uri.encode(mBody)
                val uri = "mailto:$email?subject=$subject&body=$body"
                val intent = Intent(Intent.ACTION_SENDTO)
                intent.type = "text/plain"
                intent.data = Uri.parse(uri)
                startActivity(intent)
            } else {
                Toast.makeText(this, getString(R.string.feedback_toast), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBackPressed() {
        backPressed()
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