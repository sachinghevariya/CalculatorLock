package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.activity

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.activity.viewModels
import androidx.viewpager2.widget.ViewPager2
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.R
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.adsModule.setOnClickListener
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.databinding.ActivityTrashImageViewerBinding
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.adapter.PagerAdapter
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.base.BaseActivity
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.dialog.PermanentDeleteDialog
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.MediaItem
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.MediaType
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.viewmodels.FileManagerViewModel
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.CommonClass
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.deleteFileWithCursor
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.inVisible
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.showMyDialog
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class TrashImageViewerActivity : BaseActivity<ActivityTrashImageViewerBinding>() {
    private lateinit var imageViewPagerAdapter: PagerAdapter
    private val viewModel: FileManagerViewModel by viewModels()
    private var selectedList = ArrayList<MediaItem>()
    private var currentFolderList = ArrayList<MediaItem>()
    private var selectedItemPos = 0
    private var nextItemPos = 0

    override fun getViewBinding() = ActivityTrashImageViewerBinding.inflate(layoutInflater)
    private fun marqueEnable() {
        binding.tvHeader.isSelected = true
        binding.tvDelete.isSelected = true
        binding.tvUnhide.isSelected = true
    }

    override fun initData() {
        marqueEnable()
        val isVideo = intent.getBooleanExtra("isVideo", false)
//        val item = intent.getSerializableExtra("selectedItem") as ArrayList<MediaItem>
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

        binding.btnRestore.setOnClickListener(1000L) {
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
                        viewModel.restoreMediaItemToDbSoft(mediaItem)
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
        }

        binding.btnDelete.setOnClickListener(1000L) {
            showMyDialog(
                this,
                PermanentDeleteDialog {
                    if (it) {
                        CoroutineScope(Dispatchers.IO).launch {
                            val selectedListCopy = ArrayList(selectedList)
                            val deferredList = selectedListCopy.map { mediaItem ->
                                async(Dispatchers.IO) {
                                    deleteFileWithCursor(
                                        this@TrashImageViewerActivity,
                                        mediaItem
                                    ) { _ ->
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
                                        viewModel.removeMediaItemToDb(mediaItem)
                                    }
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
        binding.viewPager.adapter = imageViewPagerAdapter
        binding.viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        val currentPageIndex = selectedItemPos
        binding.viewPager.setCurrentItem(currentPageIndex, false)
        binding.viewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
//                        val itemPos = if (position > currentFolderList.size) {
//                            currentFolderList.size - 1
//                        } else {
//                            position + 1
//                        }
                    try {
                        selectedItemPos = position
                        if (currentFolderList[position].type == MediaType.VIDEO) {
                            imageViewPagerAdapter.isVideo = true
                            imageViewPagerAdapter.pauseVideo(position)
                        } else {
                            imageViewPagerAdapter.isVideo = false
                            imageViewPagerAdapter.pauseVideo(position)
                        }
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
        imageViewPagerAdapter.stopVideo(selectedItemPos)
        super.onBackPressed()
    }
}