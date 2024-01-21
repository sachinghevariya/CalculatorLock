package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.activity

import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.R
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.adsModule.AdsUtils
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.adsModule.setOnClickListener
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.databinding.ActivityHideVideoBinding
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.base.BaseActivity
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.dialog.BsAddVideosDialog
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.fragment.HidePhotoAlbumFragment
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.fragment.HidePhotosFragment
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.gone
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.inVisible
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.showBottomSheet
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import plugin.adsdk.extras.NetworkChangeReceiver

@AndroidEntryPoint
class HideVideoActivity : BaseActivity<ActivityHideVideoBinding>() {

    private lateinit var mActiveFragment: Fragment
    private lateinit var mHidePhotoFragment: HidePhotosFragment
    private lateinit var mHidePhotoAlbumFragment: HidePhotoAlbumFragment

    private fun marqueEnable() {
        binding.tvAllVideos.isSelected = true
        binding.tvAlbums.isSelected = true
    }

    override fun getViewBinding() = ActivityHideVideoBinding.inflate(layoutInflater)

    override fun initData() {
        marqueEnable()
        bannerAd()
        mHidePhotoFragment = HidePhotosFragment(true) { isSelectionMode ->
            if (isSelectionMode) {
                binding.buttonView.gone()
            } else {
                binding.buttonView.visible()
            }
        }
        mHidePhotoAlbumFragment = HidePhotoAlbumFragment(true){isSelectionMode ->
            if (isSelectionMode) {
                binding.buttonView.gone()
            } else {
                binding.buttonView.visible()
            }
        }
        mActiveFragment = mHidePhotoFragment

        supportFragmentManager.beginTransaction()
            .add(R.id.container, mHidePhotoFragment, "mHidePhotoFragment").commit()
        supportFragmentManager.beginTransaction()
            .add(R.id.container, mHidePhotoAlbumFragment, "mMatchesFragment")
            .hide(mHidePhotoAlbumFragment).commit()

        binding.tvAllVideos.setOnClickListener(1000L) {
            setButtonClick("Videos")
        }

        binding.tvAlbums.setOnClickListener(1000L) {
            setButtonClick("Albums")
        }

        binding.ivAdd.setOnClickListener(1000L) {
            showBottomSheet(this, BsAddVideosDialog())
        }
    }


    private fun setButtonClick(mode: String) {
        when (mode) {
            "Videos" -> {
                showFragment(mHidePhotoFragment)
                manageBottomClick(
                    binding.tvAllVideos, binding.viewPhotosSelected
                )
                AdsUtils.logItemClick(this, "BottomBar", "VideosClicked")
            }

            "Albums" -> {
                showFragment(mHidePhotoAlbumFragment)
                manageBottomClick(
                    binding.tvAlbums, binding.viewAlbumSelected
                )
                AdsUtils.logItemClick(this, "BottomBar", "AlbumsClicked")
            }
        }
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().hide(mActiveFragment).show(fragment).commit()
        mActiveFragment = fragment
    }

    private fun manageBottomClick(tvActive: TextView, view: View) {
        binding.tvAllVideos.setTextColor(ContextCompat.getColor(this, R.color.textColorGray))
        binding.tvAlbums.setTextColor(ContextCompat.getColor(this, R.color.textColorGray))
        binding.viewPhotosSelected.inVisible()
        binding.viewAlbumSelected.inVisible()
        tvActive.setTextColor(ContextCompat.getColor(this, R.color.textColorWhite))
        view.visible()
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