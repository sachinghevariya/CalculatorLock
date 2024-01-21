package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.R
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.adsModule.setOnClickListener
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.databinding.FragmentHidePhotosBinding
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.adapter.PhotosAdapter
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.base.BaseActivity
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.base.BaseFragment
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.dialog.BsHideFolderListDialog
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.dialog.TrashMessageDialog
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.dialog.UnHideLoadingDialog
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.MediaItem
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.viewmodels.FileManagerViewModel
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.gone
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

@AndroidEntryPoint
class HidePhotosFragment(
        private val isVideo: Boolean = false,
        private val functionCallback: (isSelectionMode: Boolean) -> Unit
) :
        BaseFragment<FragmentHidePhotosBinding>() {

    private var selectedList = ArrayList<MediaItem>()
    private lateinit var photosAdapter: PhotosAdapter
    private val mediaItems = ArrayList<MediaItem>()
    private val viewModel: FileManagerViewModel by viewModels()
    private var currentFolderList = ArrayList<MediaItem>()
    private var isAllSelected = false

    override fun getViewBinding() = FragmentHidePhotosBinding.inflate(layoutInflater)

    private fun marqueEnable() {
        binding.tvHeader.isSelected = true
        binding.tvAll.isSelected = true
        binding.tvUnhide.isSelected = true
        binding.tvMove.isSelected = true
        binding.tvShare.isSelected = true
        binding.tvDelete.isSelected = true
        binding.tvNoData.isSelected = true
    }
    override fun init() {
        marqueEnable()
        if (isVideo) {
            binding.tvHeader.text = getString(R.string.videos)
        } else {
            binding.tvHeader.text = getString(R.string.photos)
        }

        binding.ivBack.setOnClickListener(1000L) {
            requireActivity().finish()
        }

        binding.rvImages.layoutManager = GridLayoutManager(requireActivity(), 3)
        binding.cbAll.isEnabled = false

        photosAdapter = PhotosAdapter(
                requireActivity() as AppCompatActivity, arrayListOf(), arrayListOf(), isHidePhoto = true, isVideo = isVideo
        ) { list, selectedItem ->
            if (selectedItem != null) { } else {
                selectedList = list
                if (selectedList.size > 0) {
                    manageUi(true)
                    if (mediaItems.size != selectedList.size) {
                        binding.cbAll.isChecked = false
                        isAllSelected = false
                    }

                    if (mediaItems.size == selectedList.size) {
                        binding.cbAll.isChecked = true
                        isAllSelected = true
                    }
                } else {
                    binding.cbAll.isChecked = false
                    isAllSelected = false
                    binding.containerAllSelection.gone()
                    updateImageList(isAllSelected)
                }
            }
        }

        binding.rvImages.adapter = photosAdapter

        lifecycleScope.launch {
            if (isVideo) {
                viewModel.allVideoItemsFlow.observe(this@HidePhotosFragment) {
                    if(it.isNotEmpty()){
                        binding.tvNoData.gone()
                    }else{
                        binding.tvNoData.visible()
                    }

                    currentFolderList.clear()
                    selectedList.clear()
                    mediaItems.clear()
                    mediaItems.addAll(it)
                    currentFolderList = mediaItems
                    photosAdapter.setList(currentFolderList)
                }
            } else {
                viewModel.allPhotoItemsFlow.observe(this@HidePhotosFragment) {
                    if(it.isNotEmpty()){
                        binding.tvNoData.gone()
                    }else{
                        binding.tvNoData.visible()
                    }
                    currentFolderList.clear()
                    selectedList.clear()
                    mediaItems.clear()
                    mediaItems.addAll(it)
                    currentFolderList = mediaItems
                    photosAdapter.setList(currentFolderList)
                }
            }
        }

        binding.viewAll.setOnClickListener(400L) {
            if (binding.cbAll.isChecked) {
                binding.cbAll.isChecked = false
                isAllSelected = false
            } else {
                binding.cbAll.isChecked = true
                isAllSelected = true
            }

            if (isAllSelected) {
                selectedList.clear()
                selectedList.addAll(mediaItems)
            } else {
                binding.containerAllSelection.gone()
                selectedList = arrayListOf()
            }
            updateImageList(isAllSelected)
        }

        binding.btnMove.setOnClickListener(1000L) {
            showBottomSheet(requireActivity() as AppCompatActivity,
                    BsHideFolderListDialog(true,isVideo = isVideo) { dbFolderName, dbFolderId ->
                        if (dbFolderName == selectedList[0].folderName) {
                            requireActivity().showToast(getString(R.string.cant_move_same_folder))
                        } else {
                            CoroutineScope(Dispatchers.IO).launch {
                                val selectedListCopy = ArrayList(selectedList)
                                val deferredList = selectedListCopy.map { mediaItem ->
                                    async(Dispatchers.IO) {
                                        mediaItem.folderName = dbFolderName
                                        mediaItem.folderNameId = dbFolderId
                                        viewModel.updateMediaItemToDb(mediaItem)
                                    }
                                }
                                deferredList.awaitAll()
                                withContext(Dispatchers.Main) {
                                    updateImageList(false)
                                }
                            }
                        }

                    })
        }

        binding.btnUnHide.setOnClickListener(1000L) {
            showMyDialog(requireActivity() as AppCompatActivity, UnHideLoadingDialog(selectedList) {
                updateImageList(false)
            })
        }

        binding.btnShare.setOnClickListener(1000L) {
            shareMultipleMediaItem(requireActivity(), selectedList){
                selectedList.clear()
                Handler(Looper.getMainLooper()).postDelayed({
                    isAllSelected = false
                    updateImageList(isAllSelected)
                },300)
            }
        }

        binding.btnDelete.setOnClickListener(1000L) {
            showMyDialog(
                    requireActivity() as AppCompatActivity,
                    TrashMessageDialog {
                        CoroutineScope(Dispatchers.IO).launch {
                            val selectedListCopy = ArrayList(selectedList)
                            val deferredList = selectedListCopy.map { mediaItem ->
                                async(Dispatchers.IO) {
                                    viewModel.removeMediaItemToDbSoft(mediaItem)
                                }
                            }
                            // Wait for all async tasks to complete
                            deferredList.awaitAll()
                            withContext(Dispatchers.Main) {
                                updateImageList(false)
                            }
                        }
                    })

        }

    }

    private fun updateImageList(isAllSelected: Boolean) {
        currentFolderList = mediaItems
        photosAdapter.selectedList = selectedList
        photosAdapter.isSelectionEnable = isAllSelected
        manageUi(isAllSelected)
        photosAdapter.setList(currentFolderList)
    }

    private fun manageUi(isSelectionModeOn: Boolean) {
        functionCallback.invoke(isSelectionModeOn)
        if (isSelectionModeOn) {
            binding.containerAllSelection.visible()
            binding.buttonView.visible()
        } else {
            binding.containerAllSelection.gone()
            binding.buttonView.gone()
        }
    }

}