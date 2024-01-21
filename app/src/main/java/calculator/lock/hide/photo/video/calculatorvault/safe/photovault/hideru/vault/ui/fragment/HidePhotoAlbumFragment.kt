package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.fragment

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.R
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.adsModule.setOnClickListener
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.databinding.FragmentHidePhotoAlbumBinding
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.adapter.AlbumsAdapter
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.base.BaseFragment
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.dialog.BsAddFolderDialog
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.dialog.TrashMessageDialog
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.dialog.UnHideLoadingDialog
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.FolderItem
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.FolderItemWithMediaItems
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.MediaItem
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.MediaType
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.viewmodels.FileManagerViewModel
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.gone
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
class HidePhotoAlbumFragment(
    private val isVideo: Boolean = false,
    private val isAudio: Boolean = false,
    private val isDoc: Boolean = false,
    private val functionCallback: (isSelectionMode: Boolean) -> Unit
) :
    BaseFragment<FragmentHidePhotoAlbumBinding>() {

    private lateinit var albumsAdapter: AlbumsAdapter
    private val folderItems = ArrayList<FolderItem>()
    private val selectedMediaItems = ArrayList<MediaItem>()
    private var folderItemsNew = ArrayList<FolderItemWithMediaItems>()
    private val viewModel: FileManagerViewModel by viewModels()
    private val TAG = "HidePhotoAlbumFragment"
    private var selectedList = ArrayList<FolderItemWithMediaItems>()
    private var isAllSelected = false

    override fun getViewBinding() = FragmentHidePhotoAlbumBinding.inflate(layoutInflater)

    private fun marqueEnable() {
        binding.tvHeader.isSelected = true
        binding.tvAll.isSelected = true
        binding.tvUnhide.isSelected = true
        binding.tvDelete.isSelected = true
    }

    override fun init() {
        marqueEnable()
        binding.ivBack.setOnClickListener(1000L) {
            requireActivity().finish()
        }
        binding.rvFolder.layoutManager = GridLayoutManager(requireActivity(), 2)

        if (isAudio) {
            binding.tvHeader.text = getString(R.string.audio)
        } else if (isDoc) {
            binding.tvHeader.text = getString(R.string.document)
        } else if (isVideo) {
            binding.tvHeader.text = getString(R.string.videos)
        } else {
            binding.tvHeader.text = getString(R.string.photos)
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
                selectedList.addAll(folderItemsNew)
            } else {
                binding.containerAllSelection.gone()
                selectedList = arrayListOf()
            }
            updateImageList(isAllSelected)
        }

        albumsAdapter =
            AlbumsAdapter(
                requireActivity() as AppCompatActivity,
                folderItemsNew,
                isVideo = isVideo,
                isAudio = isAudio,
                isDoc = isDoc,
                selectedList = selectedList,
                selectionCallback = { list ->
                    selectedList = list
                    if (selectedList.size > 0) {
                        manageUi(true)
                        if (folderItemsNew.size != selectedList.size) {
                            binding.cbAll.isChecked = false
                            isAllSelected = false
                        }

                        if (folderItemsNew.size == selectedList.size) {
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
            ) { isShowDialog, _ ->
                if (isShowDialog) {
                    showAddFolderDialog()
                }
            }

        binding.rvFolder.adapter = albumsAdapter

        lifecycleScope.launch {
            if (isAudio) {
                viewModel.folderAudioWithAudioFlow.observe(this@HidePhotoAlbumFragment) {
                    CoroutineScope(Dispatchers.IO).launch {
                        folderItemsNew.clear()
                        folderItemsNew.addAll(it)
                        folderItemsNew.add(
                            FolderItemWithMediaItems(
                                FolderItem(-1, "", MediaType.AUDIO),
                                arrayListOf()
                            )
                        )
                        val folderItemsNewCopy = ArrayList(folderItemsNew)
                        val deferredList = folderItemsNewCopy.map { it ->
                            async(Dispatchers.IO) {
                                if (it.mediaItems != null) {
                                    it.mediaItems =
                                        it.mediaItems.filter { mediaItem -> mediaItem.isTrash == 0 }
                                }
                            }
                        }
                        deferredList.awaitAll()
                        withContext(Dispatchers.Main) {
                            selectedList.clear()
                            albumsAdapter.setList(folderItemsNew)
                        }
                    }
                }
            } else if (isDoc) {
                viewModel.folderDocWithAudioFlow.observe(this@HidePhotoAlbumFragment) {
                    CoroutineScope(Dispatchers.IO).launch {
                        folderItemsNew.clear()
                        folderItemsNew.addAll(it)
                        folderItemsNew.add(
                            FolderItemWithMediaItems(
                                FolderItem(-1, "", MediaType.DOC),
                                arrayListOf()
                            )
                        )
                        val folderItemsNewCopy = ArrayList(folderItemsNew)
                        val deferredList = folderItemsNewCopy.map { it ->
                            async(Dispatchers.IO) {
                                if (it.mediaItems != null) {
                                    it.mediaItems =
                                        it.mediaItems.filter { mediaItem -> mediaItem.isTrash == 0 }
                                }
                            }
                        }
                        deferredList.awaitAll()
                        withContext(Dispatchers.Main) {
                            selectedList.clear()
                            albumsAdapter.setList(folderItemsNew)
                        }
                    }
                }
            } else if (isVideo) {
                viewModel.folderVideoWithImagesFlow.observe(this@HidePhotoAlbumFragment) {
                    CoroutineScope(Dispatchers.IO).launch {
                        folderItemsNew.clear()
                        folderItemsNew.addAll(it)
                        folderItemsNew.add(
                            FolderItemWithMediaItems(
                                FolderItem(-1, "", MediaType.VIDEO),
                                arrayListOf()
                            )
                        )
                        val folderItemsNewCopy = ArrayList(folderItemsNew)
                        val deferredList = folderItemsNewCopy.map { it ->
                            async(Dispatchers.IO) {
                                if (it.mediaItems != null) {
                                    it.mediaItems =
                                        it.mediaItems.filter { mediaItem -> mediaItem.isTrash == 0 }
                                }
                            }
                        }
                        deferredList.awaitAll()
                        withContext(Dispatchers.Main) {
                            selectedList.clear()
                            albumsAdapter.setList(folderItemsNew)
                        }
                    }
                }
            } else {
                viewModel.folderPhotoWithImagesFlow.observe(this@HidePhotoAlbumFragment) {
                    CoroutineScope(Dispatchers.IO).launch {
                        folderItemsNew.clear()
                        folderItemsNew.addAll(it)
                        folderItemsNew.add(
                            FolderItemWithMediaItems(
                                FolderItem(-1, "", MediaType.IMAGE),
                                arrayListOf()
                            )
                        )
                        val folderItemsNewCopy = ArrayList(folderItemsNew)
                        val deferredList = folderItemsNewCopy.map { folderItem ->
                            async(Dispatchers.IO) {
                                folderItem.mediaItems?.let { mediaItems ->
                                    folderItem.mediaItems =
                                        mediaItems.filter { mediaItem -> mediaItem.isTrash == 0 }
                                }
                            }
                        }
                        deferredList.awaitAll()
                        withContext(Dispatchers.Main) {
                            selectedList.clear()
                            albumsAdapter.setList(folderItemsNew)
                        }
                    }
                }
            }
        }

        binding.btnUnHide.setOnClickListener(1000L) {
            selectedMediaItems.clear()
            CoroutineScope(Dispatchers.IO).launch {
                val selectedListCopy = ArrayList(selectedList)
                val deferredList = selectedListCopy.map { folderItem ->
                    async(Dispatchers.IO) {
                        selectedMediaItems.addAll(folderItem.mediaItems)
                    }
                }
                deferredList.awaitAll()
                withContext(Dispatchers.Main) {
                    if (selectedMediaItems.isNotEmpty()) {
                        showMyDialog(
                            requireActivity() as AppCompatActivity,
                            UnHideLoadingDialog(selectedMediaItems) {
                                updateImageList(false)
                            })
                    } else {
                        withContext(Dispatchers.Main) {
                            updateImageList(false)
                        }
                        requireActivity().showToast(getString(R.string.no_media_found))
                    }
                }
            }
        }

        binding.btnDelete.setOnClickListener(1000L) {
            selectedMediaItems.clear()
            CoroutineScope(Dispatchers.IO).launch {
                val selectedListCopy = ArrayList(selectedList)
                val deferredList = selectedListCopy.map { folderItem ->
                    async(Dispatchers.IO) {
                        if (folderItem.folderItem.id != 1 && folderItem.folderItem.id != 0) {
                            viewModel.removeFolderFromDb(folderItem.folderItem)
                        }
                        selectedMediaItems.addAll(folderItem.mediaItems)
                    }
                }
                deferredList.awaitAll()
                withContext(Dispatchers.Main) {

                    if (selectedMediaItems.isNotEmpty()) {
                        showMyDialog(
                            requireActivity() as AppCompatActivity,
                            TrashMessageDialog {
                                CoroutineScope(Dispatchers.IO).launch {
                                    val deferredList = selectedMediaItems.map { mediaItem ->
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
                    } else {
                        withContext(Dispatchers.Main) {
                            updateImageList(false)
                        }
                    }
                }
            }


        }

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

    private fun showAddFolderDialog() {
        showMyDialog(requireActivity() as AppCompatActivity, BsAddFolderDialog {
            if (isAudio) {
                viewModel.addFolderToDb(it, MediaType.AUDIO)
            } else if (isDoc) {
                viewModel.addFolderToDb(it, MediaType.DOC)
            } else if (isVideo) {
                viewModel.addFolderToDb(it, MediaType.VIDEO)
            } else {
                viewModel.addFolderToDb(it, MediaType.IMAGE)
            }
        })
    }

    private fun updateImageList(isAllSelected: Boolean) {
        albumsAdapter.selectedList = selectedList
        albumsAdapter.isSelectionEnable = isAllSelected
        manageUi(isAllSelected)
        albumsAdapter.setList(folderItemsNew)
    }

}