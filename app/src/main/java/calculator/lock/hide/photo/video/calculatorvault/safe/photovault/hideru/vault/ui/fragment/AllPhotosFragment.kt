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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.R
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.adsModule.setOnClickListener
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.databinding.FragmentAllPhotosBinding
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.adapter.FolderNameListAdapter
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.adapter.PhotosAdapter
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
class AllPhotosFragment(private val isVideo: Boolean = false) :
    BaseFragment<FragmentAllPhotosBinding>() {

    private val viewModel: FileManagerViewModel by viewModels()
    private lateinit var photosAdapter: PhotosAdapter
    private var selectedList = ArrayList<MediaItem>()
    private val mediaItems = ArrayList<MediaItem>()
    private var currentFolderList = ArrayList<MediaItem>()
    private var folderList = ArrayList<String>()
    private var isAllSelected = false
    private lateinit var datHashMap: HashMap<String, ArrayList<MediaItem>>
    private var dbFolderName: String = "My Photos"
    private var dbFolderId: Int = 0
    private var selectedFolderName: String = ""
    private var isPopupWindowShown = false
    private lateinit var popupWindow: PopupWindow

    override fun getViewBinding() = FragmentAllPhotosBinding.inflate(layoutInflater)

    private fun marqueEnable() {
        binding.tvHeader.isSelected = true
        binding.tvAll.isSelected = true
        binding.tvHideFolderName.isSelected = true
        binding.tvHide.isSelected = true
        binding.tvNoData.isSelected = true
    }

    override fun init() {
        marqueEnable()
        binding.rvImages.layoutManager = GridLayoutManager(requireActivity(), 3)
        if (isVideo) {
            dbFolderName = "My Videos"
            dbFolderId = 1
            binding.tvHeader.text = "${getString(R.string.all_videos)}"
        } else {
            dbFolderName = "My Photos"
            dbFolderId = 0
            binding.tvHeader.text = "${getString(R.string.all_photos)}"
        }
        selectedFolderName = binding.tvHeader.text.toString().trim()
        binding.tvHideFolderName.text = "$dbFolderName"
        binding.cbAll.isEnabled = false

        photosAdapter = PhotosAdapter(
            requireActivity() as AppCompatActivity,
            arrayListOf(),
            arrayListOf(),
            isSelectionEnable = true,
            isVideo = isVideo
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

        binding.rvImages.adapter = photosAdapter

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

                if (isVideo) {
                    if (selectedFolderName == getString(R.string.all_videos)) {
                        selectedList.addAll(mediaItems)
                    } else {
                        selectedList.addAll(datHashMap[selectedFolderName]!!)
                    }
                } else {
                    if (selectedFolderName == getString(R.string.all_photos)) {
                        selectedList.addAll(mediaItems)
                    } else {
                        selectedList.addAll(datHashMap[selectedFolderName]!!)
                    }
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
                BsHideFolderListDialog(isVideo = isVideo) { dbFolderName, dbFolderId ->
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
//            if(isPopupWindowShown){
//                isPopupWindowShown = false
//                binding.ivUpDownArrow.rotation = 180f
//            }else{
//                isPopupWindowShown = true
//                binding.ivUpDownArrow.rotation = 0f
//            }
            showFolderListPopup(binding.containerSelectFolder, folderList) {
                updateImageList(it)
            }


        }

        lifecycleScope.launch {
            if (isVideo) {
                viewModel.fetchVideoMediaItemsWithFolder()
                viewModel.mediaItemsWithFolderVideo.collectLatest {
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
                    folderList.add(getString(R.string.all_videos))
                    it.forEach { item ->
                        folderList.add(getFolderNameFromPath(item.key))
                        mediaItems.addAll(item.value)
                        currentFolderList = mediaItems
                        photosAdapter.setList(currentFolderList)
                    }
                }
            } else {
                viewModel.fetchMediaItemsWithFolder()
                viewModel.mediaItemsWithFolder.collectLatest {
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
                    folderList.add(getString(R.string.all_photos))
                    it.forEach { item ->
                        folderList.add(getFolderNameFromPath(item.key))
                        mediaItems.addAll(item.value)
                        currentFolderList = mediaItems
                        photosAdapter.setList(currentFolderList)
                    }
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
        currentFolderList = if (isVideo) {
            if (folderName == getString(R.string.all_videos)) {
                mediaItems
            } else {
                datHashMap[folderName]!!
            }
        } else {
            if (folderName == getString(R.string.all_photos)) {
                mediaItems
            } else {
                datHashMap[folderName]!!
            }
        }

        photosAdapter.selectedList = selectedList
        photosAdapter.setList(currentFolderList)
    }

}