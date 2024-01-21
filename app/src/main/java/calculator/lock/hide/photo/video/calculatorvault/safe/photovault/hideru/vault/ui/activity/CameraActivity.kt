package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.FrameLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.R
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.databinding.ActivityCameraBinding
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.base.BaseActivity
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.fragment.CameraFragment
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.gone
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.showToast
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import plugin.adsdk.extras.NetworkChangeReceiver

@AndroidEntryPoint
class CameraActivity : BaseActivity<ActivityCameraBinding>() {

    val PERMISSIONS_REQUIRED = arrayOf(Manifest.permission.CAMERA)

    override fun getViewBinding() = ActivityCameraBinding.inflate(layoutInflater)

    override fun initData() {
        checkRunTimePermission(Manifest.permission.CAMERA){
            bannerAd()
            showFragment(CameraFragment())
        }
    }

    private fun hasPermissions(context: Context) = PERMISSIONS_REQUIRED.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CODE_PERMISSION){
            if (!hasPermissions(this)) {
                finish()
            } else {
                bannerAd()
                showFragment(CameraFragment())
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