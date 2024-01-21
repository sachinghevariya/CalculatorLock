package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.activity

import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.R
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.adsModule.AdsUtils
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.adsModule.setOnClickListener
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.databinding.ActivityHidePhotosBinding
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.base.BaseActivity
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.dialog.BsAddPhotosDialog
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.fragment.HidePhotoAlbumFragment
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.fragment.HidePhotosFragment
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.gone
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.inVisible
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.showBottomSheet
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import plugin.adsdk.extras.NetworkChangeReceiver

@AndroidEntryPoint
class HidePhotosActivity : BaseActivity<ActivityHidePhotosBinding>() {

    private lateinit var mActiveFragment: Fragment
    private lateinit var mHidePhotoFragment: HidePhotosFragment
    private lateinit var mHidePhotoAlbumFragment: HidePhotoAlbumFragment

    override fun getViewBinding() = ActivityHidePhotosBinding.inflate(layoutInflater)

    private fun marqueEnable() {
        binding.tvAllPhotos.isSelected = true
        binding.tvAlbums.isSelected = true
    }

    override fun initData() {
        marqueEnable()
        bannerAd()
        mHidePhotoFragment = HidePhotosFragment { isSelectionMode ->
            if (isSelectionMode) {
                binding.buttonView.gone()
            } else {
                binding.buttonView.visible()
            }
        }
        mHidePhotoAlbumFragment = HidePhotoAlbumFragment { isSelectionMode ->
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
            .add(R.id.container, mHidePhotoAlbumFragment, "mHidePhotoAlbumFragment")
            .hide(mHidePhotoAlbumFragment).commit()

        binding.tvAllPhotos.setOnClickListener(1000L) {
            setButtonClick("Photos")
        }

        binding.tvAlbums.setOnClickListener(1000L) {
            setButtonClick("Albums")
        }

        binding.ivAdd.setOnClickListener(1000L) {
            showBottomSheet(this, BsAddPhotosDialog())
        }
    }


    private fun setButtonClick(mode: String) {
        when (mode) {
            "Photos" -> {
                showFragment(mHidePhotoFragment)
                manageBottomClick(
                    binding.tvAllPhotos, binding.viewPhotosSelected
                )
                AdsUtils.logItemClick(this, "BottomBar", "PhotosClicked")
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
        binding.tvAllPhotos.setTextColor(ContextCompat.getColor(this, R.color.textColorGray))
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