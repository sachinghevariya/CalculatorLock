package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.fragment

import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.R
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.adsModule.setOnClickListener
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.databinding.FragmentTrashBinding
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.adapter.TrashAdapter
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.base.BaseActivity
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.base.BaseFragment
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.dialog.PermanentDeleteDialog
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.MediaItem
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.NoteItem
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.viewmodels.FileManagerViewModel
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.deleteFileWithCursor
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.gone
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
class TrashFragment : BaseFragment<FragmentTrashBinding>() {

    private var selectedList = ArrayList<Any>()
    private lateinit var trashAdapter: TrashAdapter
    private val mediaItems = ArrayList<Any>()
    private val viewModel: FileManagerViewModel by viewModels()
    private var currentFolderList = ArrayList<Any>()
    private var isAllSelected = false
    private var isAdShown = false
    private var isModeEnable = false

    val isEnabled = false

    private fun marqueEnable() {
        binding.tvHeader.isSelected = true
        binding.tvAll.isSelected = true
        binding.tvRestore.isSelected = true
        binding.tvDelete.isSelected = true
        binding.tvNoData.isSelected = true
    }

    override fun getViewBinding() = FragmentTrashBinding.inflate(layoutInflater)

    override fun init() {
        marqueEnable()

        binding.ivBack.setOnClickListener(1000L) {
            requireActivity().onBackPressed()
        }

        binding.rvImages.layoutManager = GridLayoutManager(requireActivity(), 3)
        binding.cbAll.isEnabled = false

        trashAdapter = TrashAdapter(
            requireActivity() as AppCompatActivity,
            arrayListOf(),
            arrayListOf(),
            isVideo = false
        ) { list, selectedItem ->
            if (selectedItem != null) {

            } else {
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

        binding.rvImages.adapter = trashAdapter

        lifecycleScope.launch {
            viewModel.trashData()
            viewModel.mergedTrashItems.observe(this@TrashFragment) {
                if (it.isNotEmpty()) {
                    binding.ivEdit.visible()
                    binding.ivEdit.isEnabled = true
                    binding.tvNoData.gone()
                } else {
                    binding.ivEdit.isEnabled = false
                    binding.tvNoData.visible()
                    binding.ivEdit.gone()
                }
                currentFolderList.clear()
                selectedList.clear()
                mediaItems.clear()
                mediaItems.addAll(it)
                currentFolderList = mediaItems
                trashAdapter.setList(currentFolderList)
            }
        }

        binding.ivEdit.setOnClickListener(400L) {
            isModeEnable = !isModeEnable
            manageUi(isModeEnable)
            trashAdapter.isSelectionEnable = isModeEnable
            trashAdapter.notifyDataSetChanged()

            if (selectedList.size > 0) {
                binding.btnRestore.isEnabled = true
                binding.btnDelete.isEnabled = true
                binding.ivUnHide.setColorFilter(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.textColorWhite
                    )
                )
                binding.tvRestore.setTextColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.textColorWhite
                    )
                )
                binding.ivDelete.setColorFilter(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.textColorWhite
                    )
                )
                binding.tvDelete.setTextColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.textColorWhite
                    )
                )
            } else {
                binding.btnRestore.isEnabled = false
                binding.btnDelete.isEnabled = false
                binding.ivUnHide.setColorFilter(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.your_disabled_tint_color
                    )
                )
                binding.tvRestore.setTextColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.your_disabled_tint_color
                    )
                )
                binding.ivDelete.setColorFilter(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.your_disabled_tint_color
                    )
                )
                binding.tvDelete.setTextColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.your_disabled_tint_color
                    )
                )
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

        binding.btnRestore.setOnClickListener(1000L) {
            CoroutineScope(Dispatchers.IO).launch {
                val selectedListCopy = ArrayList(selectedList)
                val deferredList = selectedListCopy.map { mediaItem ->
                    async(Dispatchers.IO) {
                        if (mediaItem is MediaItem) {
                            viewModel.restoreMediaItemToDbSoft(mediaItem)
                        } else if (mediaItem is NoteItem) {
                            viewModel.restoreNoteItemToDbSoft(mediaItem)
                        }

                    }
                }
                // Wait for all async tasks to complete
                deferredList.awaitAll()
                withContext(Dispatchers.Main) {
                    updateImageList(false)
                }
            }
        }

        binding.btnDelete.setOnClickListener(1000L) {
            showMyDialog(
                requireActivity() as AppCompatActivity,
                PermanentDeleteDialog {
                    if (it) {
                        CoroutineScope(Dispatchers.IO).launch {
                            val selectedListCopy = ArrayList(selectedList)
                            val deferredList = selectedListCopy.map { mediaItem ->
                                async(Dispatchers.IO) {
                                    if (mediaItem is MediaItem) {
                                        deleteFileWithCursor(requireActivity(), mediaItem) { _ ->
                                            viewModel.removeMediaItemToDb(mediaItem)
                                        }
                                    } else if (mediaItem is NoteItem) {
                                        viewModel.removeNoteItemToDb(mediaItem)
                                    }
                                }
                            }
                            // Wait for all async tasks to complete
                            deferredList.awaitAll()
                            withContext(Dispatchers.Main) {
                                updateImageList(false)
                            }
                        }
                    } else {
                        binding.containerAllSelection.gone()
                        selectedList = arrayListOf()
                        isAllSelected = false
                        updateImageList(isAllSelected)
                    }
                })

        }
    }

    private fun updateImageList(isAllSelected: Boolean) {
        currentFolderList = mediaItems
        trashAdapter.selectedList = selectedList
        trashAdapter.isSelectionEnable = isAllSelected
        manageUi(isAllSelected)
        trashAdapter.setList(currentFolderList)
    }

    private fun manageUi(isSelectionModeOn: Boolean) {
        if (isSelectionModeOn) {
            isModeEnable = true
            binding.containerAllSelection.visible()
            binding.ivEdit.gone()
            binding.buttonView.visible()
        } else {
            isModeEnable = false
            binding.containerAllSelection.gone()
            binding.ivEdit.visible()
            binding.buttonView.gone()
        }

        if (selectedList.size > 0) {
            binding.btnRestore.isEnabled = true
            binding.btnDelete.isEnabled = true
            binding.ivUnHide.setColorFilter(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.textColorWhite
                )
            )
            binding.tvRestore.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.textColorWhite
                )
            )
            binding.ivDelete.setColorFilter(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.textColorWhite
                )
            )
            binding.tvDelete.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.textColorWhite
                )
            )
        } else {
            binding.btnRestore.isEnabled = false
            binding.btnDelete.isEnabled = false
            binding.ivUnHide.setColorFilter(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.your_disabled_tint_color
                )
            )
            binding.tvRestore.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.your_disabled_tint_color
                )
            )
            binding.ivDelete.setColorFilter(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.your_disabled_tint_color
                )
            )
            binding.tvDelete.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.your_disabled_tint_color
                )
            )
        }
    }


}