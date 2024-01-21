package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.activity

import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.R
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.databinding.ActivityTrashBinding
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.base.BaseActivity
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.fragment.TrashFragment
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.gone
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import plugin.adsdk.extras.NetworkChangeReceiver

@AndroidEntryPoint
class TrashActivity : BaseActivity<ActivityTrashBinding>() {
    override fun getViewBinding() = ActivityTrashBinding.inflate(layoutInflater)

    override fun initData() {
        bannerAd()
        showFragment(TrashFragment())
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
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