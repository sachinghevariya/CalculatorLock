package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.fragment

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.R
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.adsModule.setOnClickListener
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.databinding.FragmentAllAudioBinding
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.adapter.AudioAdapter
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.adapter.FolderNameListAdapter
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.base.BaseActivity
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.base.BaseFragment
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.dialog.BsHideFolderListDialog
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.dialog.HideLoadingDialog
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.MediaItem
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.viewmodels.FileManagerViewModel
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.gone
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.showBottomSheet
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.showMyDialog
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.showToast
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AllAudioFragment : BaseFragment<FragmentAllAudioBinding>() {
    override fun getViewBinding() = FragmentAllAudioBinding.inflate(layoutInflater)

    private val viewModel: FileManagerViewModel by viewModels()
    private lateinit var audioAdapter: AudioAdapter
    private var selectedList = ArrayList<MediaItem>()
    private val mediaItems = ArrayList<MediaItem>()
    private var currentFolderList = ArrayList<MediaItem>()
    private var folderList = ArrayList<String>()
    private var isAllSelected = false
    private lateinit var datHashMap: HashMap<String, ArrayList<MediaItem>>
    private var dbFolderName: String = "My Audio"
    private var dbFolderId: Int = 0
    private var selectedFolderName: String = ""
    private var isPopupWindowShown = false
    private lateinit var popupWindow: PopupWindow


    private fun marqueEnable() {
        binding.tvHeader.isSelected = true
        binding.tvAll.isSelected = true
        binding.tvHide.isSelected = true
        binding.tvHideFolderName.isSelected = true
        binding.tvNoData.isSelected = true
    }

    override fun init() {
        marqueEnable()
        dbFolderName = "My Audios"
        dbFolderId = 2
        binding.tvHeader.text = "${getString(R.string.all_audio)}"
        binding.rvAudio.layoutManager =
            LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false)

        selectedFolderName = binding.tvHeader.text.toString().trim()
        binding.tvHideFolderName.text = "$dbFolderName"
        binding.cbAll.isEnabled = false

        audioAdapter = AudioAdapter(
            requireActivity() as AppCompatActivity,
            arrayListOf(),
            isFromHide = false,
            arrayListOf(),
            isSelectionEnable = true
        ) { list, _ ->
            selectedList = list
            if (currentFolderList.size == selectedList.size) {
                binding.cbAll.isChecked = true
                isAllSelected = true
            } else {
                binding.cbAll.isChecked = false
                isAllSelected = false
            }
            manageUi()
        }

        binding.rvAudio.adapter = audioAdapter

        binding.ivBack.setOnClickListener(1000L) {
            requireActivity().finish()
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

                if (selectedFolderName == getString(R.string.all_audio)) {
                    selectedList.addAll(mediaItems)
                } else {
                    selectedList.addAll(datHashMap[selectedFolderName]!!)
                }

                manageUi()
                updateImageList(selectedFolderName)
            } else {
                selectedList = arrayListOf()
                manageUi()
                updateImageList(selectedFolderName)
            }
        }

        binding.btnSelectFolder.setOnClickListener(1000L) {
            showBottomSheet(
                requireActivity() as AppCompatActivity,
                BsHideFolderListDialog(isAudio = true) { dbFolderName, dbFolderId ->
                    binding.tvHideFolderName.text = "$dbFolderName"
                    this.dbFolderName = dbFolderName
                    this.dbFolderId = dbFolderId
                })
        }

        binding.btnHide.setOnClickListener(1000L) {
            if (selectedList.isEmpty()) {
                requireActivity().showToast(getString(R.string.select_media))
            } else {
                showMyDialog(
                    requireActivity() as AppCompatActivity,
                    HideLoadingDialog(selectedList, dbFolderName, dbFolderId) {
                        /*(requireActivity() as BaseActivity<*>).showInterAd(object :
                            BaseActivity.OnAdClose {
                            override fun onAdClose() {
                                requireActivity().finish()
                            }
                        })*/
                        requireActivity().finish()
                    })
            }
        }

        binding.containerSelectFolder.setOnClickListener(1000L) {
            showFolderListPopup(binding.containerSelectFolder, folderList) {
                updateImageList(it)
            }
        }

        lifecycleScope.launch {
            viewModel.fetchAudioMediaItems()
            viewModel.audioItemsWithFolder.collectLatest {
                if (it.isNotEmpty()) {
                    binding.tvNoData.gone()
                    binding.containerAllSelection.visible()
                } else {
                    binding.tvNoData.visible()
                    binding.containerAllSelection.gone()
                }
                datHashMap = updateHashMap(it)
                folderList.clear()
                mediaItems.clear()
                folderList.add(getString(R.string.all_audio))
                it.forEach { item ->
                    folderList.add(getFolderNameFromPath(item.key))
                    mediaItems.addAll(item.value)
                    currentFolderList = mediaItems
                    audioAdapter.setList(currentFolderList)
                }
            }
        }

    }

    private fun updateHashMap(it: HashMap<String, ArrayList<MediaItem>>): HashMap<String, ArrayList<MediaItem>> {
        val newHashMap = HashMap<String, ArrayList<MediaItem>>()
        for ((folderName, mediaItemList) in it) {
            for (mediaItem in mediaItemList) {
                val newPathKey = getFolderNameFromPath(folderName)
                newHashMap.getOrPut(newPathKey) { ArrayList() }.add(mediaItem)
            }
        }
        return newHashMap
    }

    private fun showFolderListPopup(
        anchorView: View,
        folderList: List<String>,
        functionCallback: (folderName: String) -> Unit
    ) {

        if (isPopupWindowShown) {
            isPopupWindowShown = false
            binding.ivUpDownArrow.rotation = 0f
            popupWindow?.dismiss()
            return
        }

        val inflater =
            anchorView.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val customView = inflater.inflate(R.layout.fragment_bs_list_folder_dialog, null)

        popupWindow = PopupWindow(
            customView,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        val recyclerView = customView.findViewById<RecyclerView>(R.id.rvFolderNameList)
        recyclerView.layoutManager =
            LinearLayoutManager(anchorView.context, RecyclerView.VERTICAL, false)
        recyclerView.adapter = FolderNameListAdapter(folderList) {
            functionCallback.invoke(it)
            binding.ivUpDownArrow.rotation = 0f
            isPopupWindowShown = false
            popupWindow.dismiss()
        }

        popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popupWindow.isOutsideTouchable = false
        binding.ivUpDownArrow.rotation = 180f
        popupWindow.showAsDropDown(anchorView)
        isPopupWindowShown = true
    }

    private fun getFolderNameFromPath(filePath: String): String {
        val parts = filePath.split("/")
        return parts.last()
    }

    private fun manageUi() {
        binding.tvHide.text = "${getString(R.string.hide)}(${selectedList.size})"
    }

    private fun updateImageList(folderName: String) {
        selectedFolderName = folderName
        binding.tvHeader.text = "$folderName"
        currentFolderList = if (folderName == getString(R.string.all_audio)) {
            mediaItems
        } else {
            datHashMap[folderName]!!
        }

        audioAdapter.selectedList = selectedList
        audioAdapter.setList(currentFolderList)
    }

}