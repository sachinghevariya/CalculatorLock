package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.activity

import android.app.Activity
import android.content.Intent
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.viewpager2.widget.ViewPager2
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.R
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.adsModule.setOnClickListener
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.databinding.ActivityImageViewerBinding
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.adapter.PagerAdapter
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.base.BaseActivity
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.dialog.BsHideFolderListDialog
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.dialog.TrashMessageDialog
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.dialog.UnHideLoadingDialog
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.MediaItem
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.viewmodels.FileManagerViewModel
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.CommonClass
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.gone
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.inVisible
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.shareMultipleMediaItem
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.showBottomSheet
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.showMyDialog
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.showToast
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import plugin.adsdk.extras.NetworkChangeReceiver

@AndroidEntryPoint
class ImageViewerActivity : BaseActivity<ActivityImageViewerBinding>() {

    private lateinit var imageViewPagerAdapter: PagerAdapter
    private val viewModel: FileManagerViewModel by viewModels()
    private var selectedList = ArrayList<MediaItem>()
    private var currentFolderList = ArrayList<MediaItem>()

    override fun getViewBinding() = ActivityImageViewerBinding.inflate(layoutInflater)
    private var selectedItemPos = 0
    private var nextItemPos = 0

    private fun marqueEnable() {
        binding.tvHeader.isSelected = true
        binding.tvShare.isSelected = true
        binding.tvDelete.isSelected = true
        binding.tvMove.isSelected = true
        binding.tvUnhide.isSelected = true
    }

    override fun initData() {
        marqueEnable()
        bannerAd()
        val isVideo = intent.getBooleanExtra("isVideo", false)

        if (isVideo) {
            binding.tvHeader.text = getString(R.string.videos)
        } else {
            binding.tvHeader.text = getString(R.string.photos)
        }

        val item = arrayListOf<MediaItem>()
        item.clear()
        item.addAll(CommonClass.pagerList)
        if (item.isEmpty()) {
            Toast.makeText(this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT)
                .show()
            return
        }
        selectedItemPos = intent.getIntExtra("selectedItemPos", 0)
        nextItemPos = selectedItemPos + 1
        currentFolderList.clear()
        currentFolderList.addAll(item)
        selectedList.clear()
        selectedList.add(item[selectedItemPos])
        imageViewPagerAdapter =
            PagerAdapter("", currentFolderList, isVideo) { mediaItem, isShowMenu ->
                selectedList.clear()
                selectedList.add(mediaItem)
                if (isShowMenu) {
                    binding.buttonView.visible()
                } else {
                    binding.buttonView.inVisible()
                }
            }
        setUpViewPager()

        binding.ivBack.setOnClickListener(1000L) {
            imageViewPagerAdapter.stopVideo(selectedItemPos)
            finish()
        }
        binding.btnMove.setOnClickListener(1000L) {
            showBottomSheet(this,
                BsHideFolderListDialog(true, isVideo = isVideo) { dbFolderName, dbFolderId ->
                    if (dbFolderName == selectedList[0].folderName) {
                        showToast(getString(R.string.cant_move_same_folder))
                    } else {
                        CoroutineScope(Dispatchers.IO).launch {
                            val selectedListCopy = ArrayList(selectedList)
                            val deferredList = selectedListCopy.map { mediaItem ->
                                async(Dispatchers.IO) {
                                    mediaItem.folderName = dbFolderName
                                    mediaItem.folderNameId = dbFolderId
                                    viewModel.updateMediaItemToDb(mediaItem)
                                    val positionToDelete = currentFolderList.indexOf(mediaItem)
                                    currentFolderList.remove(mediaItem)
                                    nextItemPos = positionToDelete
                                    if (nextItemPos >= currentFolderList.size) {
                                        nextItemPos = currentFolderList.size - 1
                                    }
                                    if (nextItemPos == -1) {
                                        nextItemPos = 0
                                    }
                                    selectedItemPos = nextItemPos
                                }
                            }
                            deferredList.awaitAll()
                            withContext(Dispatchers.Main) {
                                if (currentFolderList.size == 0) {
                                    onBackPressed()
                                } else {
                                    updateImageList()
                                }
                            }
                        }
                    }
                })
        }
        binding.btnUnHide.setOnClickListener(1000L) {
            showMyDialog(
                this,
                UnHideLoadingDialog(selectedList) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val selectedListCopy = ArrayList(selectedList)
                        val deferredList = selectedListCopy.map { mediaItem ->
                            async(Dispatchers.IO) {
                                val positionToDelete = currentFolderList.indexOf(mediaItem)
                                currentFolderList.remove(mediaItem)
                                nextItemPos = positionToDelete
                                if (nextItemPos >= currentFolderList.size) {
                                    nextItemPos = currentFolderList.size - 1
                                }
                                if (nextItemPos == -1) {
                                    nextItemPos = 0
                                }
                                selectedItemPos = nextItemPos
                            }
                        }
                        // Wait for all async tasks to complete
                        deferredList.awaitAll()
                        withContext(Dispatchers.Main) {
                            if (currentFolderList.size == 0) {
                                onBackPressed()
                            } else {
                                updateImageList()
                            }
                        }
                    }
                })
        }
        binding.btnShare.setOnClickListener(1000L) {
            shareMultipleMediaItem(this, selectedList) {
            }
        }
        binding.btnDelete.setOnClickListener(1000L) {
            showMyDialog(
                this,
                TrashMessageDialog {
                    CoroutineScope(Dispatchers.IO).launch {
                        val selectedListCopy = ArrayList(selectedList)
                        val deferredList = selectedListCopy.map { mediaItem ->
                            async(Dispatchers.IO) {
                                val positionToDelete = currentFolderList.indexOf(mediaItem)
                                currentFolderList.remove(mediaItem)
                                nextItemPos = positionToDelete
                                if (nextItemPos >= currentFolderList.size) {
                                    nextItemPos = currentFolderList.size - 1
                                }
                                if (nextItemPos == -1) {
                                    nextItemPos = 0
                                }
                                selectedItemPos = nextItemPos
                                viewModel.removeMediaItemToDbSoft(mediaItem)
                            }
                        }
                        // Wait for all async tasks to complete
                        deferredList.awaitAll()
                        withContext(Dispatchers.Main) {
                            if (currentFolderList.size == 0) {
                                onBackPressed()
                            } else {
                                updateImageList()
                            }
                        }
                    }
                })
        }
    }

    private fun updateImageList() {
        try {
            selectedList.clear()
            selectedList.add(currentFolderList[nextItemPos])
            imageViewPagerAdapter.setList(currentFolderList, nextItemPos)
            binding.viewPager.setCurrentItem(nextItemPos, false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setUpViewPager() {
        binding.viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.viewPager.adapter = imageViewPagerAdapter
        val currentPageIndex = selectedItemPos
        binding.viewPager.setCurrentItem(currentPageIndex, false)
        binding.viewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    try {
                        imageViewPagerAdapter.pauseVideo(position)
                        selectedList.clear()
                        selectedList.add(currentFolderList[position])
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.viewPager.unregisterOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {}
        )
    }

    private fun onBackPressedWithExitTransition() {
        val exitIntent = Intent()
        setResult(Activity.RESULT_OK, exitIntent)
        finish()
    }

    override fun onBackPressed() {
        if(::imageViewPagerAdapter.isInitialized) {
            imageViewPagerAdapter.stopVideo(selectedItemPos)
        }
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