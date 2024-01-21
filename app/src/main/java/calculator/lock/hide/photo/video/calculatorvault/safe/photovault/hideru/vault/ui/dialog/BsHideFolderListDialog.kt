package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.dialog

import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.R
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.databinding.FragmentBsHideFolderListDialogBinding
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.adapter.AlbumsAdapter
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.base.BaseBottomSheetFragment
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.FolderItem
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.FolderItemWithMediaItems
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.MediaType
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.viewmodels.FileManagerViewModel
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.showMyDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class BsHideFolderListDialog(
    val isFromMove: Boolean = false,
    val isVideo: Boolean = false,
    val isAudio: Boolean = false,
    val isDoc: Boolean = false,
    private val functionCallback: (folderName: String, folderId: Int) -> Unit
) : BaseBottomSheetFragment<FragmentBsHideFolderListDialogBinding>() {

    private lateinit var albumsAdapter: AlbumsAdapter
    private val folderItemsNew = ArrayList<FolderItemWithMediaItems>()
    private val viewModel: FileManagerViewModel by viewModels()

    private fun marqueEnable() {
        binding.tvTitle.isSelected = true
    }


    override fun getViewBinding() = FragmentBsHideFolderListDialogBinding.inflate(layoutInflater)

    override fun init() {
        marqueEnable()
        if (isFromMove) {
            binding.tvTitle.text = "${getString(R.string.move_to_album)}"
        } else {
            binding.tvTitle.text = "${getString(R.string.create_album)}"
            binding.tvTitle.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.textColorWhite
                )
            )
        }

        binding.rvFolderNameList.layoutManager = LinearLayoutManager(
            requireActivity(),
            RecyclerView.VERTICAL, false
        )

        albumsAdapter =
            AlbumsAdapter(
                requireActivity() as AppCompatActivity,
                folderItemsNew,
                true,
                isVideo = isVideo,
                isAudio= isAudio,
                isDoc = isDoc,
                selectionCallback = {}
            ) { isShowDialog, folderItemWithMedia ->
                if (isShowDialog) {
                    showAddFolderDialog()
                } else {
                    val dbFolderName = folderItemWithMedia.folderItem.folderName
                    val dbFolderId = folderItemWithMedia.folderItem.id
                    functionCallback.invoke(dbFolderName, dbFolderId)
                    dismiss()
                }
            }

        binding.rvFolderNameList.adapter = albumsAdapter

        lifecycleScope.launch {
            if(isAudio) {
                viewModel.folderAudioWithAudioFlow.observe(this@BsHideFolderListDialog) {
                    CoroutineScope(Dispatchers.IO).launch {
                        folderItemsNew.clear()
                        folderItemsNew.add(
                            FolderItemWithMediaItems(
                                FolderItem(-1, "", MediaType.AUDIO),
                                arrayListOf()
                            )
                        )
                        folderItemsNew.addAll(it)

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
                            albumsAdapter.setList(folderItemsNew)
                        }
                    }
                }
            } else if (isDoc) {
                viewModel.folderDocWithAudioFlow.observe(this@BsHideFolderListDialog) {
                    CoroutineScope(Dispatchers.IO).launch {
                        folderItemsNew.clear()
                        folderItemsNew.add(
                            FolderItemWithMediaItems(
                                FolderItem(-1, "", MediaType.DOC),
                                arrayListOf()
                            )
                        )
                        folderItemsNew.addAll(it)

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
                            albumsAdapter.setList(folderItemsNew)
                        }
                    }
                }
            }else if (isVideo) {
                viewModel.folderVideoWithImagesFlow.observe(this@BsHideFolderListDialog) {
                    CoroutineScope(Dispatchers.IO).launch {
                        folderItemsNew.clear()
                        folderItemsNew.add(
                            FolderItemWithMediaItems(
                                FolderItem(-1, "", MediaType.VIDEO),
                                arrayListOf()
                            )
                        )
                        folderItemsNew.addAll(it)

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
                            albumsAdapter.setList(folderItemsNew)
                        }
                    }
                }
            } else {
                viewModel.folderPhotoWithImagesFlow.observe(this@BsHideFolderListDialog) {
                    CoroutineScope(Dispatchers.IO).launch {
                        folderItemsNew.clear()
                        folderItemsNew.add(
                            FolderItemWithMediaItems(
                                FolderItem(-1, "", MediaType.IMAGE),
                                arrayListOf()
                            )
                        )
                        folderItemsNew.addAll(it)

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
                            albumsAdapter.setList(folderItemsNew)
                        }
                    }
                }
            }
        }
    }

    private fun showAddFolderDialog() {
        showMyDialog(requireActivity() as AppCompatActivity, BsAddFolderDialog {
            if(isAudio) {
                viewModel.addFolderToDb(it, MediaType.AUDIO)
            }else if (isDoc) {
                viewModel.addFolderToDb(it, MediaType.DOC)
            }else if (isVideo) {
                viewModel.addFolderToDb(it, MediaType.VIDEO)
            } else {
                viewModel.addFolderToDb(it, MediaType.IMAGE)
            }
        })
    }

}